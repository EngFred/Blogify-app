package com.engineerfred.kotlin.next.presentation.screens.editProfile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey

@Composable
fun EditProfileScreen(
    onBackClicked: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel,
    isDarkTheme: Boolean
) {

    val currentUser = commonViewModel.currentUser
    val uiState = viewModel.uiState.collectAsState().value
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )

    if ( !uiState.updateError.isNullOrEmpty() ) {
        Toast.makeText(context, uiState.updateError, Toast.LENGTH_LONG).show()
    }

    if ( uiState.updateSuccessful ) onBackClicked.invoke()

    currentUser?.let {
        LaunchedEffect(key1 = Unit) {
            viewModel.initializeUiState( currentUser.firstName, currentUser.lastName, currentUser.about )
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 8.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { onBackClicked.invoke() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Edit Profile",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        HorizontalDivider()
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
        ){
            Text(text = "Edit your names:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(5.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier.weight(1f).height(55.dp),
                    value = uiState.firstName,
                    onValueChange = {
                        viewModel.onEvent( EditProfileUiEvents.FirstNameChanged(it) )
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(  imeAction = if ( uiState.lastName.isNotEmpty() ) ImeAction.Done else ImeAction.Next, keyboardType = KeyboardType.Text ),
                    placeholder = {  Text(text = "First name", fontSize = 13.sp, maxLines = 1) },
                    keyboardActions = KeyboardActions( onDone = { focusManager.clearFocus() } ),
                    textStyle = TextStyle( fontSize = 13.sp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = if(isDarkTheme) Color.White else CrimsonRed
                    ), shape = RoundedCornerShape(5.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                TextField(
                    modifier = Modifier.weight(1f).height(55.dp),
                    value = uiState.lastName,
                    onValueChange = {
                        viewModel.onEvent( EditProfileUiEvents.LastNameChanged(it) )
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(  imeAction = if ( uiState.firstName.isNotEmpty() ) ImeAction.Done else ImeAction.Next, keyboardType = KeyboardType.Text ),
                    keyboardActions = KeyboardActions( onDone = { focusManager.clearFocus() } ),
                    placeholder = {  Text(text = "Last name", fontSize = 13.sp, maxLines = 1) },
                    textStyle = TextStyle( fontSize = 13.sp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = if(isDarkTheme) Color.White else CrimsonRed
                    ), shape = RoundedCornerShape(5.dp)
                )
            }
            Spacer(modifier = Modifier.size(11.dp))
            Text(text = "Edit your bio:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(5.dp))
            TextField(
                modifier = Modifier.fillMaxWidth().height(55.dp),
                value = uiState.about ?: "",
                onValueChange = {
                    viewModel.onEvent( EditProfileUiEvents.AboutChanged(it) )
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(  imeAction = if ( uiState.firstName.isNotEmpty() && uiState.lastName.isNotEmpty() ) ImeAction.Done else ImeAction.Next, keyboardType = KeyboardType.Text ),
                placeholder = {  Text(text = "Bio", fontSize = 15.sp, maxLines = 1) },
                textStyle = TextStyle( fontSize = 15.sp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = if(isDarkTheme) Color.White else CrimsonRed
                ), shape = RoundedCornerShape(5.dp)
            )
            Spacer(modifier = Modifier.size(11.dp))
            Button(
                onClick = {
                    viewModel.onEvent( EditProfileUiEvents.UpdateButtonClicked )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = uiState.lastName.isNotEmpty() && uiState.lastName.length >= 2 && uiState.firstName.length >= 2 && uiState.firstName.isNotEmpty() && !uiState.isUpdating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if( isDarkTheme ) DarkSlateGrey else CrimsonRed,
                    disabledContentColor = if(isDarkTheme) DarkSlateGrey  else CrimsonRed,
                    contentColor = Color.White
                )
            ) {
                if ( uiState.isUpdating ) {
                    CircularProgressIndicator( modifier = Modifier.size(20.dp), color = Color.White )
                } else {
                    Text(text = "Update", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}