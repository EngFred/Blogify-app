package com.engineerfred.kotlin.next.presentation.screens.create

import android.content.Context

sealed class CreateUiEvents {
    data class PostTextValueChanged( val text: String ) : CreateUiEvents()
    data class PostButtonClicked( val context: Context ): CreateUiEvents()
    data class ImageUrlChanged(val url: String ) : CreateUiEvents()
    data class VideoUrlChanged(val url: String ) : CreateUiEvents()
    data class PostTypeChanged( val postType: String ) : CreateUiEvents()
    data class FeelingSelected( val feeling: String ) : CreateUiEvents()
    data class LocationSelected( val location: String ) : CreateUiEvents()
    data class FriendsTagged( val friendsTagged: ArrayList<String> ) : CreateUiEvents()
}