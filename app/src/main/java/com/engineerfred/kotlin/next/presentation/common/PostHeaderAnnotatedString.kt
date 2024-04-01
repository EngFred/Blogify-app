package com.engineerfred.kotlin.next.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
fun postHeaderAnnotatedString(
    creatorName: String,
    feeling: String?,
    location: String?,
    taggedFriends: ArrayList<String>
): AnnotatedString {
    val textToShow = when {
        !location.isNullOrEmpty() && taggedFriends.isEmpty() && feeling.isNullOrEmpty() -> {
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(creatorName)
                }
                append(" is at ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(location)
                }
            }
        }

        !feeling.isNullOrEmpty() && location.isNullOrEmpty() && taggedFriends.isEmpty() -> {
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(creatorName)
                }
                append(" is feeling ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(feeling)
                }
            }
        }

        taggedFriends.isNotEmpty() && feeling.isNullOrEmpty() && location.isNullOrEmpty() -> {
            when (taggedFriends.size) {
                1 -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                    }
                }

                2 -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                        append(" and ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[1])
                        }
                    }
                }

                else -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                        append(" and ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${taggedFriends.size - 1} others")
                        }
                    }
                }
            }
        }

        !location.isNullOrEmpty() && !feeling.isNullOrEmpty() && taggedFriends.isEmpty() -> {
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(creatorName)
                }
                append(" is feeling ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(feeling)
                }
                append(" at ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(location)
                }
            }
        }

        !location.isNullOrEmpty() && !feeling.isNullOrEmpty() && taggedFriends.isNotEmpty() -> {
            when (taggedFriends.size) {
                1 -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is feeling ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(feeling)
                        }
                        append(" with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                        append(" at ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(location)
                        }
                    }
                }

                2 -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is feeling ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(feeling)
                        }
                        append(" with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                        append(" and ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[1])
                        }
                        append(" at ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(location)
                        }
                    }
                }

                else -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is feeling ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(feeling)
                        }
                        append(" with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                        append(" and ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${taggedFriends.size - 1} others")
                        }
                        append(" at ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(location)
                        }
                    }
                }
            }
        }

        taggedFriends.isNotEmpty() && !feeling.isNullOrEmpty() && location.isNullOrEmpty() -> {
            when (taggedFriends.size) {
                1 -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is feeling ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(feeling)
                        }
                        append(" with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                    }
                }

                2 -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is feeling ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(feeling)
                        }
                        append(" with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                        append(" and ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[1])
                        }
                    }
                }

                else -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is feeling ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(feeling)
                        }
                        append(" with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                        append(" and ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${taggedFriends.size - 1} others")
                        }
                    }
                }
            }
        }

        taggedFriends.isNotEmpty() && feeling.isNullOrEmpty() && !location.isNullOrEmpty() -> {
            when (taggedFriends.size) {
                1 -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                        append(" at ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(location)
                        }
                    }
                }

                2 -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                        append(" and ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[1])
                        }
                        append(" at ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(location)
                        }
                    }
                }

                else -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(creatorName)
                        }
                        append(" is with ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(taggedFriends[0])
                        }
                        append(" and ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${taggedFriends.size - 1} others")
                        }
                        append(" at ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(location)
                        }
                    }
                }
            }
        }

        else -> {
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(creatorName)
                }
            }
        }
    }
    return textToShow
}