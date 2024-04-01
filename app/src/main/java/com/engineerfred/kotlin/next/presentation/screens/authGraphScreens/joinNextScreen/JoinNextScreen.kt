package com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.joinNextScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.EerieBlack

@Composable
fun JoinNextScreen(
    onBackClicked: () -> Unit,
    onGetStartedClicked: () -> Unit,
    onAlreadyHaveAccountClicked: () -> Unit,
    isDarkTheme: Boolean
) {
    val bgColor = if (isDarkTheme) MaterialTheme.colorScheme.background else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    SetSystemBarColor(color = MaterialTheme.colorScheme.surface, isDarkTheme)

    Column (
        Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ){

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
            IconButton(onClick = { onBackClicked.invoke() }) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = textColor)
            }
        }
        Spacer(modifier = Modifier.size(11.dp))
        Text(
            text = "Join Next",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            color = textColor
        )
        Spacer(modifier = Modifier.size(11.dp))
        Image(
            painter = painterResource(id = R.drawable.hackerr),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(EerieBlack),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(11.dp))
        Text(
            text = "Create an account to connect with friends, family and communities of people who share your interests.",
            color =  textColor
        )
        Spacer(modifier = Modifier.size(11.dp))
        Button(
            onClick = { onGetStartedClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                //disabledContainerColor = Color.LightGray,
                containerColor = if (isDarkTheme) Charcoal else CrimsonRed,
                disabledContentColor = Color.White,
                contentColor = Color.White
            )
        ) {
            Text(text = "Get Started", fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.size(11.dp))
        OutlinedButton(
            onClick = { onAlreadyHaveAccountClicked() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "I already have an account", fontWeight = FontWeight.SemiBold, color = if ( isDarkTheme ) Color.White else CrimsonRed)
        }
    }
}