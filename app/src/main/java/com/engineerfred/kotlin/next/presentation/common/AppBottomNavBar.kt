package com.engineerfred.kotlin.next.presentation.common

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.presentation.navigation.BottomNavItem
import com.engineerfred.kotlin.next.presentation.navigation.ScreenRoutes
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey
import com.engineerfred.kotlin.next.presentation.theme.NextTheme

@Composable
fun AppBottomNavBar(
    items: List<BottomNavItem>,
    navController: NavHostController,
    isDarkTheme: Boolean
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomAppBar(
        containerColor = if ( currentDestination?.route == ScreenRoutes.Reels.destination ) Color.Black else MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {

        items.forEach {
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if ( currentDestination?.route == ScreenRoutes.Reels.destination ) CrimsonRed else Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = if (isDarkTheme ) DarkSlateGrey else DarkSlateGrey.copy(alpha = .5f),
                ),
                selected = currentDestination?.route == it.destinationScreen,
                onClick = {
                    navController.navigate( it.destinationScreen ) {
                        popUpTo(navController.graph.findStartDestination().id){ saveState =  true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(painter = painterResource(id = it.icon), contentDescription = it.label)
                }
            )
        }
    }
}

@Preview( showBackground = true )
@Composable
fun BottomBarPreview() {
    NextTheme {
        AppBottomNavBar(items = listOf(
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
            )
        ), navController = rememberNavController(), false)
    }
}