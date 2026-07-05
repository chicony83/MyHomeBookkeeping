package com.chico.myhomebookkeeping.backup

import android.content.Context
import android.net.Uri
import android.preference.PreferenceManager
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.obj.Constants
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object DatabaseBackupManager {
    private const val DATABASE_NAME = "DataBase"
    private const val FORMAT_VERSION = 1
    private const val ITERATIONS = 310_000
    private const val KEY_LENGTH_BITS = 256
    private const val KDF_SHA256: Byte = 1
    private const val KDF_SHA1: Byte = 2
    private val MAGIC = "MHBKBACKUP".toByteArray(Charsets.US_ASCII)
    private val random = SecureRandom()

    fun createEncryptedBackup(context: Context, destination: Uri, password: CharArray) {
        require(password.size >= 8) { "Password must contain at least 8 characters" }

        val temporaryArchive = File.createTempFile("mhbk-backup-", ".zip", context.cacheDir)
        try {
            createArchive(context.applicationContext, temporaryArchive)
            encryptArchive(context, temporaryArchive, destination, password)
        } finally {
            password.fill('\u0000')
            temporaryArchive.delete()
        }
    }

    private fun createArchive(context: Context, target: File) {
        checkpointDatabase(context)
        val database = context.getDatabasePath(DATABASE_NAME)
        check(database.isFile) { "Database file does not exist" }

        ZipOutputStream(BufferedOutputStream(target.outputStream())).use { zip ->
            addFile(zip, database, "database/$DATABASE_NAME")
            listOf("-wal", "-shm").forEach { suffix ->
                val sidecar = File(database.path + suffix)
                if (sidecar.isFile && sidecar.length() > 0) {
                    addFile(zip, sidecar, "database/$DATABASE_NAME$suffix")
                }
            }

            zip.putNextEntry(ZipEntry("manifest.json"))
            zip.write(createManifest(context).toString().toByteArray(Charsets.UTF_8))
            zip.closeEntry()

            zip.putNextEntry(ZipEntry("settings.json"))
            zip.write(createPersistentSettings(context).toString().toByteArray(Charsets.UTF_8))
            zip.closeEntry()
        }
    }

    private fun checkpointDatabase(context: Context) {
        val roomDatabase = dataBase.getDataBase(context)
        try {
            roomDatabase.openHelper.writableDatabase
                .query("PRAGMA wal_checkpoint(FULL)")
                .use { cursor -> while (cursor.moveToNext()) Unit }
        } finally {
            roomDatabase.close()
        }
    }

    private fun createManifest(context: Context): JSONObject {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val roomDatabase = dataBase.getDataBase(context)
        val schemaVersion = try {
            roomDatabase.openHelper.readableDatabase.version
        } finally {
            roomDatabase.close()
        }
        return JSONObject()
            .put("formatVersion", FORMAT_VERSION)
            .put("createdAtUtc", utcTimestamp())
            .put("applicationId", context.packageName.removeSuffix(".debug"))
            .put("appVersionName", packageInfo.versionName ?: "")
            .put("databaseSchemaVersion", schemaVersion)
    }

    private fun createPersistentSettings(context: Context): JSONObject {
        val appPreferences = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
        val calculatorPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return JSONObject()
            .put("sortingCategories", appPreferences.getString(Constants.SORTING_CATEGORIES, null))
            .put("sortingFastPayments", appPreferences.getString(Constants.SORTING_FAST_PAYMENTS, null))
            .put("calculatorHistorySize", calculatorPreferences.getString("HISTORY_SIZE", null))
            .put("calculatorNumberPrecision", calculatorPreferences.getString("NUMBER_PRECISION", null))
            .put(
                "calculatorPreventSleep",
                calculatorPreferences.getBoolean("PREVENT_PHONE_FROM_SLEEPING", false)
            )
    }

    private fun addFile(zip: ZipOutputStream, file: File, entryName: String) {
        zip.putNextEntry(ZipEntry(entryName))
        FileInputStream(file).use { input -> input.copyTo(zip) }
        zip.closeEntry()
    }

    private fun encryptArchive(
        context: Context,
        archive: File,
        destination: Uri,
        password: CharArray
    ) {
        val salt = ByteArray(16).also(random::nextBytes)
        val iv = ByteArray(12).also(random::nextBytes)
        val (kdfId, key) = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        }

        val output = context.contentResolver.openOutputStream(destination, "w")
            ?: error("Cannot open selected file")
        DataOutputStream(BufferedOutputStream(output)).use { header ->
            header.write(MAGIC)
            header.writeInt(FORMAT_VERSION)
            header.writeByte(kdfId.toInt())
            header.writeInt(ITERATIONS)
            header.writeInt(salt.size)
            header.write(salt)
            header.writeInt(iv.size)
            header.write(iv)
            header.flush()

            CipherOutputStream(header, cipher).use { encrypted ->
                FileInputStream(archive).use { input -> input.copyTo(encrypted) }
            }
        }
    }

    private fun deriveKey(password: CharArray, salt: ByteArray): Pair<Byte, SecretKeySpec> {
        val (id, factory) = try {
            KDF_SHA256 to SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        } catch (_: Exception) {
            KDF_SHA1 to SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        }
        val specification = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH_BITS)
        return try {
            id to SecretKeySpec(factory.generateSecret(specification).encoded, "AES")
        } finally {
            specification.clearPassword()
        }
    }

    private fun utcTimestamp(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())
}
