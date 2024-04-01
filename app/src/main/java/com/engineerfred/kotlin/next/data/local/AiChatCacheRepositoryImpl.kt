package com.engineerfred.kotlin.next.data.local

import android.util.Log
import com.engineerfred.kotlin.next.data.local.room.AiChatDao
import com.engineerfred.kotlin.next.domain.model.AiChat
import com.engineerfred.kotlin.next.domain.repository.AiChatCacheRepository
import com.engineerfred.kotlin.next.utils.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AiChatCacheRepositoryImpl @Inject constructor(
    private val cache: AiChatDao
) : AiChatCacheRepository {

    companion object {
        const val TAG = "AiChatCacheRepository"
    }

    override suspend fun addAiChat(aiChat: AiChat): Response<Any> {
        return try {
            val task = cache.addChat(aiChat)
            Response.Success(task)
        } catch ( ex: Exception ) {
            Log.d(TAG, "Error adding chat in cache: $ex")
            Response.Failure("$ex")
        }
    }

    override suspend fun deleteAiChat(aiChatId: String): Response<Any> {
        return try {
            val task = cache.deleteChat(aiChatId)
            Response.Success(task)
        } catch ( ex: Exception ) {
            Log.d(TAG, "Error deleting chat in cache: $ex")
            Response.Failure("$ex")
        }
    }

    override fun getAllAiChatsInCache(): Flow<Response<List<AiChat>>> {
        return channelFlow {
            send(Response.Undefined)
             cache.getChats().collectLatest {
                 send(Response.Success(it))
             }
        }.catch {
            Log.d(TAG, "Error getting chats from cache: $it")
            emit(Response.Failure("$it"))
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun deleteAllChatsWithAi(): Response<Any> {
        return try {
            withContext(NonCancellable){
                val task = cache.deleteAllChats()
                Response.Success(task)
            }
        } catch ( ex: Exception ) {
            Log.d(TAG, "Error deleting chat in cache: $ex")
            Response.Failure("$ex")
        }
    }
}