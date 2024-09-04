package com.pob.seeat.presentation.view.home

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pob.seeat.R
import com.pob.seeat.domain.model.TagModel

class TagAdapter(private val tagList: List<TagModel>) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    private var itemClickListener: OnItemClickListener? = null

    private var selectedPosition = RecyclerView.NO_POSITION

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tagList[position]
        holder.bind(tag)

        // 선택된 항목의 backgroundTint 변경
        val itemLayout = holder.itemView.findViewById<ConstraintLayout>(R.id.item_tag_layout)
        val color = if (position == selectedPosition) {
            ContextCompat.getColor(holder.itemView.context, R.color.tertiary)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.white)
        }
        itemLayout.backgroundTintList = ColorStateList.valueOf(color)

        holder.itemView.setOnClickListener {
            // 선택된 위치를 업데이트하고 RecyclerView를 갱신
            if (position == selectedPosition) {
                // 동일한 항목을 다시 클릭한 경우 선택 해제
                selectedPosition = RecyclerView.NO_POSITION
            } else {
                // 다른 항목을 클릭한 경우 선택된 위치를 업데이트
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition)
            }

            notifyItemChanged(position) // 현재 선택된 항목 갱신

            // 클릭 리스너 호출
            itemClickListener?.onItemClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tagImage: ImageView = itemView.findViewById(R.id.iv_tag_icon)
        private val tagName: TextView = itemView.findViewById(R.id.tv_tag_name)

        fun bind(tag: TagModel) {
            tagImage.setImageResource(tag.tagImage)
            tagName.text = tag.tagName
        }
    }
}


