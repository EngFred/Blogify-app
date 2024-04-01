package com.engineerfred.kotlin.next.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import kotlinx.coroutines.Dispatchers
import java.net.URLEncoder

@Composable
fun PostAsyncImage(
    images: ArrayList<String>,
    isDarkTheme: Boolean,
    onImageClicked: (String) -> Unit,
) {

    val context = LocalContext.current

    when (images.size) {
        1 -> {
            val imageUrl = URLEncoder.encode(images[0], "utf-8")
            val imageRequest = ImageRequest.Builder(context)
                .data(images[0])
                .allowConversionToBitmap(true)
                .crossfade(true)
                .dispatcher(Dispatchers.IO)
                .memoryCacheKey(images[0])
                .diskCacheKey(images[0])
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()

            Box(modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 480.dp)
            ) {
                SubcomposeAsyncImage(
                    model = imageRequest,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(440.dp)
                                .background(if (isDarkTheme) Charcoal else Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "loading...", color =  if (isDarkTheme) Color.White else CrimsonRed )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(440.dp)
                                .background(if (isDarkTheme) Charcoal else Color.LightGray),
                            contentAlignment = Alignment.Center
                        ){}
                    },

                    modifier = Modifier
                        .clickable { onImageClicked.invoke(imageUrl) }
                        .align(Alignment.Center)
                        .wrapContentWidth()
                        .heightIn(max = 480.dp)
                        .background(if (isDarkTheme) Charcoal else Color.LightGray),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                )
            }

        }
        2 -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
                    .background(if (isDarkTheme) Charcoal else Color.LightGray)
            ) {
                val imageRequest1 = ImageRequest.Builder(context)
                    .data(images[0])
                    .allowConversionToBitmap(true)
                    .crossfade(true)
                    .dispatcher(Dispatchers.IO)
                    .memoryCacheKey(images[0])
                    .diskCacheKey(images[0])
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()

                val imageUrl1 = URLEncoder.encode(images[0], "utf-8")

                SubcomposeAsyncImage(
                    model = imageRequest1,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(.5f)
                                .height(440.dp)
                                .background(if (isDarkTheme) Charcoal else Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "loading...", color =  if (isDarkTheme) Color.White else CrimsonRed )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(.5f)
                                .height(440.dp)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ){}
                    },

                    modifier = Modifier
                        .clickable { onImageClicked.invoke(imageUrl1) }
                        .fillMaxWidth(.5f)
                        .heightIn(max = 440.dp)
                        .background(Color.Transparent),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                val imageRequest2 = ImageRequest.Builder(context)
                    .data(images[1])
                    .allowConversionToBitmap(true)
                    .crossfade(true)
                    .dispatcher(Dispatchers.IO)
                    .memoryCacheKey(images[1])
                    .diskCacheKey(images[1])
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()

                val imageUrl2 = URLEncoder.encode(images[1], "utf-8")

                SubcomposeAsyncImage(
                    model = imageRequest2,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(440.dp)
                                .background(if (isDarkTheme) Charcoal else Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "loading...", color =  if (isDarkTheme) Color.White else CrimsonRed )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(440.dp)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ){}
                    },

                    modifier = Modifier
                        .clickable { onImageClicked.invoke(imageUrl2) }
                        .fillMaxWidth()
                        .heightIn(max = 440.dp)
                        .background(Color.Transparent),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
        3 -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
                    .background(if (isDarkTheme) Charcoal else Color.LightGray)
            ) {
                val imageRequest = ImageRequest.Builder(context)
                    .data(images[0])
                    .allowConversionToBitmap(true)
                    .crossfade(true)
                    .dispatcher(Dispatchers.IO)
                    .memoryCacheKey(images[0])
                    .diskCacheKey(images[0])
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()

                val imageUrl1 = URLEncoder.encode(images[0], "utf-8")
                val imageUrl2 = URLEncoder.encode(images[1], "utf-8")
                val imageUrl3 = URLEncoder.encode(images[2], "utf-8")

                SubcomposeAsyncImage(
                    model = imageRequest,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(.5f)
                                .height(440.dp)
                                .background(if (isDarkTheme) Charcoal else Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "loading...", color =  if (isDarkTheme) Color.White else CrimsonRed )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(.5f)
                                .height(440.dp)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ){}
                    },

                    modifier = Modifier
                        .clickable {
                            onImageClicked.invoke(imageUrl1)
                        }
                        .fillMaxWidth(.5f)
                        .heightIn(max = 440.dp)
                        .background(Color.Transparent),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 440.dp)
                        .background(Color.Transparent),
                ) {
                    val imageRequest1 = ImageRequest.Builder(context)
                        .data(images[1])
                        .allowConversionToBitmap(true)
                        .crossfade(true)
                        .dispatcher(Dispatchers.IO)
                        .memoryCacheKey(images[1])
                        .diskCacheKey(images[1])
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build()

                    SubcomposeAsyncImage(
                        model = imageRequest1,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .background(if (isDarkTheme) Charcoal else Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "loading...", color =  if (isDarkTheme) Color.White else CrimsonRed )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .background(Color.Transparent),
                                contentAlignment = Alignment.Center
                            ){}
                        },

                        modifier = Modifier
                            .clickable { onImageClicked.invoke(imageUrl2) }
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .background(Color.Transparent),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    val imageRequest2 = ImageRequest.Builder(context)
                        .data(images[2])
                        .allowConversionToBitmap(true)
                        .crossfade(true)
                        .dispatcher(Dispatchers.IO)
                        .memoryCacheKey(images[2])
                        .diskCacheKey(images[2])
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build()

                    SubcomposeAsyncImage(
                        model = imageRequest2,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .background(if (isDarkTheme) Charcoal else Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "loading...", color =  if (isDarkTheme) Color.White else CrimsonRed )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .background(Color.Transparent),
                                contentAlignment = Alignment.Center
                            ){}
                        },

                        modifier = Modifier
                            .clickable { onImageClicked.invoke(imageUrl3) }
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .background(Color.Transparent),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

}