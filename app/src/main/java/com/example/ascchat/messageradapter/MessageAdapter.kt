package com.example.ascchat.messageradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import butterknife.Setter
import com.amity.socialcloud.sdk.chat.message.AmityMessage
import com.amity.socialcloud.sdk.extension.adapter.AmityMessageAdapter
import com.example.ascchat.R

class MessageAdapter(
    private val listener: ListListener
) : AmityMessageAdapter<RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<AmityMessage>() {

    override fun areItemsTheSame(oldItem: AmityMessage, newItem: AmityMessage): Boolean {
        return oldItem.getMessageId() == newItem.getMessageId()
    }

    override fun areContentsTheSame(oldItem: AmityMessage, newItem: AmityMessage): Boolean {
        return oldItem.getMessageId() == newItem.getMessageId()
                && oldItem.getCreatedAt() == newItem.getCreatedAt()
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.message_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val channel = getItem(position)
        val visibility = Setter { view: View, value: Int?, index: Int -> view.visibility = value!! }
        if (channel == null) {
            renderLoadingItem(holder as MessageViewHolder, visibility, position)
        } else {
            (holder as MessageViewHolder).bind(channel)
            addOnClickListener(channel, holder, position)
        }
    }

    private fun renderLoadingItem(
        holder: MessageViewHolder,
        visibility: Setter<View, Int?>,
        position: Int
    ) {
        //no need to add
    }

    private fun addOnClickListener(
        message: AmityMessage,
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        holder.itemView
            .setOnClickListener {
                listener.onItemClick(
                    message,
                    holder.adapterPosition,
                    holder.itemView
                )
            }
    }
}