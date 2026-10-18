package com.lorenzl5.hermes.passwort.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.lorenzl5.hermes.passwort.R
import com.lorenzl5.hermes.passwort.database.MetaData
import com.lorenzl5.hermes.passwort.database.MetaDataSQLiteHelper

class UpdateMetadataDialog(
    private val oldMeta: MetaData,
    private val db: MetaDataSQLiteHelper,
    private val onSaved: () -> Unit = {}
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_update, null)

        val versionBar = view.findViewById<SeekBar>(R.id.seekbar_version)
        val versionText = view.findViewById<TextView>(R.id.textVersion)
        val usernameField = view.findViewById<EditText>(R.id.editText_username)
        val lowerCb = view.findViewById<CheckBox>(R.id.checkbox_lower)
        val upperCb = view.findViewById<CheckBox>(R.id.checkbox_upper)
        val numbersCb = view.findViewById<CheckBox>(R.id.checkbox_numbers)
        val specialCb = view.findViewById<CheckBox>(R.id.checkbox_special)
        val lengthBar = view.findViewById<SeekBar>(R.id.seekbar_length)
        val cancelButton = view.findViewById<Button>(R.id.btn_cancel)
        val saveButton = view.findViewById<Button>(R.id.btn_save)

        // Pre-fill
        versionBar.progress = (oldMeta.iteration - 1).coerceIn(0, 9)
        versionText.text = oldMeta.iteration.toString()
        usernameField.setText(oldMeta.username)
        lowerCb.isChecked = oldMeta.hasLettersLow == 1
        upperCb.isChecked = oldMeta.hasLettersUp == 1
        numbersCb.isChecked = oldMeta.hasNumbers == 1
        specialCb.isChecked = oldMeta.hasSymbols == 1
        lengthBar.progress = oldMeta.length

        versionBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                versionText.text = (progress + 1).toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        cancelButton.setOnClickListener { dismiss() }

        saveButton.setOnClickListener {
            val newMeta = MetaData(
                id = oldMeta.id,
                domain = oldMeta.domain,
                username = usernameField.text.toString().trim(),
                hasLettersLow = if (lowerCb.isChecked) 1 else 0,
                hasLettersUp = if (upperCb.isChecked) 1 else 0,
                hasNumbers = if (numbersCb.isChecked) 1 else 0,
                hasSymbols = if (specialCb.isChecked) 1 else 0,
                length = lengthBar.progress,
                iteration = versionBar.progress + 1,
                positionId = oldMeta.positionId
            )
            db.updateAccount(newMeta)
            onSaved()
            dismiss()
        }

        return AlertDialog.Builder(context)
            .setTitle(R.string.update_account_title)
            .setView(view)
            .create()
    }
}
