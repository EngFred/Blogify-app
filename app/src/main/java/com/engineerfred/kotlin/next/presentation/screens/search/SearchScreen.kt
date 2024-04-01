package com.engineerfred.kotlin.next.presentation.screens.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.engineerfred.kotlin.next.core.SetSystemBarColor

@Composable
fun SearchScreen() {
    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )
    Box( modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp), contentAlignment = Alignment.Center ){
        Text(
            text = "The search screen feature was not implemented!!",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}