package com.pob.seeat.utils.dialog

import android.app.Dialog
import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.pob.seeat.R

object Dialog {

    fun showCommentDialog(context: Context, onDelete: () -> Unit) {
        val dialogView: View =
            LayoutInflater.from(context).inflate(R.layout.my_comment_dialog, null)

        val dialog = Dialog(context)
        dialog.setContentView(dialogView)

        val deleteButton = dialogView.findViewById<TextView>(R.id.btn_delete)
//        val editButton = dialogView.findViewById<TextView>(R.id.btn_edit)

        deleteButton.setOnClickListener {
            onDelete()
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

    fun showProfileConfirmDialog(context: Context, onConfirm: () -> Unit){
        val dialogView : View = LayoutInflater.from(context).inflate(R.layout.confirm_dialog, null)

        val dialog = Dialog(context)
        dialog.setContentView(dialogView)

        val cancelBtn = dialogView.findViewById<Button>(R.id.btn_cancel)
        val confirmBtn = dialogView.findViewById<Button>(R.id.btn_confirm)

        val window = dialog.window
        window?.setLayout(1000,615)

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        confirmBtn.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }
        dialog.show()
    }


}