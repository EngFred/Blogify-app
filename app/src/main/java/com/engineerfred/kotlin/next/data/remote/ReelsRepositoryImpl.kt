package com.engineerfred.kotlin.next.data.remote

import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.domain.repository.ReelsRepository
import com.engineerfred.kotlin.next.utils.Response
import com.engineerfred.kotlin.next.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReelsRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore
) : ReelsRepository {
    override fun getReels(): Flow<Response<List<Post>>> {
        return flow {
            emit(Response.Undefined)
            val dbTask = database.collection(Constants.REELS_COLLECTION).get().await()
            val videos = dbTask.toObjects(Post::class.java)
            emit(Response.Success(videos))
        }.flowOn(Dispatchers.IO)
            .catch {
                emit(Response.Failure("$it"))
            }
    }
}