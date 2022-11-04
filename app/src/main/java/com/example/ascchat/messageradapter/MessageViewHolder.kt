package com.example.ascchat.messageradapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.chat.message.AmityMessage
import com.example.ascchat.R

class MessageViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {

    fun bind(item: AmityMessage) {
        itemView.apply {
            addMessageToView(this, item)
        }
    }

    private fun addMessageToView(view: View, item: AmityMessage) {
        view.findViewById<TextView>(R.id.usernameMessageAppTextView).text = item.getUser()?.getDisplayName()

        when (val description = item.getData()) {
            is AmityMessage.Data.TEXT -> {
                if (!item.isDeleted()) {
                    view.findViewById<TextView>(R.id.chatTextMessageAppTextView).text = description.getText()
                } else {
                    view.findViewById<TextView>(R.id.chatTextMessageAppTextView).text ="Message Deleted"
                }
            }
            else -> {}
        }
    }
}