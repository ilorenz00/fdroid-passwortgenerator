package com.lorenzl5.hermes.passwort.dialogs

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.lorenzl5.hermes.passwort.R
import com.lorenzl5.hermes.passwort.database.MetaData
import com.lorenzl5.hermes.passwort.database.MetaDataSQLiteHelper
import com.lorenzl5.hermes.passwort.generator.PasswordGenerator
import com.lorenzl5.hermes.passwort.helpers.PreferenceKeys
import java.util.concurrent.Executors

class GeneratePasswordDialog(
    private val meta: MetaData,
    private val db: MetaDataSQLiteHelper,
    private val onDone: () -> Unit = {}
) : DialogFragment() {

    private var masterPassword: String = ""
    private var generatedPassword: String = ""
    private var visibility = true
    private var lastGenerated: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_generate, null)

        val accountName = view.findViewById<TextView>(R.id.dialog_account_name)
        val usernameLabel = view.findViewById<TextView>(R.id.dialog_username_label)
        val username = view.findViewById<TextView>(R.id.dialog_username)
        val versionLabel = view.findViewById<TextView>(R.id.dialog_version_label)
        val version = view.findViewById<TextView>(R.id.dialog_version)
        val masterPasswordField = view.findViewById<EditText>(R.id.editText_masterpassword)
        val passwordText = view.findViewById<TextView>(R.id.textPassword)
        val copyButton = view.findViewById<TextView>(R.id.btn_copy)
        val generateButton = view.findViewById<Button>(R.id.btn_generate)
        val doneButton = view.findViewById<Button>(R.id.btn_done)
        val visibilityButton = view.findViewById<TextView>(R.id.btn_visibility)

        accountName.text = meta.domain
        if (meta.username.isEmpty()) {
            usernameLabel.visibility = View.GONE
            username.visibility = View.GONE
        } else {
            username.text = meta.username
        }
        version.text = meta.iteration.toString()

        // Version visibility
        if (meta.iteration > 1) {
            versionLabel.visibility = View.VISIBLE
            version.visibility = View.VISIBLE
        }

        visibilityButton.setOnClickListener {
            visibility = !visibility
            masterPasswordField.transformationMethod = if (visibility)
                null else android.text.method.PasswordTransformationMethod.getInstance()
        }

        copyButton.setOnClickListener {
            copyToClipboard(context, generatedPassword)
        }

        generateButton.setOnClickListener {
            masterPassword = masterPasswordField.text.toString()
            if (masterPassword.isEmpty()) return@setOnClickListener

            // Show progress
            val progressDialog = AlertDialog.Builder(context)
                .setView(R.layout.dialog_generating)
                .setCancelable(false)
                .create()
            progressDialog.show()

            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                val deviceId = PreferenceKeys.getDeviceId(context)
                val bcryptCost = PreferenceKeys.getBcryptCost(context)
                val generator = PasswordGenerator(
                    meta.domain,
                    meta.username,
                    masterPassword,
                    deviceId,
                    meta.iteration,
                    10000, // hashIterations
                    "SHA256",
                    bcryptCost
                )
                generatedPassword = generator.getPassword(
                    meta.hasSymbols, meta.hasLettersLow,
                    meta.hasLettersUp, meta.hasNumbers,
                    meta.length
                )
                try {
                    generator.deleteFinalize()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

                activity?.runOnUiThread {
                    progressDialog.dismiss()
                    lastGenerated = generatedPassword
                    passwordText.text = generatedPassword
                    passwordText.visibility = View.VISIBLE
                }
            }
        }

        doneButton.setOnClickListener {
            onDone()
            dismiss()
        }

        return AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(true)
            .create()
    }

    private fun copyToClipboard(context: Context, text: String) {
        if (!PreferenceKeys.isClipboardEnabled(context)) return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("password", text)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(
            (activity as? android.app.Activity)?.findViewById(android.R.id.content)
                ?: view ?: return,
            R.string.copied_to_clipboard,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}
