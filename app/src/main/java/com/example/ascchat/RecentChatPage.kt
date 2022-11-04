package com.example.ascchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.chat.AmityChatClient
import com.amity.socialcloud.sdk.chat.channel.AmityChannel
import com.amity.socialcloud.sdk.chat.channel.AmityChannelFilter
import com.amity.socialcloud.sdk.chat.channel.AmityChannelRepository
import com.amity.socialcloud.sdk.core.AmityTags
import com.example.ascchat.chatadapter.ChatAdapter
import com.example.ascchat.chatadapter.ListListener
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecentChatPage : AppCompatActivity(), ListListener {
    var chatAdapter: ChatAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_chat_page)
        findViewById<TextView>(R.id.channelID).text = "UserId1"

        initChatFragment()
        initChannelRepository()
    }

    private fun initChatFragment() {
        val recycler = findViewById<RecyclerView>(R.id.channelListRecycler)
        chatAdapter = ChatAdapter(this)
        recycler.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
            adapter = chatAdapter
            onFlingListener = null
        }
    }

    fun initChannelRepository() {
        val channelRepository = AmityChatClient.newChannelRepository()
        initChannel(channelRepository)
       // createConversationChannel(channelRepository)
    }

    private fun initChannel(channelRepository: AmityChannelRepository) {
        val channelQuery = getChannelCollection(channelRepository)
        channelQuery.observe(this, Observer {
            if ((chatAdapter?.itemCount ?: Int.MAX_VALUE) < it.size) {
                chatAdapter?.submitList(it)
            }
        })
    }


    private fun getChannelCollection(channelRepository: AmityChannelRepository): LiveData<PagedList<AmityChannel>> {
        return LiveDataReactiveStreams.fromPublisher(
            channelRepository.getChannels()
                .all()
                .filter(AmityChannelFilter.MEMBER)
                .build()
                .query()
                .subscribeOn(Schedulers.io())
                .observeOn((AndroidSchedulers.mainThread()))
        )
    }

    fun createConversationChannel(channelRepository: AmityChannelRepository) {
        // create channel and let SDK handle channelId generation
        channelRepository.createChannel()
            .conversationType()
            .withUserId(userId = "UserId1")
            .displayName(displayName = "Chat with my BFF") // optional
            .metadata(metadata = JsonObject()) // optional
            .tags(tags = AmityTags(listOf("friends"))) // optional
            .build()
            .create()
            .doOnSuccess { channel: AmityChannel ->
                //success
            }
            .subscribe()
    }

    override fun onItemClick(chatItem: AmityChannel, position: Int, holder: View) {
        Log.e("onClick", chatItem.getChannelId())
    }
}