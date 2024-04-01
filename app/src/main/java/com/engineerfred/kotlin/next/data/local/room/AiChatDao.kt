package com.engineerfred.kotlin.next.data.local.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.engineerfred.kotlin.next.domain.model.AiChat
import kotlinx.coroutines.flow.Flow

@Dao
interface AiChatDao {

    @Upsert
    suspend fun addChat( chat: AiChat )

    @Query("Select * from AiChat")
    fun getChats(): Flow<List<AiChat>>

    @Query("Delete from AiChat where id=:chatId")
    suspend fun deleteChat(chatId: String)

    @Query("DELETE FROM aichat")
    suspend fun deleteAllChats()

}