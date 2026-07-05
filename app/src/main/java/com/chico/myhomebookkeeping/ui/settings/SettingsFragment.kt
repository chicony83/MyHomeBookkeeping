package com.chico.myhomebookkeeping.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.backup.DatabaseBackupManager
import com.chico.myhomebookkeeping.backup.DatabaseRestoreManager
import com.chico.myhomebookkeeping.checks.GetVersionCode
import com.chico.myhomebookkeeping.databinding.FragmentSettingsBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.ui.dialogs.WhatNewInLastVersionDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController
    private val uiHelper = UiHelper()
    private var pendingBackupPassword: CharArray? = null
    private var pendingRestoreUri: Uri? = null
    private val createBackupDocument = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        val password = pendingBackupPassword
        pendingBackupPassword = null
        if (uri == null || password == null) {
            password?.fill('\u0000')
            return@registerForActivityResult
        }
        viewLifecycleOwner.lifecycleScope.launch {
            binding.createBackupButton.isEnabled = false
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    DatabaseBackupManager.createEncryptedBackup(
                        requireContext().applicationContext,
                        uri,
                        password
                    )
                }
            }
            binding.createBackupButton.isEnabled = true
            message(
                getString(
                    if (result.isSuccess) R.string.backup_created else R.string.backup_failed
                )
            )
        }
    }
    private val openBackupDocument = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        pendingRestoreUri = uri
        if (uri != null) showRestorePasswordDialog()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        control = findNavController()
        navControlHelper = NavControlHelper(control)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        with(binding) {
//            changePasswordButton.setOnClickListener {
//                navControlHelper.moveToSelectedFragment(R.id.nav_select_password)
//            }
            checkNewVersionButton.setOnClickListener {
                checkNewVersion()
            }
            versionHistoryButton.setOnClickListener {
                WhatNewInLastVersionDialog().show(
                    childFragmentManager,
                    getString(R.string.tag_show_dialog)
                )
            }
            createBackupButton.setOnClickListener {
                showBackupPasswordDialog()
            }
            restoreBackupButton.setOnClickListener {
                openBackupDocument.launch(arrayOf("application/octet-stream", "*/*"))
            }
            submitButton.setOnClickListener {
                message("настройки сохранены")
                navControlHelper.moveToPreviousFragment()
            }
        }
        with(settingsViewModel){
            appVersion.observe(viewLifecycleOwner, {
                binding.appVersion.text = it.toString()
            })
        }

        return binding.root
    }

    private fun checkNewVersion() {
        val getVersionCode = GetVersionCode(requireContext())
        val version: Any = getVersionCode.getNewVersion()
    }

    private fun showBackupPasswordDialog() {
        val padding = (20 * resources.displayMetrics.density).toInt()
        val password = passwordField(R.string.backup_password_hint)
        val repeatedPassword = passwordField(R.string.backup_password_repeat_hint)
        val fields = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, 0, padding, 0)
            addView(password)
            addView(repeatedPassword)
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.backup_password_title)
            .setView(fields)
            .setNegativeButton(R.string.text_on_button_cancel, null)
            .setPositiveButton(R.string.backup_create_button, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val first = password.text.toString()
                val second = repeatedPassword.text.toString()
                when {
                    first.length < 8 -> password.error = getString(R.string.backup_password_too_short)
                    first != second -> repeatedPassword.error = getString(R.string.backup_password_mismatch)
                    else -> {
                        pendingBackupPassword?.fill('\u0000')
                        pendingBackupPassword = first.toCharArray()
                        password.text.clear()
                        repeatedPassword.text.clear()
                        dialog.dismiss()
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                        val version = requireContext().packageManager
                            .getPackageInfo(requireContext().packageName, 0)
                            .versionName ?: "unknown"
                        createBackupDocument.launch(
                            getString(R.string.backup_file_name, version, date)
                        )
                    }
                }
            }
        }
        dialog.show()
    }

    private fun passwordField(hintRes: Int) = EditText(requireContext()).apply {
        hint = getString(hintRes)
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }

    private fun showRestorePasswordDialog() {
        val password = passwordField(R.string.restore_password_hint)
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.restore_password_title)
            .setView(password)
            .setNegativeButton(R.string.text_on_button_cancel) { _, _ ->
                pendingRestoreUri = null
                password.text.clear()
            }
            .setPositiveButton(R.string.text_on_button_submit) { _, _ ->
                val uri = pendingRestoreUri
                pendingRestoreUri = null
                val secret = password.text.toString().toCharArray()
                password.text.clear()
                if (uri != null) stageRestore(uri, secret)
            }
            .show()
    }

    private fun stageRestore(uri: Uri, password: CharArray) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.restoreBackupButton.isEnabled = false
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    DatabaseRestoreManager.stageRestore(
                        requireContext().applicationContext,
                        uri,
                        password
                    )
                }
            }
            binding.restoreBackupButton.isEnabled = true
            if (result.isFailure) {
                message(getString(R.string.restore_failed))
                return@launch
            }
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.restore_ready_title)
                .setMessage(R.string.restore_ready_message)
                .setCancelable(false)
                .setPositiveButton(R.string.text_on_button_submit) { _, _ ->
                    android.os.Process.killProcess(android.os.Process.myPid())
                    exitProcess(0)
                }
                .show()
        }
    }

    private fun message(text: String) {
        Toast.makeText(requireContext(),text,Toast.LENGTH_LONG).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
        pendingBackupPassword?.fill('\u0000')
        pendingBackupPassword = null
        pendingRestoreUri = null
        _binding = null
    }
}
