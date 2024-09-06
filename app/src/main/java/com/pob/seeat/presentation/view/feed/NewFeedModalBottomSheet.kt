package com.pob.seeat.presentation.view.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pob.seeat.R
import com.pob.seeat.domain.model.TagModel
import com.pob.seeat.presentation.viewmodel.NewFeedViewModel
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.tagList
import timber.log.Timber

class NewFeedModalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var chipGroup: ChipGroup
    private val maxSelectableChips = 3

    private val tagLists = tagList.drop(1)

    private val viewModel: NewFeedViewModel by activityViewModels()

    private lateinit var selectedTags: MutableList<TagModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.new_feed_modal_bottom_sheet, container, false)

        selectedTags = viewModel.selectTagList.value.toMutableList()

        chipGroup = view.findViewById(R.id.chips_group)

        // tagList를 이용해 Chip을 동적으로 생성
        for (tag in tagLists) {
            val chip = Chip(context).apply {
                text = tag.tagName
                setChipIconResource(tag.tagImage)

                chipBackgroundColor = ContextCompat.getColorStateList(
                    context,
                    R.color.background_chip_background_selector
                )
                chipStrokeWidth = 0f

                chipIconSize = 16f.px.toFloat()
                chipCornerRadius = 32f.px.toFloat()
                chipStartPadding = 10f.px.toFloat()

                textSize = 14f

                elevation = 1f.px.toFloat()

                isCheckable = true
                isClickable = true
            }

            chip.isChecked = tag in selectedTags

            // 클릭 리스너를 설정하여 선택 가능한 Chip 개수를 제한
            chip.setOnClickListener {
                val selectedChipCount = chipGroup.checkedChipIds.size

                if (selectedChipCount > maxSelectableChips) {
                    // 최대 선택 수를 초과한 경우 선택을 해제
                    chip.isChecked = false

                    // 사용자에게 토스트 메시지 표시
                    Toast.makeText(
                        context,
                        "최대 ${maxSelectableChips}개의 태그만 선택할 수 있습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Tag가 selectedTags에 존재하는지 확인
                    if (selectedTags.contains(tag)) {
                        selectedTags.remove(tag)
                    } else {
                        selectedTags.add(tag)
                    }

                }

            }
            // ChipGroup에 동적으로 Chip 추가
            chipGroup.addView(chip)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.tag("NewFeedModalBottomSheet").d("onDestroyView")
        Timber.tag("NewFeedModalBottomSheet").d(selectedTags.toString())
        viewModel.updateSelectTagList(selectedTags)
    }


    companion object {
        const val TAG = "BasicBottomModalSheet"
    }
}
