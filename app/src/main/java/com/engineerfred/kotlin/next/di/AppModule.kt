package com.engineerfred.kotlin.next.di

import android.app.Application
import android.content.Context
import androidx.annotation.OptIn
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.room.Room
import com.engineerfred.kotlin.next.data.local.AiChatCacheRepositoryImpl
import com.engineerfred.kotlin.next.data.local.room.AiChatCache
import com.engineerfred.kotlin.next.data.local.room.AiChatDao
import com.engineerfred.kotlin.next.data.remote.AuthRepositoryImpl
import com.engineerfred.kotlin.next.data.remote.ChatRepositoryImpl
import com.engineerfred.kotlin.next.data.remote.CommentsRepositoryImpl
import com.engineerfred.kotlin.next.data.remote.GeminiRepositoryImpl
import com.engineerfred.kotlin.next.data.remote.NotificationRepositoryImpl
import com.engineerfred.kotlin.next.data.remote.PostRepositoryImpl
import com.engineerfred.kotlin.next.data.remote.ReelsRepositoryImpl
import com.engineerfred.kotlin.next.data.remote.UserRepositoryImpl
import com.engineerfred.kotlin.next.domain.repository.AiChatCacheRepository
import com.engineerfred.kotlin.next.domain.repository.AuthRepository
import com.engineerfred.kotlin.next.domain.repository.ChatRepository
import com.engineerfred.kotlin.next.domain.repository.CommentsRepository
import com.engineerfred.kotlin.next.domain.repository.GeminiRepository
import com.engineerfred.kotlin.next.domain.repository.NotificationRepository
import com.engineerfred.kotlin.next.domain.repository.PostRepository
import com.engineerfred.kotlin.next.domain.repository.ReelsRepository
import com.engineerfred.kotlin.next.domain.repository.UserRepository
import com.engineerfred.kotlin.next.presentation.screens.reels.ReelsViewModel.Companion.cacheDataSourceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class )
object AppModule {

    @Provides
    @Singleton
    fun providesFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseStorage() = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesPostRepository(
        database: FirebaseFirestore,
        storage: FirebaseStorage
    ): PostRepository = PostRepositoryImpl( database, storage  )

    @Provides
    @Singleton
    fun providesAuthRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl( auth, database )

    @Provides
    @Singleton
    fun providesUserRepository(
        database: FirebaseFirestore,
        storage: FirebaseStorage
    ): UserRepository = UserRepositoryImpl( database, storage )

    @Provides
    @Singleton
    fun providesChatRepository(
        database: FirebaseFirestore,
        storage: FirebaseStorage
    ): ChatRepository = ChatRepositoryImpl( database, storage )

    @Provides
    @Singleton
    fun providesNotificationRepository(
        database: FirebaseFirestore
    ): NotificationRepository = NotificationRepositoryImpl(database)

    @Provides
    @Singleton
    fun providesCommentRepository(
        database: FirebaseFirestore
    ): CommentsRepository = CommentsRepositoryImpl(database)

    @Provides
    @Singleton
    fun providesReelsRepository(
        database: FirebaseFirestore
    ): ReelsRepository = ReelsRepositoryImpl(database)

    @Provides
    @Singleton
    fun providesGeminiRepository(): GeminiRepository = GeminiRepositoryImpl()

    @Singleton
    @Provides
    fun provideDatastoreInstance(  @ApplicationContext context : Context) = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile("settings")
    }

    @Provides
    @Singleton
    fun providesAiChatCache(@ApplicationContext context: Context): AiChatCache {
        return Room.databaseBuilder(context, AiChatCache::class.java, "ai_chat_cache")
            .fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    @Singleton
    fun providesAiChatCacheDao( aiChatCache: AiChatCache ): AiChatDao = aiChatCache.getAiChatDao()

    @Provides
    @Singleton
    fun providesAiChatCacheRepository( cacheDao: AiChatDao): AiChatCacheRepository = AiChatCacheRepositoryImpl(cacheDao)

    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    fun provideExoPlayer( app: Application)  = ExoPlayer.Builder( app )
        .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory)).build()

}