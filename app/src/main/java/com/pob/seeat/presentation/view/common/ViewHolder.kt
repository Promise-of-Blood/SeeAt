package com.pob.seeat.presentation.view.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder<T>(root: View) : RecyclerView.ViewHolder(root) {
    abstract fun onBind(item: T)
}