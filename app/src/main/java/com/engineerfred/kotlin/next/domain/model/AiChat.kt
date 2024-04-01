package com.engineerfred.kotlin.next.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class AiChat(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val prompt: String,
    val imageUrl: String?,
    val isFromUser: Boolean
)
