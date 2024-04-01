package com.engineerfred.kotlin.next.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.domain.model.PostType
import com.engineerfred.kotlin.next.domain.repository.PostRepository
import com.engineerfred.kotlin.next.utils.Constants
import com.engineerfred.kotlin.next.utils.Response
import com.engineerfred.kotlin.next.utils.compressImage
import com.google.firebase.firestore.FieldValue
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

class PostRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage
) : PostRepository {

    companion object {
        private val TAG = PostRepositoryImpl::class.simpleName
    }

    override suspend fun addPost( post: Post, context: Context ): Response<Any> {
        return try {
            withContext( NonCancellable ) {
                if ( post.type == PostType.Reel.name ) {
                    val storageRef = storage.reference
                    Log.d(TAG, "Uploading video to cloud...")
                    val uploadTask = storageRef.child("${Constants.VIDEOS_STORAGE}/${post.postedOn}").putFile(Uri.parse(post.videoUrl)).await()
                    Log.d(TAG, "Downloading the uploaded video uri...")
                    val videoUri = uploadTask.storage.downloadUrl.await()
                    Log.d(TAG, "Uploading the reel to database...")
                    val dbTask = database.collection(Constants.REELS_COLLECTION).document(post.id).set(post.copy(videoUrl = "$videoUri")).await()
                    Log.i(TAG, "Video Uploaded successfully!!")
                    Response.Success(dbTask)
                } else {
                    if ( post.imagesCollection.isEmpty() ) {
                        Log.d(TAG, "Uploading post to database...")
                        val dbTask = database.collection(Constants.POSTS_COLLECTION).document(post.id).set(post).await()
                        Log.i(TAG, "Post Uploaded successfully!!")
                        Response.Success(dbTask)
                    } else {
                        val imagesList = ArrayList<String>()
                        val storageRef = storage.reference
                        post.imagesCollection.forEachIndexed{  index, imageUrl ->
                        Log.d(TAG, "Compressing post image ${index + 1}...")
                            val compressedImage = context.compressImage(imageUrl)
                            Log.d(TAG, "Uploading post image to cloud ${index + 1}...")
                            val uploadTask = storageRef.child("${Constants.POST_IMAGES_STORAGE}/${post.postedOn}${index+1}").putBytes(compressedImage).await()
                            Log.d(TAG, "Downloading the uploaded image uri ${index + 1}...")
                            val imageUri = uploadTask.storage.downloadUrl.await()
                            imagesList.add(imageUri.toString())
                        }
                        Log.d(TAG, "Uploading the post to database...")
                        val dbTask = database.collection(Constants.POSTS_COLLECTION).document(post.id).set(post.copy(imagesCollection = imagesList )).await()
                        Log.i(TAG, "Post Uploaded successfully!!")
                        Response.Success(dbTask)
                    }
                }
            }
        } catch ( ex: Exception ) {
            Log.v(TAG, "ERROR Adding post: $ex")
            Response.Failure("$ex")
        }
    }

    override fun getPosts(): Flow<Response<List<Post>>> {
        return callbackFlow {
            trySend(Response.Undefined)
            val task = database.collection(Constants.POSTS_COLLECTION).addSnapshotListener { value, error ->
                if ( value != null ) {
                    val posts = value.toObjects(Post::class.java)
                    trySend(Response.Success(posts))
                }
                if ( error != null ){
                    return@addSnapshotListener
                }
            }
            awaitClose { task.remove() }
        }.catch {
            Log.v(TAG, "ERROR Getting posts: $it")
            emit(Response.Failure("$it"))
        }.flowOn( Dispatchers.IO )
    }

    override fun getUserPosts(userId: String): Flow<Response<MutableList<Post>>> {
        return callbackFlow {
            trySend(Response.Undefined)
            val dbTask = database.collection(Constants.POSTS_COLLECTION)
                .whereEqualTo("ownerId", userId)
                .orderBy("postedOn", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if ( value != null ) {
                        val posts = value.toObjects(Post::class.java)
                        trySend(Response.Success(posts))
                    }
                    if ( error != null ){
                        return@addSnapshotListener
                    }
                }
            awaitClose { dbTask.remove() }
        }.catch {
            Log.v(TAG, "ERROR Getting user posts: $it")
            emit(Response.Failure("$it"))
        }.flowOn( Dispatchers.IO )
    }

    override fun getPostById(postId: String): Flow<Response<Post?>> {
        return callbackFlow {
            trySend(Response.Undefined)
            val dbTask = database.collection(Constants.POSTS_COLLECTION)
                .document(postId)
                .addSnapshotListener { value, error ->
                    if ( value != null ) {
                        val post = value.toObject(Post::class.java)
                        trySend(Response.Success(post))
                    }
                    if ( error != null ){
                        return@addSnapshotListener
                    }
                }
            awaitClose { dbTask.remove() }
        }.catch {
            Log.v(TAG, "ERROR Getting post by id: $it")
            emit(Response.Failure("$it"))
        }.flowOn( Dispatchers.IO )
    }

    override suspend fun deletePostById(postId: String): Response<Any> {
        return try {
            withContext(NonCancellable) {
                val dbTask = database.collection(Constants.POSTS_COLLECTION).document(postId).delete().await()
                Response.Success(dbTask)
            }
        } catch ( ex: Exception ) {
            Log.v(TAG, "ERROR Deleting post: $ex")
            Response.Failure("$ex")
        }
    }

    override suspend fun likePost(userId: String, postId: String, notification: Notification?) {
        try {
            val batch = database.batch()
            withContext(NonCancellable) {
                batch.update(
                    database.collection(Constants.POSTS_COLLECTION).document(postId),
                    mapOf("likedBy" to FieldValue.arrayUnion(userId))
                )
                batch.update(
                    database.collection(Constants.POSTS_COLLECTION).document(postId),
                    mapOf("likesCount" to FieldValue.increment(1))
                )
                if ( notification != null ) {
                    batch.set(
                        database.collection(Constants.NOTIFICATIONS_COLLECTION).document(notification.id),
                        notification
                    )
                }
                val result = batch.commit().await()
                Response.Success(result)
            }
        } catch ( ex: Exception ) {
            Log.v(TAG, "ERROR liking post: $ex")
            Response.Failure("$ex")
        }
    }

    override suspend fun unLikePost(userId: String, postId: String, notificationId: String?) {
        try {
            val batch = database.batch()
            withContext(NonCancellable) {
                batch.update(
                    database.collection(Constants.POSTS_COLLECTION).document(postId),
                    mapOf("likedBy" to FieldValue.arrayRemove(userId))
                )
                batch.update(
                    database.collection(Constants.POSTS_COLLECTION).document(postId),
                    mapOf("likesCount" to FieldValue.increment(-1))
                )
                if ( notificationId != null ) {
                    batch.delete(
                        database.collection(Constants.NOTIFICATIONS_COLLECTION).document(notificationId)
                    )
                }
                val result = batch.commit().await()
                Response.Success(result)
            }
        } catch ( ex: Exception ) {
            Log.v(TAG, "ERROR unLiking post: $ex")
            Response.Failure("$ex")
        }
    }

}