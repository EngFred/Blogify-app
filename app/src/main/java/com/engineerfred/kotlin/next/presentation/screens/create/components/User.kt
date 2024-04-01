package com.engineerfred.kotlin.next.presentation.screens.create.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed

@Composable
fun User(
    modifier: Modifier = Modifier,
    onAudienceButtonClicked: () -> Unit,
    text: AnnotatedString,
    userProfileImage: String?,
    isDarkTheme: Boolean
) {

    val btnColor = if (isDarkTheme) Charcoal else CrimsonRed

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        AsyncImage(
            model = userProfileImage,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 4.dp)
                .size(60.dp)
                .clip(CircleShape)
                .background(if (isDarkTheme) Charcoal else Color.LightGray)
                .border(1.dp, if (isDarkTheme) Color.White else CrimsonRed, CircleShape),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text
            )
            Spacer(modifier = Modifier.size(6.dp))
            Row(
                modifier = Modifier
                    .clickable { onAudienceButtonClicked() }
                    .clip(RoundedCornerShape(6.dp))
                    .background(btnColor)
                    .padding(
                        vertical = 3.dp,
                        horizontal = 5.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_public),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Public",
                    fontWeight = FontWeight.Light,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_dropdown_arrow),
                    contentDescription = null,
                    tint = Color.White
                )

            }
        }

    }

}