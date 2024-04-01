package com.engineerfred.kotlin.next.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.NextTheme

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    text: () -> String,
    onTextChanged: (String) -> Unit,
    onDoneClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    placeHolder: String = "Type your location",
    isDarkTheme: Boolean
) {
    
    val containerColor = if ( !isDarkTheme ) MaterialTheme.colorScheme.primary.copy(alpha = .6f) else Charcoal

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            value = text(),
            onValueChange = onTextChanged,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = if (isDarkTheme) Color.White else CrimsonRed
            ),
            trailingIcon = {
                AnimatedVisibility(visible = text().isNotEmpty()) {
                    IconButton(onClick = { onCloseClicked() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }
                }
            }, shape = RoundedCornerShape(11.dp),
            placeholder = { Text(text = placeHolder, color = Color.LightGray) },
            singleLine = true,
            keyboardActions = KeyboardActions( onDone = {
                if ( text.invoke().isNotEmpty() ) {
                    onDoneClicked()
                }
            } ),
            keyboardOptions = KeyboardOptions( imeAction = ImeAction.Done, keyboardType = KeyboardType.Text )
        )
        IconButton(onClick = { onDoneClicked() }, enabled = text.invoke().isNotEmpty()) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = null,
                tint = if (isDarkTheme) Color.White else CrimsonRed,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Preview( showBackground = true )
@Composable
private fun SearchTextFieldPreview() {
    NextTheme {
        SearchTextField(
            text = { "" },
            onTextChanged = {},
            onDoneClicked = { /*TODO*/ },
            onCloseClicked = {},
            isDarkTheme = false
        )
    }
}