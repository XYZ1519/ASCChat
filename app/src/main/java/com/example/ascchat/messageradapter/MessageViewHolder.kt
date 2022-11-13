package com.example.ascchat.messageradapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.amity.socialcloud.sdk.chat.message.AmityMessage
import com.amity.socialcloud.sdk.core.file.AmityImage
import com.example.ascchat.R

class MessageViewHolder(itemsView: View) : RecyclerView.ViewHolder(itemsView) {

    fun bind(item: AmityMessage) {
        addMessageToView(itemView, item)
    }

    private fun addMessageToView(view: View, item: AmityMessage) {
        view.findViewById<TextView>(R.id.usernameMessageAppTextView).text = item.getUser()?.getDisplayName()
        view.findViewById<ImageView>(R.id.profileImageView).load(item.getUser()?.getAvatar()?.getUrl(AmityImage.Size.MEDIUM))

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