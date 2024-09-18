package com.raybit.newvendor.ui.fragment.chatModule

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView

import com.raybit.newvendor.ui.adapter.ConversationsAdapter
import com.raybit.newvendor.ui.viewModel.HomeViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.pawegio.kandroid.toast
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.fragment.BaseContainerFragment
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.FirebaseConversation
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.LayoutChatlistFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ConversationFragment : BaseContainerFragment<LayoutChatlistFragmentBinding>() {
    private var data: java.util.ArrayList<FirebaseConversation>? = null
    private val conversations = ArrayList<FirebaseConversation>()
    private var userData: LoginResponse? = null
    private lateinit var binding: LayoutChatlistFragmentBinding
    private lateinit var adapter: ConversationsAdapter

    private var page = 1
    private var moreData: Boolean = false
    var isQuery = false

    var deletedItemPos = 0
    private lateinit var database: FirebaseDatabase

    @Inject
    lateinit var mDataStoreHelper: DataStoreHelper
    val viewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            mDataStoreHelper.getCurrentUserLoggedIn().collectLatest {
                if (it) {

                }
            }
        }
        bindObserver()
    }

    private fun bindObserver() {
        viewModel.loading.observe(this, Observer {
            binding.swipe?.isRefreshing = it
        })

        viewModel.error.observe(this, Observer {
            requireContext().toast(it.message!!)
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        database = Firebase.database
        listeners()
        setAdapter()
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
//                imProfile.loadImage(it?.profile_pic_url!!)
//                tvName.text=it!!.name
//                tvEmail.text=it.email
                userData = it
            }
        }

        hitApi("")

//        (requireActivity() as HomeActivity)
    }

    private fun listeners() {
       binding. swipe?.setOnRefreshListener {
            page = 1
            isQuery = false
            hitApi("")
        }
        binding. edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (  binding. edtSearch.text.toString().trim().isNotEmpty()) {
                    var test = conversations.filter {
                        var index = if (it.membersNames.indexOf(userData?.name) == 0) 1 else 0
                        it.membersNames[index].toLowerCase().contains(
                            binding. edtSearch.text.toString().trim().toLowerCase()
                        )
                    }
                    conversations.clear()
                    conversations.addAll(test)
                    adapter.notifyDataSetChanged()
                } else {
                    conversations.clear()
                    conversations.addAll(data?: arrayListOf())
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }


    private fun setAdapter() {

//        onRecyclerViewScrolled()
    }

    private fun onRecyclerViewScrolled() {
        binding. rvNotifications.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (moreData && !recyclerView.canScrollVertically(1)) {
                    isQuery = false
                    hitApi("")
                }
            }
        })
    }

    private fun hitApi(query: String) {
        binding. swipe.isRefreshing = false
        if (userData != null) {
            database.getReference("Conversations").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var fbCons = ArrayList<FirebaseConversation>()
                    snapshot.children.forEach {
                        if (it.key.toString().trim().contains(userData?.id.toString())) {
                            Log.e("KEY:: ", it.key!!)
                            var hashMap = it.value as HashMap<*, *>
                            var fbC = FirebaseConversation(
                                hashMap["id"].toString(),
                                (if(hashMap["lastMessageBy"]!=null) (hashMap["lastMessageBy"] as Long).toInt() else 0),
                                hashMap["read"] as Boolean,
                                hashMap["updatedAt"].toString(),
                                hashMap["lastMessageSent"].toString(),
                                hashMap["membersIds"] as List<Int>,
                                hashMap["membersNames"] as List<String>,
                                hashMap["membersProfiles"] as List<String>
                            )
                            fbCons.add(fbC)
                        }
                    }
                    adapter = ConversationsAdapter(this@ConversationFragment, fbCons, userData!!)
                    binding.  rvNotifications?.adapter = adapter

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
//            viewModel.getConversations(userData!!.id)
    }

    fun clickItem(conversation: FirebaseConversation) {
        findNavController().navigate(
            R.id.action_conversation_to_chat,
            bundleOf("DATA" to Gson().toJson(conversation))
        )
    }

    override val layoutResourceId: Int
        get() = R.layout.layout_chatlist_fragment


}