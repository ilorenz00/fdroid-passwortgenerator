package com.lorenzl5.hermes.passwort.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.lorenzl5.hermes.passwort.R
import com.lorenzl5.hermes.passwort.database.MetaDataSQLiteHelper

class AddMetaDataDialog(
    private val onAdded: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add, null)

        val domainField = view.findViewById<EditText>(R.id.editText_domain)
        val usernameField = view.findViewById<EditText>(R.id.editText_username)
        val lowerCb = view.findViewById<CheckBox>(R.id.checkbox_lower)
        val upperCb = view.findViewById<CheckBox>(R.id.checkbox_upper)
        val numbersCb = view.findViewById<CheckBox>(R.id.checkbox_numbers)
        val specialCb = view.findViewById<CheckBox>(R.id.checkbox_special)
        val lengthBar = view.findViewById<SeekBar>(R.id.seekbar_length)
        val addButton = view.findViewById<Button>(R.id.btn_add)

        addButton.setOnClickListener {
            val domain = domainField.text.toString().trim()
            if (domain.isEmpty()) {
                Toast.makeText(context, R.string.domain_hint, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = MetaDataSQLiteHelper(context)
            db.addAccount(
                domain = domain,
                username = usernameField.text.toString().trim(),
                hasLow = if (lowerCb.isChecked) 1 else 0,
                hasUp = if (upperCb.isChecked) 1 else 0,
                hasNum = if (numbersCb.isChecked) 1 else 0,
                hasSym = if (specialCb.isChecked) 1 else 0,
                length = lengthBar.progress,
                iteration = 1
            )
            onAdded()
            dismiss()
        }

        return AlertDialog.Builder(context)
            .setTitle(R.string.add_account_title)
            .setView(view)
            .create()
    }
}
