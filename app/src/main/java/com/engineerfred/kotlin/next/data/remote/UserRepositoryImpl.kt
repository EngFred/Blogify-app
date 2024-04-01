package com.engineerfred.kotlin.next.data.remote

import android.content.Context
import android.util.Log
import com.engineerfred.kotlin.next.domain.model.Comment
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.domain.model.Reply
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.domain.repository.UserRepository
import com.engineerfred.kotlin.next.utils.Response
import com.engineerfred.kotlin.next.utils.Constants
import com.engineerfred.kotlin.next.utils.compressImage
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage
) : UserRepository {

    companion object{
        private val TAG = UserRepositoryImpl::class.java.simpleName
    }

    override fun getUsers(): Flow<Response<List<User>>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserProfilePicture(userId: String, profileImageUrl: String, context: Context): Response<Any> {
        return try {
            withContext(NonCancellable) {
                val usersCollection = database.collection(Constants.USERS_COLLECTION)
                val postsCollection = database.collection(Constants.POSTS_COLLECTION)
                val notificationsCollection = database.collection(Constants.NOTIFICATIONS_COLLECTION)
                val commentsCollection = database.collection(Constants.COMMENTS_COLLECTION)
                val reelsCollection = database.collection(Constants.REELS_COLLECTION)

                val batch = database.batch()
                batch.run {
                    val storageRef = storage.reference
                    Log.d(TAG, "Compressing profile image...")
                    val compressedImage = context.compressImage(profileImageUrl)
                    Log.d(TAG, "Uploading profile image to cloud...")
                    val uploadTask =
                        storageRef.child("${Constants.PROFILE_IMAGES_STORAGE}/${System.currentTimeMillis()}")
                            .putBytes(compressedImage).await()
                    Log.d(TAG, "Downloading the uploaded profile image uri...")
                    val imageUri = uploadTask.storage.downloadUrl.await()
                    Log.d(TAG, "Updating user profile image in database...")
                    this.update(
                        usersCollection.document(userId),
                        mapOf("profileImageUrl" to "$imageUri")
                    )
                    val querySnap1 = postsCollection.whereEqualTo("ownerId", userId).get().await()
                    val userPosts = querySnap1.toObjects(Post::class.java)
                    if ( userPosts.isNotEmpty() ) {
                        Log.d(TAG, "Updating user profile image on his posts in the database...")
                        userPosts.forEach {
                            this.update(
                                postsCollection.document(it.id),
                                mapOf("ownerProfileImageUrl" to imageUri)
                            )
                        }
                    } else {
                        Log.v(TAG, "User has no posts in the database to update yet!!!")
                    }
                    val querySnap5 = reelsCollection.whereEqualTo("ownerId", userId).get().await()
                    val userReels = querySnap5.toObjects(Post::class.java)
                    if ( userReels.isNotEmpty() ) {
                        Log.d(TAG, "Updating user profile image on his reels in the database...")
                        userReels.forEach {
                            this.update(
                                reelsCollection.document(it.id),
                                mapOf("ownerProfileImageUrl" to imageUri)
                            )
                        }
                    }else {
                        Log.v(TAG, "User has no reels in the database to update yet!!!")
                    }

                    val querySnap4 = notificationsCollection.whereEqualTo("fromUserId", userId).get().await()
                    val userNotifications = querySnap4.toObjects(Notification::class.java)
                    if ( userNotifications.isNotEmpty() ) {
                        Log.d(TAG, "Updating user profile image on his notifications in the database...")
                        userNotifications.forEach {
                            this.update(
                                notificationsCollection.document(it.id),
                                mapOf("fromUserProfileImageUrl" to imageUri)
                            )
                        }
                    }else {
                        Log.v(TAG, "User has no notifications created by them in the database to update yet!!!")
                    }

                    val querySnap2 = commentsCollection.whereEqualTo("userId", userId).get().await()
                    val userComments = querySnap2.toObjects(Comment::class.java)
                    if ( userComments.isNotEmpty() ) {
                        Log.d(TAG, "Updating user profile image on his comments on posts in the database...")
                        userComments.forEach { comment ->
                            this.update(
                                commentsCollection.document(comment.id),
                                mapOf("userProfileImageUrl" to imageUri)
                            )
                            val querySnap3 = commentsCollection.document(comment.id).collection(Constants.REPLIES_THREAD_COLLECTION).whereEqualTo("userId", userId).get().await()
                            val userReplies = querySnap3.toObjects(Reply::class.java)
                            if ( userReplies.isNotEmpty() ) {
                                Log.i(TAG, "Updating user profile image on his replies on comments in the database...")
                                userReplies.forEach {
                                    this.update(
                                        commentsCollection.document(comment.id).collection(Constants.REPLIES_THREAD_COLLECTION).document(it.id),
                                        mapOf("userProfileImageUrl" to imageUri)
                                    )
                                }
                            } else {
                                Log.v(TAG, "User has no replies on any comment in the database to update yet!!!")
                            }
                        }
                    } else {
                        Log.v(TAG, "User has no comments on any post in the database to update yet!!!")
                    }
                }
                val result = batch.commit().await()
                Log.i(TAG, "Profile image updated successfully!!")
                Response.Success(result)
            }
        }catch (ex: Exception){
            Response.Failure("$ex")
        }
    }

    override suspend fun updateUserCoverPicture(userId: String, coverImageUrl: String, context: Context): Response<Any> {
        return try {
            withContext(NonCancellable){
                val storageRef = storage.reference
                Log.d(TAG, "Compressing cover image...")
                val compressedImage = context.compressImage(coverImageUrl)
                Log.d(TAG, "Uploading cover image to cloud...")
                val uploadTask = storageRef.child("${Constants.COVER_IMAGES_STORAGE}/${System.currentTimeMillis()}").putBytes(compressedImage).await()
                Log.d(TAG, "Downloading the uploaded cover image uri...")
                val imageUri = uploadTask.storage.downloadUrl.await()
                Log.d(TAG, "Updating user cover image in database...")
                val dbTask = database.collection(Constants.USERS_COLLECTION).document(userId).update("coverImageUrl","$imageUri" ).await()
                Log.i(TAG, "Cover image updated successfully!!")
                Response.Success(dbTask)
            }
        }catch (ex: Exception){
            Response.Failure("$ex")
        }
    }

    override suspend fun updateUserInfo(
        userId: String,
        firstName: String,
        lastName: String,
        about: String?
    ): Response<Any> {
        return try {
            withContext(NonCancellable) {
                val batch = database.batch()
                val usersCollection = database.collection(Constants.USERS_COLLECTION)
                val postsCollection = database.collection(Constants.POSTS_COLLECTION)
                val notificationsCollection = database.collection(Constants.NOTIFICATIONS_COLLECTION)
                val commentsCollection = database.collection(Constants.COMMENTS_COLLECTION)
                val reelsCollection = database.collection(Constants.REELS_COLLECTION)
                batch.run {
                    Log.d(TAG, "Updating user info in database...")
                    val updateMap = mapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "about" to about
                    )
                    batch.update(
                        usersCollection.document(userId),
                        updateMap
                    )
                    val querySnap1 = postsCollection.whereEqualTo("ownerId", userId).get().await()
                    val userPosts = querySnap1.toObjects(Post::class.java)
                    if ( userPosts.isNotEmpty() ) {
                        Log.d(TAG, "Updating user name on his posts in the database (${userPosts.size})...")
                        userPosts.forEach {
                            this.update(
                                postsCollection.document(it.id),
                                mapOf(
                                    "ownerName"  to "$firstName $lastName"
                                )
                            )
                        }
                    } else {
                        Log.v(TAG, "User has no posts in the database to update yet!!!")
                    }
                    val querySnap5 = reelsCollection.whereEqualTo("ownerId", userId).get().await()
                    val userReels = querySnap5.toObjects(Post::class.java)
                    if ( userReels.isNotEmpty() ) {
                        Log.d(TAG, "Updating user name on his reels in the database (${userReels.size})...")
                        userReels.forEach {
                            this.update(
                                reelsCollection.document(it.id),
                                mapOf(  "ownerName"  to "$firstName $lastName" )
                            )
                        }
                    }else {
                        Log.v(TAG, "User has no reels in the database to update yet!!!")
                    }

                    val querySnap4 = notificationsCollection.whereEqualTo("fromUserId", userId).get().await()
                    val userNotifications = querySnap4.toObjects(Notification::class.java)
                    if ( userNotifications.isNotEmpty() ) {
                        Log.d(TAG, "Updating user name on his notifications in the database (${userNotifications.size})...")
                        userNotifications.forEach {
                            this.update(
                                notificationsCollection.document(it.id),
                                mapOf( "fromUserName"  to "$firstName $lastName" )
                            )
                        }
                    }else {
                        Log.v(TAG, "User has no notifications created by them in the database to update yet!!!")
                    }

                    val querySnap2 = commentsCollection.whereEqualTo("userId", userId).get().await()
                    val userComments = querySnap2.toObjects(Comment::class.java)
                    if ( userComments.isNotEmpty() ) {
                        Log.d(TAG, "Updating user profile name on his comments on posts in the database (${userComments.size})...")
                        userComments.forEach { comment ->
                            this.update(
                                commentsCollection.document(comment.id),
                                mapOf("userName" to "$firstName $lastName")
                            )
                            val querySnap3 = commentsCollection.document(comment.id).collection(Constants.REPLIES_THREAD_COLLECTION).whereEqualTo("userId", userId).get().await()
                            val userReplies = querySnap3.toObjects(Reply::class.java)
                            if ( userReplies.isNotEmpty() ) {
                                Log.v(TAG, "Updating user profile name on his replies on comments in the database (${userReplies.size})...")
                                userReplies.forEach {
                                    this.update(
                                        commentsCollection.document(comment.id).collection(Constants.REPLIES_THREAD_COLLECTION).document(it.id),
                                        mapOf("userName" to  "$firstName $lastName")
                                    )
                                }
                            } else {
                                Log.v(TAG, "User has no replies on any comment in the database to update yet!!!")
                            }
                        }
                    } else {
                        Log.v(TAG, "User has no comments on any post in the database to update yet!!!")
                    }
                }
                val result = batch.commit().await()
                Log.i(TAG, "User info has been updated successfully!!")
                Response.Success(result)
            }
        }catch (ex: Exception) {
            Log.e(TAG, "User info has been exception $ex")
            Response.Failure("$ex")
        }
    }

    override suspend fun followUser(
        currentUserId: String,
        otherUserId: String,
        notification: Notification
    ): Response<Any> {
        return try {
            val usersCollection = database.collection(Constants.USERS_COLLECTION)
            val notificationsCollection = database.collection(Constants.NOTIFICATIONS_COLLECTION)
            val batch = database.batch()

            //current user part
            batch.update(
                usersCollection.document(currentUserId),
                mapOf(
                    "following" to FieldValue.arrayUnion(otherUserId),
                    "followingCount" to FieldValue.increment(1)
                )
            )

            //other user's part
            batch.update(
                usersCollection.document(otherUserId),
                mapOf(
                    "followers" to FieldValue.arrayUnion(currentUserId),
                    "followersCount" to FieldValue.increment(1)
                )
            )

            batch.set(
                notificationsCollection.document(notification.id),
                notification
            )
            val result = batch.commit().await()
            Response.Success(result)
        }catch (ex: Exception) {
            Response.Failure("$ex")
        }
    }

    override suspend fun followBackUser(
        currentUserId: String,
        otherUserId: String,
        notification: Notification
    ): Response<Any> {
        return try {
            val usersCollection = database.collection(Constants.USERS_COLLECTION)
            val notificationsCollection = database.collection(Constants.NOTIFICATIONS_COLLECTION)
            val batch = database.batch()

            //current user part
            //friends = currentUser.id in otherUser.followers && otherUser.id in currentUser.followers, //friends
            batch.update(
                usersCollection.document(currentUserId),
                mapOf(
                    "followers" to FieldValue.arrayUnion(otherUserId),
                    "followersCount" to FieldValue.increment(1)
                )
            )

            //other user's part
            batch.update(
                usersCollection.document(otherUserId),
                mapOf(
                    "followers" to FieldValue.arrayUnion(currentUserId),
                    "followersCount" to FieldValue.increment(1)
                )
            )

            batch.set(
                notificationsCollection.document(notification.id),
                notification
            )
            val result = batch.commit().await()
            Response.Success(result)
        }catch (ex: Exception) {
            Response.Failure("$ex")
        }
    }


    override suspend fun unFollowUser(
        currentUserId: String,
        otherUserId: String,
        notificationId: String?
    ): Response<Any> {
        return try {
            val usersCollection = database.collection(Constants.USERS_COLLECTION)
            val notificationsCollection = database.collection(Constants.NOTIFICATIONS_COLLECTION)
            val batch = database.batch()

            //current user's part
            batch.update(
                usersCollection.document(currentUserId),
                mapOf(
                    "following" to FieldValue.arrayRemove(otherUserId),
                    "followingCount" to FieldValue.increment(-1)
                )
            )

            //other user's part
            batch.update(
                usersCollection.document(otherUserId),
                mapOf(
                    "followers" to FieldValue.arrayRemove(currentUserId),
                    "followersCount" to FieldValue.increment(-1)
                )
            )
            if ( notificationId != null ) {
                batch.delete(
                    notificationsCollection.document(notificationId)
                )
            }
            val result = batch.commit().await()
            Response.Success(result)
        }catch (ex: Exception) {
            Response.Failure("$ex")
        }
    }
}