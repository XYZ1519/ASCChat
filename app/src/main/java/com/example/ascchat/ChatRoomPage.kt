package com.example.ascchat

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.chat.AmityChatClient
import com.amity.socialcloud.sdk.chat.channel.AmityChannel
import com.amity.socialcloud.sdk.chat.message.AmityMessage
import com.amity.socialcloud.sdk.core.AmityTags
import com.amity.socialcloud.sdk.core.error.AmityError
import com.example.ascchat.messageradapter.ListListener
import com.example.ascchat.messageradapter.MessageAdapter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers

open class ChatRoomPage : AppCompatActivity(), ListListener {

    private var messageAdapter: MessageAdapter? = null
    private val messageRepository = AmityChatClient.newMessageRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room_page)
        title = getChannelTitle()
        initMessageFragment()
        initMessageRepository()
        setUpSendButton()
    }

    private fun initMessageFragment() {
        val recycler = findViewById<RecyclerView>(R.id.content_recycler)
        messageAdapter = MessageAdapter(this)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = messageAdapter
    }

    private fun initMessageRepository() {
        val channelQuery = getMessageCollection(getChannelId())
        channelQuery.observe(this) {
            if ((messageAdapter?.itemCount ?: Int.MAX_VALUE) < it.size) {
                messageAdapter?.submitList(it)
            }
        }
    }

    private fun setUpSendButton() {
        val message_edittext = findViewById<EditText>(R.id.message)
        val message_send_button = findViewById<Button>(R.id.sendBtn)
        message_edittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                message_send_button.isEnabled = !TextUtils.isEmpty(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // do nothing
            }
        })

        message_send_button.setOnClickListener {
            val text = message_edittext.text.toString().trim()
            message_edittext.text = null
            createTextMessage(text, getChannelId())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { t ->
                    val amityError = AmityError.from(t)
                    if (amityError == AmityError.USER_IS_BANNED) {
                        message_edittext.post {
                            Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                        }
                        message_edittext.postDelayed({ finish() }, 500)
                    }
                }
                .subscribe()

        }
    }

    fun getMessageCollection(channelId: String): LiveData<PagedList<AmityMessage>> {
        return LiveDataReactiveStreams.fromPublisher(
            messageRepository.getMessages(channelId)
                .build()
                .query()
        )
    }

    private fun getChannelId(): String {
        val channel = intent.getParcelableExtra<AmityChannel>(EXTRA_CHANNEL)
        return channel?.getChannelId() ?: ""
    }

    private fun getChannelTitle() : String {
        val channel = intent.getParcelableExtra<AmityChannel>(EXTRA_CHANNEL)
        return channel?.getDisplayName() ?: ""
    }

    fun createTextMessage(text: String, channelId: String): Completable {
        return messageRepository.createMessage(channelId)
            .with()
            .text(text)
            .build()
            .send()
    }

    companion object {
        const val EXTRA_CHANNEL = "extra_channel"
    }

    override fun onItemClick(chatItem: AmityMessage, position: Int, holder: View) {

    }

}