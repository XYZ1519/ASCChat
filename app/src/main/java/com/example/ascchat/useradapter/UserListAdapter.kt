package com.example.ascchat.useradapter

import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.core.user.AmityUser
import com.amity.socialcloud.sdk.extension.adapter.AmityUserAdapter
import com.example.ascchat.R
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject


class UserListAdapter : AmityUserAdapter<UserListAdapter.UserViewHolder>() {

    private val onClickSubject = PublishSubject.create<AmityUser>()

    val onClickFlowable: Flowable<AmityUser>
        get() = onClickSubject.toFlowable(BackpressureStrategy.BUFFER)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bindView(user, onClickSubject::onNext)
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(user : AmityUser?,onClickListener: (AmityUser) -> Unit = {}) {
            val userTextview = itemView.findViewById<TextView>(R.id.user_textview)
            if (user == null) {
                userTextview.text = "loading..."
            } else {
                val text = StringBuilder()
                    .append("id: ")
                    .append(user.getUserId())
                    .append("\ndisplayname: ")
                    .append(user.getDisplayName())
                    .append("\nflag count: ")
                    .append(user.getFlagCount())
                    .append("\nmetadata: ")
                    .append(user.getMetadata().toString())
                    .append("\navatarURL: ")
                    .append(user.getAvatar()?.getUrl())
                    .append("\navatarCustomURL: ")
                    .append(user.getAvatarCustomUrl())
                    .append("\ndescription: ")
                    .append(user.getDescription())
                    .append("\ncreatedAt: ")
                    .append(user.getCreatedAt().toString())
                    .toString()
                userTextview.text = text
                itemView.setOnClickListener { onClickListener.invoke(user) }
            }
        }
    }
}
