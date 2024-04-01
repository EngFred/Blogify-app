package com.engineerfred.kotlin.next.presentation.screens.create.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.NextTheme

@Composable
fun CaptionTextField(
    modifier: Modifier = Modifier,
    text: () -> String,
    onTextChanged: (String) -> Unit,
    placeHolder: String,
    height: Dp,
    isDarkTheme: Boolean
) {

    val btnColor = if (isDarkTheme) Charcoal else CrimsonRed.copy(alpha = .6f)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        colors = CardDefaults.cardColors(
            containerColor = btnColor,
        )
    ) {
        TextField(
            value = text(),
            onValueChange = onTextChanged,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(text = placeHolder, color = Color.White)
            }
        )
    }
}

@Preview( showBackground = true )
@Composable
private fun CaptionTextFieldPreview() {
    NextTheme {
        CaptionTextField(
            text = { "" },
            onTextChanged = {},
            placeHolder = "",
            height = 300.dp,
            isDarkTheme = false
        )
    }
}