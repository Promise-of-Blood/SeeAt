package com.pob.seeat.presentation.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder<T>(root: View) : RecyclerView.ViewHolder(root) {
    abstract fun bind(item: T, onClick: (T) -> Unit = {})
}