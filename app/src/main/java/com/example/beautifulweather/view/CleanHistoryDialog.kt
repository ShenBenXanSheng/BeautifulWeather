package com.example.beautifulweather.view

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.CancellationSignal
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.beautifulweather.R

class CleanHistoryDialog : Dialog {
    private lateinit var onCleanHistoryListener: OnCleanHistoryListener
    private lateinit var cleanOK: TextView
    private lateinit var cleanGiveUp: TextView

    constructor(context: Context) : this(context, false)

    constructor(context: Context, cancel: Boolean) : this(context, cancel, null)

    constructor(
        context: Context,
        cancel: Boolean,
        onCancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancel, onCancelListener) {
        setCancelable(true)
        val cleanHistoryView =
            LayoutInflater.from(context).inflate(R.layout.dialog_clean_history, null)
        setContentView(cleanHistoryView)
        initView(cleanHistoryView)
        initListener()
    }

    private fun initListener() {
        cleanGiveUp.setOnClickListener {
            dismiss()
        }

        cleanOK.setOnClickListener {
            if (onCleanHistoryListener != null) {
                onCleanHistoryListener.cleanHistoryClick()
            }
            dismiss()
        }
    }

    private fun initView(cleanHistoryView: View?) {
        if (cleanHistoryView != null) {
            cleanOK = cleanHistoryView.findViewById(R.id.dialogCleanOK)
            cleanGiveUp = cleanHistoryView.findViewById(R.id.dialogCleanGiveUp)
        }
    }

    fun setOnCleanHistoryListener(onCleanHistoryListener: OnCleanHistoryListener) {
        this.onCleanHistoryListener = onCleanHistoryListener
    }

    interface OnCleanHistoryListener {
        fun cleanHistoryClick()
    }
}