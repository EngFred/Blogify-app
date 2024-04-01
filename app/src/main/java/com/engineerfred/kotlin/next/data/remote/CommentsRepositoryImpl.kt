package com.engineerfred.kotlin.next.data.remote

import android.util.Log
import com.engineerfred.kotlin.next.domain.model.Comment
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.Reply
import com.engineerfred.kotlin.next.domain.repository.CommentsRepository
import com.engineerfred.kotlin.next.utils.Response
import com.engineerfred.kotlin.next.utils.Constants
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentsRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore
) : CommentsRepository {

    companion object {
        private val TAG = CommentsRepositoryImpl::class.java.simpleName
    }

    override suspend fun addComment(comment: Comment, postId: String, notification: Notification?) {
        try {
            val batch = database.batch()
            val commentsCollection = database.collection(Constants.COMMENTS_COLLECTION)
            val postsCollection = database.collection(Constants.POSTS_COLLECTION)
            val notificationsCollection = database.collection(Constants.NOTIFICATIONS_COLLECTION)
            batch.set(
                commentsCollection.document(comment.id),
                comment
            )
            batch.update(
                postsCollection.document(postId),
                mapOf("commentsCount" to FieldValue.increment(1))
            )
            if ( notification != null ) {
                batch.set(
                    notificationsCollection.document(notification.id),
                    notification
                )
            }
            batch.commit().await()
            Log.i(TAG, "Comment added!!")
        } catch ( ex: Exception ) {
            Log.v(TAG, "ERROR commenting on post: $ex")
        }
    }

    override suspend fun addReplyOnComment(
        reply: Reply,
        commentId: String,
        notification: Notification?
    ) {
        try {
            val batch = database.batch()
            val commentsCollection = database.collection(Constants.COMMENTS_COLLECTION)
            val notificationsCollection = database.collection(Constants.NOTIFICATIONS_COLLECTION)
            batch.set(
                commentsCollection.document(commentId).collection(Constants.REPLIES_THREAD_COLLECTION).document(reply.id),
                reply
            )
            batch.update(
                commentsCollection.document(commentId),
                mapOf("repliesCount" to FieldValue.increment(1))
            )
            if ( notification != null ) {
                batch.set(
                    notificationsCollection.document( notification.id ),
                    notification
                )
            }
            batch.commit().await()
            Log.i(TAG, "Reply added!!")
        } catch ( ex: Exception ) {
            Log.v(TAG, "ERROR replying to a post on comment: $ex")
        }
    }

    override fun getPostComments(postId: String): Flow<Response<List<Comment>>> {
        return callbackFlow {
            trySend(Response.Undefined)
            val task = database.collection(Constants.COMMENTS_COLLECTION)
                .whereEqualTo("postId", postId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if ( value != null ) {
                        Log.i("Bae", "Received post comments snapshot(repo).")
                        val postComments = value.toObjects(Comment::class.java)
                        trySend(Response.Success(postComments))
                    }
                    if ( error != null ) {
                        Log.i("Bae", "Theres are snapshot error. $error(repo).")
                        trySend(Response.Failure("Something went wrong!"))
                    }
                }
            awaitClose { task.remove() }
        }.catch {
            Log.v(TAG, "ERROR getting post comments")
            Log.i("Bae", "Error getting post comments: $it.")
            emit(Response.Failure("$it"))
        }.flowOn(Dispatchers.IO)
    }

    override fun getCommentReplies(commentId: String): Flow<Response<List<Reply>>> {
        return callbackFlow {
            trySend(Response.Undefined)
            val task = database.collection(Constants.COMMENTS_COLLECTION)
                .document(commentId)
                .collection(Constants.REPLIES_THREAD_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if ( value != null ) {
                        val commentReplies = value.toObjects(Reply::class.java)
                        trySend(Response.Success(commentReplies))
                    }
                    if ( error != null ) {
                        return@addSnapshotListener
                    }
                }
            awaitClose { task.remove() }
        }.catch {
            Log.v(TAG, "ERROR getting comment replies")
            emit(Response.Failure("$it"))
        }.flowOn(Dispatchers.IO)


    }

    override fun getCommentById(commentId: String): Flow<Response<Comment?>> {
        return callbackFlow {
            trySend(Response.Undefined)
            val task = database.collection(Constants.COMMENTS_COLLECTION)
                .document(commentId)
                .addSnapshotListener { value, error ->
                    if ( value != null ) {
                        val comment = value.toObject(Comment::class.java)
                        trySend(Response.Success(comment))
                    }
                    if ( error != null ) {
                        return@addSnapshotListener
                    }
                }
            awaitClose { task.remove() }
        }.catch {
            Log.v(TAG, "ERROR getting comment")
            emit(Response.Failure("$it"))
        }.flowOn(Dispatchers.IO)
    }
}