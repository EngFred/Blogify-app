package com.engineerfred.kotlin.next.presentation.screens.authGraphScreens.loginScreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.NextTheme

@Composable
fun LoginContainer(
    modifier: Modifier = Modifier,
    emailValue: () -> String,
    passwordValue: () -> String,
    onLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    loggingInProgress: () -> Boolean,
    onEmailValueChanged: (String) -> Unit,
    onPasswordValueChanged: (String) -> Unit,
    isDarkTheme: Boolean
) {
    val cursorColor = if ( isDarkTheme ) Color.White else CrimsonRed

    val focusManager = LocalFocusManager.current
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }

    var passwordTFTrailingIcon = if ( isPasswordVisible ) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        TextField(
            modifier = Modifier.fillMaxWidth().height(55.dp),
            value = emailValue(),
            onValueChange = onEmailValueChanged,
            keyboardOptions = KeyboardOptions(  imeAction = ImeAction.Next, keyboardType = KeyboardType.Email ),
            placeholder = {  Text(text = "Email", fontSize = 15.sp) },
            textStyle = TextStyle( fontSize = 15.sp),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = cursorColor
            ), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        TextField(
            modifier = Modifier.fillMaxWidth().height(55.dp),
            value = passwordValue(),
            onValueChange = onPasswordValueChanged,
            keyboardOptions = KeyboardOptions(  imeAction = ImeAction.Done, keyboardType = KeyboardType.Password ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus()  }
            ),
            trailingIcon = {
                 Icon(
                     modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible },
                     imageVector = passwordTFTrailingIcon,
                     contentDescription = null,
                     tint = if (isDarkTheme) Color.White else CrimsonRed
                 )
            },
            visualTransformation = PasswordVisualTransformation(),
            placeholder = {  Text(text = "Password", fontSize = 15.sp) },
            textStyle = TextStyle( fontSize = 15.sp),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = cursorColor
            ), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Button(
            onClick = {
                focusManager.clearFocus()
                onLogin()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                //disabledContainerColor = Color.LightGray,
                containerColor = if (isDarkTheme) Charcoal else CrimsonRed,
                disabledContentColor = Color.White,
                contentColor = Color.White
            ), enabled = !loggingInProgress() && emailValue().isNotEmpty() && passwordValue().isNotEmpty()
        ) {
            if ( loggingInProgress() ) {
                CircularProgressIndicator( color = Color.White, modifier = Modifier.size(22.dp) )
            }else {
                Text(text = "Log in")
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), contentAlignment = Alignment.Center
        ){
            Text(
                text = "Forgot password?",
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color.White else Color.Black,
                modifier = Modifier.clickable { onForgotPassword() }
            )
        }
    }
}

@Preview( showBackground = true )
@Composable
private fun LoginContainerPreview() {
    NextTheme {
        LoginContainer(
            emailValue = { "" },
            passwordValue = { "" },
            onLogin = {  },
            onForgotPassword = {  },
            loggingInProgress = { true },
            onEmailValueChanged = {},
            onPasswordValueChanged = {},
            isDarkTheme = false
        )
    }
}