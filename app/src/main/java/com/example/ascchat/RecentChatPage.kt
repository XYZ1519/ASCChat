package com.example.ascchat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.chat.AmityChatClient
import com.amity.socialcloud.sdk.chat.channel.AmityChannel
import com.amity.socialcloud.sdk.chat.channel.AmityChannelFilter
import com.amity.socialcloud.sdk.chat.channel.AmityChannelRepository
import com.example.ascchat.ChatRoomPage.Companion.EXTRA_CHANNEL
import com.example.ascchat.chatadapter.ChatAdapter
import com.example.ascchat.chatadapter.ListListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecentChatPage : AppCompatActivity(), ListListener {
    var chatAdapter: ChatAdapter? = null
    private lateinit var channelRepository: AmityChannelRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_chat_page)

        initChatFragment()
    }

    override fun onResume() {
        super.onResume()
        initChannelRepository()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.channel_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_add) {
            val intent = Intent(this, SearchUserPage::class.java)
            startActivity(intent)
            return true
        }
        if (id == R.id.action_add_image) {
            val intent = Intent(this, MyAvatarUpdateActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
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

    private fun initChannelRepository() {
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

    override fun onItemClick(chatItem: AmityChannel, position: Int, holder: View) {
        val intent = Intent(this, ChatRoomPage::class.java)
        intent.putExtra(EXTRA_CHANNEL, chatItem)
        startActivity(intent)
    }
}