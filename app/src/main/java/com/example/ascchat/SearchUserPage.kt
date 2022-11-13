package com.example.ascchat

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.AmityCoreClient
import com.amity.socialcloud.sdk.chat.AmityChatClient
import com.amity.socialcloud.sdk.chat.channel.AmityChannelRepository
import com.amity.socialcloud.sdk.core.user.AmityUser
import com.amity.socialcloud.sdk.core.user.AmityUserSortOption
import com.example.ascchat.useradapter.UserListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class SearchUserPage : AppCompatActivity() {

    private val userRepository = AmityCoreClient.newUserRepository()
    private var channelRepository: AmityChannelRepository = AmityChatClient.newChannelRepository()
    private var users: LiveData<PagedList<AmityUser>>? = null
    private val adapter = UserListAdapter()
    private var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user_page)
        setupAdapter()
        setupListener()
        initView()
    }

    private fun setupAdapter() {
        val user_list_recyclerview = findViewById<RecyclerView>(R.id.userListRecycler)
        user_list_recyclerview.adapter = adapter
    }

    private fun initView() {
        findViewById<Button>(R.id.searchBtn).setOnClickListener {
            keyword = findViewById<TextView>(R.id.userID).text.toString()
            observeUserCollection()
        }
    }

    private fun setupListener() {
        val disposable = adapter.onClickFlowable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                createConversationChannel(
                    channelRepository,
                    it.getUserId(),
                    it.getDisplayName() ?: it.getUserId()
                )
            }, { Toast.makeText(applicationContext, it.message ?: "", Toast.LENGTH_SHORT).show() })
    }

    private fun observeUserCollection() {
        users?.removeObservers(this)
        users = getUsersLiveData()
        users?.observe(this) { adapter.submitList(it) }
    }

    private fun getUsersLiveData(): LiveData<PagedList<AmityUser>> {
        return LiveDataReactiveStreams.fromPublisher(
            userRepository.searchUserByDisplayName(keyword)
                .sortBy(AmityUserSortOption.DISPLAYNAME)
                .build()
                .query()
        )
    }

    fun createConversationChannel(
        channelRepository: AmityChannelRepository,
        userId: String,
        userName: String
    ) {
        // create channel and let SDK handle channelId generation
        channelRepository.createChannel()
            .conversationType()
            .withUserId(userId = userId)
            .displayName(displayName = userName) // optional
            .build()
            .create()
            .doOnError {
                Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
            }
            .doOnSuccess {
                finish()
            }
            .subscribe()
    }

    fun createConversationChannel(
        channelRepository: AmityChannelRepository,
        userIds: List<String>,
        displayName: String
    ) {
        // create channel and let SDK handle channelId generation
        channelRepository.createChannel()
            .conversationType()
            .withUserIds(userIds = userIds)
            .displayName(displayName = displayName) // optional
            .build()
            .create()
            .doOnError {
                Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
            }
            .doOnSuccess {
                finish()
            }
            .subscribe()
    }

}