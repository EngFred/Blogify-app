package com.engineerfred.kotlin.next.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.engineerfred.kotlin.next.domain.model.Feeling
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val feelings = listOf(
    Feeling("", "happy"),
    Feeling("", "blessed"),
    Feeling("", "loved"),
    Feeling("", "sad"),
    Feeling("", "lovely"),
    Feeling("", "thankful"),
    Feeling("", "excited"),
    Feeling("", "crazy"),
    Feeling("", "grateful"),
    Feeling("", "blissful"),
    Feeling("", "fantastic"),
    Feeling("", "silly")
)

val places = listOf(
    "Kampala, Uganda",
    "Jinja, Uganda",
    "Tororo, Uganda",
    "Mengo, Uganda",
    "Wakiso, Uganda",
    "New Delhi, India",
    "Tokyo, Japan",
    "Gulu, Uganda",
    "Gulu, Uganda",
    "K.Garden Groove College Buddo, Kampala Uganda",
    "Makere University, Kampala Uganda",
    "Cambridge University, USA",
    "Manyagwa, Uganda",
    "Entebbe, Uganda"
)

val dummyListOfFriends = listOf(
    "Ssemayanja Denis",
    "Shadiq Razaq",
    "Shamira Live",
    "Kamuhanda Benon",
    "Jude Wiz Wiz",
    "Nakandi Proscovia Norah",
    "Kisibo Peter Hixon",
    "Cedric Mwambu",
    "Eengu Dominic",
    "Bisaso Raymond",
    "Remie Cruuzy",
    "Kyeyune Muhammad",
    "Thio Star",
    "Ssemwanga David",
    "Alowo Florence",
    "Trisha Hoodz",
    "Shadrack Grayham",
    "Mayombwe Ronald",
    "Daniel Bats Arvel",
    "Christine Agnes",
    "Blessed Trinak",
    "Mwesigwa Amos Amo",
    "Jovan Jovic",
    "Iragena Felix Mugisha",
    "Fickie Pro Max",
    "Peterson Zander",
    "Glorious Rains",
    "Chris Bafana RichMan Rich",
    "Shadiz Black"
)


private const val ONE_MINUTE = 60 * 1000L
private const val ONE_HOUR = 60 * ONE_MINUTE
private const val ONE_DAY = 24 * ONE_HOUR
private const val ONE_WEEK = 7 * ONE_DAY
private const val ONE_MONTH = 30 * ONE_DAY
private const val ONE_YEAR = 365 * ONE_DAY

fun Context.compressImage(filePath: String): ByteArray {
    // compressing image
    val bitmap = MediaStore.Images.Media.getBitmap( contentResolver, Uri.parse( filePath ) )
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}

fun formatPostTimeStamp(commentTimeStamp: Long ) : String {
    val currentTimeStamp = System.currentTimeMillis()
    val timeDifference = (currentTimeStamp - commentTimeStamp)

    return when {
        timeDifference < ONE_MINUTE -> "Just now"
        timeDifference < ONE_HOUR -> "${timeDifference / ONE_MINUTE} m"
        timeDifference < ONE_DAY -> "${timeDifference / ONE_HOUR} h"
        timeDifference < ONE_WEEK -> "${timeDifference / ONE_DAY} d"
        timeDifference < ONE_MONTH -> "${timeDifference / ONE_WEEK} w"
        timeDifference < ONE_YEAR -> "${timeDifference / ONE_MONTH} mo"
        else -> "${timeDifference / ONE_YEAR} y"
    }
}

fun pluralizeWord(word: String, count: Long): String {
    return if (count == 1L) {
        word
    } else {
        "${word}s"
    }
}

fun formatCommentTimeStamp(commentTimeStamp: Long ) : String {
    val currentTimeStamp = System.currentTimeMillis()
    val timeDifference = (currentTimeStamp - commentTimeStamp)

    return when {
        timeDifference < ONE_MINUTE -> "just now"
        timeDifference < ONE_HOUR -> "${timeDifference / ONE_MINUTE} ${pluralizeWord("min", timeDifference / ONE_MINUTE  )} ago"
        timeDifference < ONE_DAY -> "${timeDifference / ONE_HOUR} ${pluralizeWord("hr",timeDifference / ONE_HOUR )} ago"
        timeDifference < ONE_WEEK -> if ( timeDifference / ONE_DAY == 1L ) "yesterday" else "${timeDifference / ONE_DAY} days ago"
        timeDifference < ONE_MONTH -> if ( timeDifference / ONE_WEEK == 1L ) "last week" else "${timeDifference / ONE_WEEK} wks ago"
        timeDifference < ONE_YEAR ->  if ( timeDifference / ONE_MONTH == 1L ) "last month" else "${timeDifference / ONE_MONTH} mos ago"
        else ->  if ( timeDifference / ONE_YEAR == 1L ) "last year" else "${timeDifference / ONE_YEAR} yrs ago"
    }
}

fun formatTimestampToDateString(timestamp: Long): String {
    // Create a SimpleDateFormat object with the desired date format
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

    // Convert the timestamp to a Date object
    val date = Date(timestamp)

    // Format the Date object into a string
    return dateFormat.format(date)
}

fun formatTimestampToDateAndTimeString(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MMM, dd 'at' h:mm a", Locale.getDefault())
    val date = Date(timestamp)

    val currentTimeStamp = System.currentTimeMillis()
    val timeDifference = (currentTimeStamp - timestamp)

    return when {
        timeDifference < ONE_MINUTE -> "Just now"
        timeDifference < ONE_HOUR -> "${timeDifference / ONE_MINUTE} ${pluralizeWord("minute", timeDifference / ONE_MINUTE )} ago"
        timeDifference < ONE_DAY -> "${timeDifference / ONE_HOUR} ${pluralizeWord("hour",timeDifference / ONE_HOUR )} ago"
        else -> dateFormat.format(date)
    }
}