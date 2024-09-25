package com.pob.seeat.presentation.view.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.pob.seeat.R
import com.pob.seeat.databinding.ItemAlarmBinding
import com.pob.seeat.domain.model.AlarmModel
import com.pob.seeat.presentation.common.ViewHolder
import com.pob.seeat.utils.Utils.toKoreanDiffString

class AlarmAdapter(private val onClick: (AlarmModel) -> Unit) :
    ListAdapter<AlarmModel, ViewHolder<AlarmModel>>(object : DiffUtil.ItemCallback<AlarmModel>() {
        override fun areItemsTheSame(oldItem: AlarmModel, newItem: AlarmModel) =
            oldItem.alarmId == newItem.alarmId

        override fun areContentsTheSame(oldItem: AlarmModel, newItem: AlarmModel) =
            oldItem == newItem
    }) {
    class Holder(binding: ItemAlarmBinding) : ViewHolder<AlarmModel>(binding.root) {
        private val container = binding.clAlarmContainer
        private val title = binding.tvAlarmTitle
        private val content = binding.tvAlarmContent
        private val time = binding.tvAlarmTime
        private val image = binding.ivAlarmImage
        private val isNewIcon = binding.tvAlarmIsNew

        override fun bind(
            item: AlarmModel, onClick: (AlarmModel) -> Unit
        ) {
            content.visibility = View.VISIBLE
//            title.text = itemView.context.getString(R.string.alarm_post_title, item.feedTitle)
            title.text = item.feedTitle
            content.text = item.content
            time.text = item.createdAt?.toKoreanDiffString() ?: ""
            isNewIcon.visibility = if (item.isRead) View.GONE else View.VISIBLE
            container.apply {
                val bgColor =
                    itemView.context.getColor(if (item.isRead) R.color.alarm_read else R.color.alarm_unread)
                setBackgroundColor(bgColor)
                setOnClickListener { onClick(item) }
            }
            if (item.feedImage.isEmpty()) image.visibility = View.GONE
            else Glide.with(image.context).load(item.feedImage)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10))).into(image)
        }

        fun getView() = container
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<AlarmModel> {
        return Holder(
            ItemAlarmBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<AlarmModel>, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    fun getAlarmId(position: Int) = getItem(position).alarmId
}