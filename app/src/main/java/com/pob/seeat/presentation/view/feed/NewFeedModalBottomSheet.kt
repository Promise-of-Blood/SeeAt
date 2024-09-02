package com.pob.seeat.presentation.view.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pob.seeat.R
import timber.log.Timber

class NewFeedModalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var chipGroup: ChipGroup
    private val maxSelectableChips = 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.new_feed_modal_bottom_sheet, container, false)

        chipGroup = view.findViewById(R.id.chips_group)

        // 각 Chip에 클릭 리스너를 설정
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.setOnClickListener {
                val selectedChipCount = chipGroup.checkedChipIds.size

                if (selectedChipCount > maxSelectableChips) {
                    // 최대 선택 수를 초과한 경우 선택을 해제
                    chip.isChecked = false

                    // 사용자에게 토스트 메시지 표시
                    Toast.makeText(context, "최대 ${maxSelectableChips}개의 태그만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    companion object {
        const val TAG = "BasicBottomModalSheet"
    }
}
