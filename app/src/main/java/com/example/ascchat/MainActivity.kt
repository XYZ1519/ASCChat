package com.example.ascchat

import android.content.ClipData.newIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amity.socialcloud.sdk.AmityCoreClient
import com.amity.socialcloud.sdk.AmityEndpoint
import com.amity.socialcloud.sdk.chat.AmityChatClient
import com.amity.socialcloud.sdk.chat.channel.AmityChannel
import com.amity.socialcloud.sdk.chat.channel.AmityChannelRepository
import com.amity.socialcloud.sdk.core.AmityTags
import com.google.gson.JsonObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AmityCoreClient.setup(
            apiKey = "b0efed5d69d3f3614862844d065a1289d95bdab7bb313e78",
            endpoint = AmityEndpoint.EU // optional param, defaulted as SG region
        )
        authenticate()
    }
    fun authenticate() {
        AmityCoreClient.login(userId = "userId 2")
            .displayName(displayName = "xyz") // optional
            .authToken(authToken = "token") // optional
            .build()
            .submit()
            .doOnComplete {
                val intent = Intent(this,RecentChatPage::class.java)
                startActivity(intent)
                //success
            }
            .subscribe()
    }
}