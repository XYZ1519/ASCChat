package com.example.ascchat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amity.socialcloud.sdk.AmityCoreClient
import com.amity.socialcloud.sdk.AmityEndpoint

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
        AmityCoreClient.login(userId = "userId 6")
            .displayName(displayName = "Jose Miguel") // optional
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