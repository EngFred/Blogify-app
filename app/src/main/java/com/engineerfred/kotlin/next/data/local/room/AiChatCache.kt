package com.engineerfred.kotlin.next.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.engineerfred.kotlin.next.domain.model.AiChat

@Database(
    entities = [AiChat::class],
    version = 1,
    exportSchema = false
)
abstract class AiChatCache() : RoomDatabase() {
    abstract fun getAiChatDao(): AiChatDao
}