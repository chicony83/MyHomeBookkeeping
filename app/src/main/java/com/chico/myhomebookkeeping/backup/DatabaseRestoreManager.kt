package com.chico.myhomebookkeeping.backup

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.chico.myhomebookkeeping.obj.Constants
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object DatabaseRestoreManager {
    private const val DATABASE_NAME = "DataBase"
    private const val FORMAT_VERSION = 1
    private const val KEY_LENGTH_BITS = 256
    private const val KDF_SHA256: Byte = 1
    private const val KDF_SHA1: Byte = 2
    private const val READY_MARKER = ".ready"
    private val MAGIC = "MHBKBACKUP".toByteArray(Charsets.US_ASCII)
    private val requiredTables = setOf(
        "cash_account_table", "category_table", "currency_table", "money_moving_table",
        "fast_payments_table", "parent_categories_table", "icon_resource_table",
        "icon_category_table"
    )
    private val version8Tables = setOf("payment_type_table")

    fun stageRestore(context: Context, source: Uri, password: CharArray) {
        val appContext = context.applicationContext
        val staging = stagingDirectory(appContext)
        staging.deleteRecursively()
        check(staging.mkdirs()) { "Cannot create restore staging directory" }
        val archive = File.createTempFile("mhbk-restore-", ".zip", appContext.cacheDir)
        try {
            decryptBackup(appContext, source, archive, password)
            extractArchive(archive, staging)
            validateStagedBackup(appContext, staging)
            check(File(staging, READY_MARKER).createNewFile()) { "Cannot mark restore as ready" }
        } catch (error: Exception) {
            staging.deleteRecursively()
            throw error
        } finally {
            password.fill('\u0000')
            archive.delete()
        }
    }

    fun applyPendingRestore(context: Context): Boolean {
        val appContext = context.applicationContext
        val staging = stagingDirectory(appContext)
        if (!File(staging, READY_MARKER).isFile) return false
        val databaseDirectory = appContext.getDatabasePath(DATABASE_NAME).parentFile
            ?: error("Database directory is unavailable")
        if (!databaseDirectory.exists()) databaseDirectory.mkdirs()
        val rollback = File(appContext.filesDir, "database-restore-rollback")
        rollback.deleteRecursively()
        check(rollback.mkdirs()) { "Cannot create database rollback directory" }
        val names = listOf(DATABASE_NAME, "$DATABASE_NAME-wal", "$DATABASE_NAME-shm")
        try {
            names.forEach { name ->
                val current = File(databaseDirectory, name)
                if (current.isFile) current.copyTo(File(rollback, name), overwrite = true)
            }
            names.forEach { File(databaseDirectory, it).delete() }
            names.forEach { name ->
                val restored = File(staging, "database/$name")
                if (restored.isFile) restored.copyTo(File(databaseDirectory, name), overwrite = true)
            }
            restorePersistentSettings(appContext, File(staging, "settings.json"))
            staging.deleteRecursively()
            rollback.deleteRecursively()
            return true
        } catch (error: Exception) {
            names.forEach { File(databaseDirectory, it).delete() }
            names.forEach { name ->
                val original = File(rollback, name)
                if (original.isFile) original.copyTo(File(databaseDirectory, name), overwrite = true)
            }
            throw error
        }
    }

    private fun decryptBackup(context: Context, source: Uri, target: File, password: CharArray) {
        val stream = context.contentResolver.openInputStream(source)
            ?: error("Cannot open selected backup")
        DataInputStream(BufferedInputStream(stream)).use { input ->
            val magic = ByteArray(MAGIC.size).also(input::readFully)
            check(magic.contentEquals(MAGIC)) { "Not a MyHomeBookkeeping backup" }
            check(input.readInt() == FORMAT_VERSION) { "Unsupported backup format" }
            val kdfId = input.readByte()
            val iterations = input.readInt()
            check(iterations in 100_000..2_000_000) { "Invalid encryption parameters" }
            val saltSize = input.readInt()
            check(saltSize == 16) { "Invalid salt" }
            val salt = ByteArray(saltSize).also(input::readFully)
            val ivSize = input.readInt()
            check(ivSize == 12) { "Invalid initialization vector" }
            val iv = ByteArray(ivSize).also(input::readFully)
            val key = deriveKey(password, salt, iterations, kdfId)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
                init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
            }
            CipherInputStream(input, cipher).use { decrypted ->
                FileOutputStream(target).use { output -> decrypted.copyTo(output) }
            }
        }
    }

    private fun extractArchive(archive: File, destination: File) {
        ZipInputStream(BufferedInputStream(archive.inputStream())).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val output = File(destination, entry.name)
                check(output.canonicalPath.startsWith(destination.canonicalPath + File.separator)) {
                    "Invalid archive path"
                }
                if (entry.isDirectory) output.mkdirs() else {
                    output.parentFile?.mkdirs()
                    FileOutputStream(output).use { file -> zip.copyTo(file) }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
    }

    private fun validateStagedBackup(context: Context, staging: File) {
        val manifestFile = File(staging, "manifest.json")
        val databaseFile = File(staging, "database/$DATABASE_NAME")
        check(manifestFile.isFile && databaseFile.isFile) { "Backup is incomplete" }
        val manifest = JSONObject(manifestFile.readText(Charsets.UTF_8))
        check(manifest.getInt("formatVersion") == FORMAT_VERSION)
        check(manifest.getString("applicationId") == context.packageName.removeSuffix(".debug"))
        val database = SQLiteDatabase.openDatabase(
            databaseFile.path, null, SQLiteDatabase.OPEN_READWRITE
        )
        try {
            database.rawQuery("PRAGMA integrity_check", null).use { cursor ->
                check(cursor.moveToFirst() && cursor.getString(0).equals("ok", true)) {
                    "Database integrity check failed"
                }
            }
            val databaseVersion = database.version
            check(databaseVersion in 1..8) { "Unsupported database schema" }
            val actualTables = mutableSetOf<String>()
            database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
                .use { cursor -> while (cursor.moveToNext()) actualTables += cursor.getString(0) }
            check(actualTables.containsAll(requiredTables)) { "Database tables are missing" }
            if (databaseVersion >= 8) {
                check(actualTables.containsAll(version8Tables)) { "Database tables are missing" }
            }
        } finally {
            database.close()
        }
    }

    private fun restorePersistentSettings(context: Context, settingsFile: File) {
        val settings = if (settingsFile.isFile) {
            JSONObject(settingsFile.readText(Charsets.UTF_8))
        } else JSONObject()
        context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE).edit().apply {
            putBoolean(Constants.IS_FIRST_LAUNCH, false)
            settings.optString("sortingCategories").takeIf(String::isNotEmpty)
                ?.let { putString(Constants.SORTING_CATEGORIES, it) }
            settings.optString("sortingFastPayments").takeIf(String::isNotEmpty)
                ?.let { putString(Constants.SORTING_FAST_PAYMENTS, it) }
            putBoolean(
                Constants.QUICK_PAYMENT_CURRENCY_SELECTION_SCROLL,
                settings.optBoolean("quickPaymentCurrencySelectionScroll", false)
            )
            putBoolean(
                Constants.QUICK_PAYMENT_CASH_ACCOUNT_SELECTION_SCROLL,
                settings.optBoolean("quickPaymentCashAccountSelectionScroll", false)
            )
            putBoolean(
                Constants.QUICK_PAYMENT_SHOW_CALCULATOR,
                settings.optBoolean("quickPaymentShowCalculator", true)
            )
            settings.optString(
                "quickPaymentAmountInputMode",
                Constants.QUICK_PAYMENT_AMOUNT_INPUT_DIGITS
            ).takeIf(String::isNotEmpty)
                ?.let { putString(Constants.QUICK_PAYMENT_AMOUNT_INPUT_MODE, it) }
            putInt(
                Constants.QUICK_PAYMENT_AMOUNT_WHOLE_DIGITS,
                settings.optInt(
                    "quickPaymentAmountWholeDigits",
                    Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_WHOLE_DIGITS
                )
            )
            putInt(
                Constants.QUICK_PAYMENT_AMOUNT_FRACTION_DIGITS,
                settings.optInt(
                    "quickPaymentAmountFractionDigits",
                    Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_FRACTION_DIGITS
                )
            )
            apply()
        }
    }

    private fun deriveKey(
        password: CharArray, salt: ByteArray, iterations: Int, kdfId: Byte
    ): SecretKeySpec {
        val algorithm = when (kdfId) {
            KDF_SHA256 -> "PBKDF2WithHmacSHA256"
            KDF_SHA1 -> "PBKDF2WithHmacSHA1"
            else -> error("Unsupported key derivation function")
        }
        val specification = PBEKeySpec(password, salt, iterations, KEY_LENGTH_BITS)
        return try {
            SecretKeySpec(
                SecretKeyFactory.getInstance(algorithm).generateSecret(specification).encoded,
                "AES"
            )
        } finally {
            specification.clearPassword()
        }
    }

    private fun stagingDirectory(context: Context) =
        File(context.filesDir, "pending-database-restore")
}
