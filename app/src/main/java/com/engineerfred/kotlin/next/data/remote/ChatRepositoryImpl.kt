package com.engineerfred.kotlin.next.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.engineerfred.kotlin.next.domain.model.ChatMessage
import com.engineerfred.kotlin.next.domain.model.MessageType
import com.engineerfred.kotlin.next.domain.repository.ChatRepository
import com.engineerfred.kotlin.next.utils.Constants
import com.engineerfred.kotlin.next.utils.Response
import com.engineerfred.kotlin.next.utils.compressImage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ChatRepository {

    companion object {
        const val TAG = "Chat"
    }

    override suspend fun sendChat(message: ChatMessage, context: Context): Response<Any> {
        return try {
            val collectionRefForSender = database.collection(Constants.MESSAGES_COLLECTION)
                .document("${message.senderId}_${message.receiverId}")
                .collection(Constants.CHATS_COLLECTION)
                .document(message.id)

            val collectionRefForReceiver = database.collection(Constants.MESSAGES_COLLECTION)
                .document("${message.receiverId}_${message.senderId}")
                .collection(Constants.CHATS_COLLECTION)
                .document(message.id)

            val storageRef = storage.reference
            val batch = database.batch()

            withContext(NonCancellable) {
                when (message.type) {
                    MessageType.ImageOnly.name, MessageType.ImageWithCaption.name -> {
                        val batchOperation = batch.run {
                            Log.d(TAG, "Compressing message image...")
                            val compressedImage = context.compressImage(message.mediaUrl!!)
                            Log.d(TAG, "Uploading chat image to cloud...")
                            val uploadTask = storageRef.child("${Constants.CHAT_IMAGES_STORAGE}/${message.timeStamp}").putBytes(compressedImage).await()
                            Log.d(TAG, "Downloading the uploaded image uri...")
                            val imageUri = uploadTask.storage.downloadUrl.await()
                            Log.d(TAG, "adding chat message to database...")
                            this.set(
                                collectionRefForSender,
                                message.copy(mediaUrl = "$imageUri")
                            )
                            this.set(
                                collectionRefForReceiver,
                                message.copy(mediaUrl = "$imageUri")
                            )
                        }.commit().await()
                        Log.i(TAG, "Chat message has been sent successfully!!")
                        Response.Success(batchOperation)
                    }
                    MessageType.VideoOnly.name, MessageType.VideoWithCaption.name -> {
                        val batchOperation = batch.run {
                            Log.d(TAG, "Uploading chat video to cloud...")
                            val uploadTask = storageRef.child("${Constants.CHAT_VIDEOS_STORAGE}/${message.timeStamp}").putFile(
                                Uri.parse(message.mediaUrl!!)).await()
                            Log.d(TAG, "Downloading the uploaded chat video uri...")
                            val videoUri = uploadTask.storage.downloadUrl.await()
                            Log.d(TAG, "adding chat message to database...")
                            this.set(
                                collectionRefForSender,
                                message.copy(mediaUrl = "$videoUri")
                            )
                            this.set(
                                collectionRefForReceiver,
                                message.copy(mediaUrl = "$videoUri")
                            )
                        }.commit().await()
                        Log.i(TAG, "Chat message has been sent successfully!!")
                        Response.Success(batchOperation)
                    }
                    else -> {
                        val batchOperation = batch.run {
                            this.set(
                                collectionRefForSender,
                                message
                            )
                            this.set(
                                collectionRefForReceiver,
                                message
                            )
                        }.commit().await()
                        Log.i(TAG, "Chat message has been sent successfully!!")
                        Response.Success(batchOperation)
                    }
                }
            }
        }catch (ex: Exception) {
            Response.Failure("$ex")
        }
    }

    override fun getChatMessages( currentUserId: String, receiverId: String ): Flow<Response<List<ChatMessage>>> {
        return callbackFlow {
            val listenerReg = database.collection(Constants.MESSAGES_COLLECTION)
                .document("${currentUserId}_${receiverId}")
                .collection(Constants.CHATS_COLLECTION)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if ( error != null ) {
                        trySend(Response.Failure("$error"))
                    }
                    if ( value != null ) {
                        val chatMessages = value.toObjects(ChatMessage::class.java)
                        trySend(Response.Success(chatMessages))
                    }
                }
            awaitClose { listenerReg.remove() }
        }.catch {
            emit(Response.Failure("$it"))
        }.flowOn(Dispatchers.IO)
    }
}