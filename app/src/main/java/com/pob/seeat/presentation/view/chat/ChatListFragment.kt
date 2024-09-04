package com.pob.seeat.presentation.view.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pob.seeat.databinding.FragmentChatListBinding
import com.pob.seeat.presentation.view.chat.chatlist.adapter.ChatListAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private val chatListAdapter = ChatListAdapter()

    companion object {
        fun newInstance() = ChatListFragment
    }

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.database
        // child 가 아래 속성
        //        val ref = db.getReference("chats").child("base").child("person")
        // 값 설정
        //        ref.child("base").child("person").setValue("상대방 이름(string)")
        val ref = db.getReference("chats")
        val newData = mapOf(
            "test" to "테스트용",
        )

        // 임의의 고정키 이름으로 자식 생성
//        val newChild = ref.push()

        // 이름 지정한 자식 생성
        val newChild = ref.child("mytest")

        newChild.setValue(newData).addOnSuccessListener {
            Timber.d("rtdb success")
        }.addOnFailureListener {
            Timber.d("rtdb fail")
        }

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value
                // 자식 개수 구하기
//                val count = snapshot.childrenCount
                Timber.d("rtdb value : $value")
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.d("rtdb error : ${error.toException()}")
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvChatList.adapter = chatListAdapter
        chatListAdapter.submitList(ChatListDummyData.getDummyList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}