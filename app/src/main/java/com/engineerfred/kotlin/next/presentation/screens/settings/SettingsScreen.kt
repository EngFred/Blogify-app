package com.engineerfred.kotlin.next.presentation.screens.settings

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.screens.settings.components.ThemeSwitcher
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey
import com.engineerfred.kotlin.next.presentation.theme.EerieBlack
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    onProfileClicked: () -> Unit,
    onThemeToggled: () -> Unit,
    isDarkTheme: Boolean,
    commonViewModel: CommonViewModel,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )

    val currentUser = commonViewModel.currentUser
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isLoggingOut by remember {
        mutableStateOf(false)
    }

    val name = if ( currentUser != null ) "${currentUser.firstName.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }} ${currentUser.lastName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }}" else "..."
    val profileImage = currentUser?.profileImageUrl

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 8.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = {
                onNavigateUp()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Settings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (currentUser != null) onProfileClicked.invoke()
                }
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                model = profileImage ,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Charcoal else Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Your profile",
                    maxLines = 1,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Switch theme",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )
        Row(
            modifier = Modifier
                .clickable {
                    onThemeToggled()
                }
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ThemeSwitcher(
                size = 30.dp,
                padding = 5.dp,
                onThemeToggle = {
                    onThemeToggled()
                },
                isDarkTheme = isDarkTheme
            )
            Spacer(modifier = Modifier.size(15.dp))
            Text(
                text = if (isDarkTheme) "Dark theme" else "Light theme",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Save Login Info",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )
        Row(
            modifier = Modifier
                .clickable {
                    Toast.makeText(context,"Fred disabled this feature!", Toast.LENGTH_LONG).show()
                }
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Switch(
                checked = uiState.saveInfo,
                onCheckedChange = {
                    //viewModel.onEvent( SettingsUiEvents.SaveUserLoginInfoStateChanged )
                    Toast.makeText(context,"Fred disabled this feature!", Toast.LENGTH_LONG).show()
                },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = if ( isSystemInDarkTheme() ) EerieBlack else CrimsonRed
                )
            )
            Spacer(modifier = Modifier.size(15.dp))
            Text(
                text = "Remember me on this device",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider()
        ElevatedCard(
            onClick = {
                if ( !isLoggingOut ) {
                    isLoggingOut = true
                    Toast.makeText(context, "Logging out...", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    coroutineScope.launch {
                        delay(3000)
                        restartApplication(context)
                    }
                }
            },
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(
                contentColor = Color.White,
                containerColor = if(isDarkTheme) DarkSlateGrey  else CrimsonRed,
                disabledContainerColor = if(isDarkTheme) DarkSlateGrey  else CrimsonRed
            )
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(if (isDarkTheme) DarkSlateGrey else CrimsonRed),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Logout",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(imageVector = Icons.AutoMirrored.Rounded.Logout, contentDescription = null )
            }
        }
    }
}

private fun restartApplication( context: Context) {
    val pm = context.packageManager
    val intent = pm.getLaunchIntentForPackage(context.packageName)
    val componentName = intent!!.component
    val restartIntent = Intent.makeRestartActivityTask(componentName)
    context.startActivity(restartIntent)
    Runtime.getRuntime().exit(0)
}