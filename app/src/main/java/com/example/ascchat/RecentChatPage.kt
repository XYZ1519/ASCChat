package com.example.ascchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import com.example.ascchat.ChatRoomPage.Companion.EXTRA_CHANNEL
import com.example.ascchat.chatadapter.ChatAdapter
import com.example.ascchat.chatadapter.ListListener
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecentChatPage : AppCompatActivity(), ListListener {
    var chatAdapter: ChatAdapter? = null
    private lateinit var channelRepository: AmityChannelRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_chat_page)
        val textView = findViewById<TextView>(R.id.channelID)
        textView.text = "UserId1"
        findViewById<Button>(R.id.createBtn).setOnClickListener {
            createConversationChannel(channelRepository, textView.text.toString())
        }

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
        }
    }

    fun initChannelRepository() {
        channelRepository = AmityChatClient.newChannelRepository()
        initChannel(channelRepository)
    }

    private fun initChannel(channelRepository: AmityChannelRepository) {
        val channelQuery = getChannelCollection(channelRepository)
        channelQuery.observe(this) {
            if ((chatAdapter?.itemCount ?: Int.MAX_VALUE) < it.size) {
                chatAdapter?.submitList(it)
            }
        }
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

    fun createConversationChannel(channelRepository: AmityChannelRepository, userId: String) {
        // create channel and let SDK handle channelId generation
        channelRepository.createChannel()
            .conversationType()
            .withUserId(userId = userId)
            .displayName(displayName = userId) // optional
            .tags(tags = AmityTags(listOf("friends"))) // optional
            .build()
            .create()
            .doOnError {
                Toast.makeText(applicationContext,it.message,Toast.LENGTH_SHORT).show()
            }
            .doOnSuccess {
                //success
            }
            .subscribe()
    }

    override fun onItemClick(chatItem: AmityChannel, position: Int, holder: View) {
        val intent = Intent(this, ChatRoomPage::class.java)
        intent.putExtra(EXTRA_CHANNEL, chatItem)
        startActivity(intent)
    }
}