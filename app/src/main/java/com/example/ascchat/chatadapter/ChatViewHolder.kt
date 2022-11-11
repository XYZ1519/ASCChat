package com.example.ascchat.chatadapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.AmityCoreClient
import com.amity.socialcloud.sdk.chat.channel.AmityChannel
import com.example.ascchat.R

class ChatViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {
    var userRepository = AmityCoreClient.newUserRepository()

    fun bind(item: AmityChannel) {
        addMessageToView(itemView, item)
    }

    private fun addMessageToView(view: View, item: AmityChannel) {
        val chatIdTextView = view.findViewById<TextView>(R.id.chId)
        chatIdTextView.text = item.getDisplayName()
        item.getMetadata()?.also { metadataObject ->
            if (metadataObject.getAsJsonArray(USER_IDS) == null) {
                return
            }
            if (metadataObject.getAsJsonArray(USER_IDS).size() <= 1) {
                return
            }
            metadataObject.getAsJsonArray(USER_IDS).map {
                val userId = it.asString
                if(!userId.equals(userRepository.getCurrentUser().blockingFirst().getDisplayName())){
                    chatIdTextView.text = userId
                }
            }
        }
    }

    companion object {
        private const val USER_IDS = "USER_IDS"
    }
}