package com.pob.seeat.presentation.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pob.seeat.R

class TagAdapter(private val tagList: List<Tag>) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {
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

        holder.itemView.setOnClickListener {
            // 기존 선택된 아이템과 현재 선택한 아이템이 다를 경우
            if (position != selectedPosition) {
                // 선택된 위치를 업데이트
                val previousPosition = selectedPosition
                selectedPosition = position

                // 이전에 선택된 아이템과 현재 선택된 아이템을 갱신
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }

            // 아이템이 선택된 경우 배경색을 변경
            if (position == selectedPosition) {
                holder.itemView.setBackgroundColor(R.color.tertiary)
            } else {
                holder.itemView.setBackgroundColor(R.color.white)
            }

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

        fun bind(tag: Tag) {
            tagImage.setImageResource(tag.tagImage)
            tagName.text = tag.tagName
        }
    }
}

class Tag (
    val tagName: String,
    val tagImage: Int,
    val tagColor: Int
)
