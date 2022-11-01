package com.example.ascchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.amity.socialcloud.sdk.chat.AmityChatClient
import com.amity.socialcloud.sdk.chat.channel.AmityChannel
import com.amity.socialcloud.sdk.chat.channel.AmityChannelRepository
import com.amity.socialcloud.sdk.core.AmityTags
import com.google.gson.JsonObject

class RecentChatPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_chat_page)
        findViewById<TextView>(R.id.channelID).text = "UserId1"

        initChannelRepository()

    }

    fun initChannelRepository() {
        val channelRepository = AmityChatClient.newChannelRepository()
        createConversationChannel(channelRepository)
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
}