package com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.nameScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed

@Composable
fun EnterNameScreen(
    onBackClicked: () -> Unit,
    onNextClicked: (String,String) -> Unit,
    onAlreadyHaveAccountClicked: () -> Unit,
    isDarkTheme: Boolean
) {

    val bgColor = if (isDarkTheme) MaterialTheme.colorScheme.background else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    SetSystemBarColor(color = MaterialTheme.colorScheme.surface, isDarkTheme)

    var firstNameValue by rememberSaveable {
        mutableStateOf("")
    }

    var lastNameValue by rememberSaveable {
        mutableStateOf("")
    }

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
        Text(text = "What's your name?", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = textColor)
        Text(text = "Enter the name you use in real life.", color = textColor)
        Spacer(modifier = Modifier.size(11.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier.weight(1f).height(55.dp),
                value = firstNameValue,
                onValueChange = {
                     firstNameValue = it
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(  imeAction = ImeAction.Next, keyboardType = KeyboardType.Text ),
                placeholder = {  Text(text = "First name", fontSize = 13.sp, maxLines = 1) },
                textStyle = TextStyle( fontSize = 13.sp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = if ( isDarkTheme ) Color.White else CrimsonRed
                ), shape = RoundedCornerShape(5.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            TextField(
                modifier = Modifier.weight(1f).height(55.dp),
                value = lastNameValue,
                onValueChange = {
                    lastNameValue = it
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(  imeAction = ImeAction.Next, keyboardType = KeyboardType.Text ),
                placeholder = {  Text(text = "Last name", fontSize = 13.sp, maxLines = 1) },
                textStyle = TextStyle( fontSize = 13.sp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = if ( isDarkTheme ) Color.White else CrimsonRed
                ), shape = RoundedCornerShape(5.dp)
            )
        }
        Spacer(modifier = Modifier.size(11.dp))
        Button(
            onClick = { onNextClicked(firstNameValue.replaceFirstChar { it.titlecase() }.trim(), lastNameValue.replaceFirstChar { it.titlecase() }.trim()) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = firstNameValue.isNotEmpty() && lastNameValue.isNotEmpty() && firstNameValue.length >= 2 && lastNameValue.length >= 2,
            colors = ButtonDefaults.buttonColors(
                //disabledContainerColor = Color.LightGray,
                containerColor = if (isDarkTheme) Charcoal else CrimsonRed,
                disabledContentColor = Color.White,
                contentColor = Color.White
            )
        ) {
            Text(text = "Next", fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.clickable { onAlreadyHaveAccountClicked.invoke() },
                text = "Already have an account?.",
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

}