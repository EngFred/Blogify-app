package com.engineerfred.kotlin.next.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation

fun NavGraphBuilder.detailsNavGraph() {
    navigation(
        startDestination = ScreenRoutes.Search.destination,
        route = Graphs.Details.name
    ) {

    }
}