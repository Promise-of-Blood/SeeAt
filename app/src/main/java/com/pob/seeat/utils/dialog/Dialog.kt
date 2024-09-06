package com.pob.seeat.utils.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pob.seeat.R

object Dialog {

    fun showCommentDialog(context: Context, onDelete: () -> Unit, onEdit: () -> Unit) {
        val dialogView: View =
            LayoutInflater.from(context).inflate(R.layout.my_comment_dialog, null)

        val dialog = Dialog(context)
        dialog.setContentView(dialogView)

        val deleteButton = dialogView.findViewById<TextView>(R.id.btn_delete)
        val editButton = dialogView.findViewById<TextView>(R.id.btn_edit)

        deleteButton.setOnClickListener {
            onDelete()
            dialog.dismiss()
        }

        editButton.setOnClickListener {
            onEdit()
            dialog.dismiss()
        }

        dialog.show()

    }

    fun showReportDialog(context: Context, onReport: () -> Unit) {

        val dialogView: View =
            LayoutInflater.from(context).inflate(R.layout.other_comment_dialog, null)

        val dialog = Dialog(context)
        dialog.setContentView(dialogView)

        val reportButton = dialogView.findViewById<TextView>(R.id.btn_report)

        reportButton.setOnClickListener {
            onReport()
            dialog.dismiss()
        }

        dialog.show()
    }
}