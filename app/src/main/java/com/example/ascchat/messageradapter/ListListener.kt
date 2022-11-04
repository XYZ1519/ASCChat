package com.example.ascchat.messageradapter

import android.view.View
import com.amity.socialcloud.sdk.chat.channel.AmityChannel
import com.amity.socialcloud.sdk.chat.message.AmityMessage

interface ListListener {
    fun  onItemClick(chatItem: AmityMessage, position: Int, holder: View)
}