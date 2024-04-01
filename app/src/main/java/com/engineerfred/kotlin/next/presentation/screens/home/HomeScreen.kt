package com.engineerfred.kotlin.next.presentation.screens.home

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.presentation.common.AppBottomNavBar
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.navigation.BottomNavItem
import com.engineerfred.kotlin.next.presentation.navigation.HomeNavGraph
import com.engineerfred.kotlin.next.presentation.navigation.ScreenRoutes
import com.engineerfred.kotlin.next.presentation.screens.feeds.components.AppTopBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    onThemeToggled: () -> Unit,
    isDarkTheme: Boolean,
    commonViewModel: CommonViewModel
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val context = LocalContext.current
    
    var isDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val items = listOf(
        BottomNavItem(
            label = "Home",
            icon = R.drawable.ic_home,
            destinationScreen = ScreenRoutes.Feeds.destination
        ),
        BottomNavItem(
            label = "People",
            icon = R.drawable.ic_people,
            destinationScreen = ScreenRoutes.People.destination
        ),
        BottomNavItem(
            label = "Message",
            icon = R.drawable.ic_message,
            destinationScreen = ScreenRoutes.Message.destination
        ),
        BottomNavItem(
            label = "Nots",
            icon = R.drawable.ic_notifications,
            destinationScreen = ScreenRoutes.Notifications.destination
        ),
        BottomNavItem(
            label = "Reels",
            icon = R.drawable.ic_video,
            destinationScreen = ScreenRoutes.Reels.destination
        ) )

    val galleryPerm2 = rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO))
    val galleryPerm3 = rememberPermissionState(  Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED )
    val galleryPerm1 = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    var textToShow by rememberSaveable {
        mutableStateOf("")
    }
    
    Scaffold(
        topBar = {
            if ( currentDestination?.route == ScreenRoutes.Feeds.destination ) {
                AppTopBar(
                    onSearchClicked = {
                        navController.navigate( ScreenRoutes.Search.destination ) {
                            launchSingleTop = true
                        }
                    },
                    onMenuClicked = {
                        navController.navigate( ScreenRoutes.Settings.destination ) {
                            launchSingleTop = true
                        }
                    },
                    onCreatePostClicked = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            if ( galleryPerm3.status.isGranted ) {
                                navController.navigate( ScreenRoutes.Create.destination ) {
                                    launchSingleTop = true
                                }
                            } else {
                                if (galleryPerm3.status.shouldShowRationale) {
                                    // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                    textToShow = "The app needs to access your phone gallery inorder to be able" +
                                            " to select and post images. Please grant the permission."
                                    isDialogVisible = true
                                } else {
                                    // If it's the first time the user lands on this feature
                                    galleryPerm3.launchPermissionRequest()
                                    if ( galleryPerm3.status.isGranted ) {
                                        Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if ( galleryPerm2.allPermissionsGranted ) {
                                navController.navigate( ScreenRoutes.Create.destination ) {
                                    launchSingleTop = true
                                }
                            } else {
                                if (galleryPerm2.shouldShowRationale) {
                                    // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                    textToShow = "The app needs to access your phone gallery inorder to be able" +
                                            " to select and post images and videos. Please grant the permission."
                                    isDialogVisible = true
                                } else {
                                    // If it's the first time the user lands on this feature
                                    galleryPerm2.launchMultiplePermissionRequest()
                                    if ( galleryPerm2.allPermissionsGranted ) {
                                        Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            if ( galleryPerm1.status.isGranted ) {
                                navController.navigate( ScreenRoutes.Create.destination ) {
                                    launchSingleTop = true
                                }
                            } else {
                                if (galleryPerm1.status.shouldShowRationale) {
                                    // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                    textToShow = "The app needs to access your phone gallery inorder to be able" +
                                            " to select and post images. Please grant the permission."
                                    isDialogVisible = true
                                } else {
                                    // If it's the first time the user lands on this feature
                                    galleryPerm1.launchPermissionRequest()
                                    if ( galleryPerm1.status.isGranted ) {
                                        Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (
                currentDestination?.route == ScreenRoutes.Feeds.destination ||
                currentDestination?.route == ScreenRoutes.People.destination ||
                currentDestination?.route == ScreenRoutes.Message.destination ||
                currentDestination?.route == ScreenRoutes.Notifications.destination
            ) {
                AppBottomNavBar(items = items, navController = navController , isDarkTheme = isDarkTheme)
            }
        }
    ){
        
        //display the home graph
        HomeNavGraph( modifier = Modifier.padding(it), navController = navController, onThemeToggled, isDarkTheme, commonViewModel )
        if ( isDialogVisible ) {
            AlertDialog(
                onDismissRequest = { isDialogVisible = false },
                confirmButton = {
                    Button(onClick = {
                        isDialogVisible = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            galleryPerm3.launchPermissionRequest()
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            galleryPerm2.launchMultiplePermissionRequest()
                        } else {
                            galleryPerm1.launchPermissionRequest()
                        }
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )) {
                        Text(text = "Grant Permission")
                    }
                },
                title = {
                    Text(text = "Permission Required!")
                },
                text = {
                    Text(text = textToShow)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White,
                textContentColor = Color.White
            )
        }
    }
}