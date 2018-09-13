package com.kyotob.client.entities

import java.sql.Timestamp

data class Message(val id: Int,
                   val senderId: Int,
                   val roomId: Int,
                   val content: String,
                   val created: Timestamp)

data class GetMessageResponse(val createdAt: Timestamp,
                              val userName: String,
                              val userScreenName: String,
                              val content: String)

data class PostMessageRequest(val user_name: String,
                              val content: String)
