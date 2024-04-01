package com.engineerfred.kotlin.next.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.screens.addFeeling.AddFeelingScreen
import com.engineerfred.kotlin.next.presentation.screens.addLocation.AddLocationScreen
import com.engineerfred.kotlin.next.presentation.screens.chat.ChatScreen
import com.engineerfred.kotlin.next.presentation.screens.chat.componets.ChatImageViewer
import com.engineerfred.kotlin.next.presentation.screens.chat.componets.ChatVideoPlayer
import com.engineerfred.kotlin.next.presentation.screens.chatWithGemini.ChatWithGeminiScreen
import com.engineerfred.kotlin.next.presentation.screens.commentReplies.CommentRepliesScreen
import com.engineerfred.kotlin.next.presentation.screens.create.CreateScreen
import com.engineerfred.kotlin.next.presentation.screens.editProfile.EditProfileScreen
import com.engineerfred.kotlin.next.presentation.screens.feeds.FeedsScreen
import com.engineerfred.kotlin.next.presentation.screens.messages.MessageScreen
import com.engineerfred.kotlin.next.presentation.screens.notifications.NotificationsScreen
import com.engineerfred.kotlin.next.presentation.screens.people.PeopleScreen
import com.engineerfred.kotlin.next.presentation.screens.player.PlayerScreen
import com.engineerfred.kotlin.next.presentation.screens.postDetail.PostDetailScreen
import com.engineerfred.kotlin.next.presentation.screens.profile.ProfileScreen
import com.engineerfred.kotlin.next.presentation.screens.profile2.Profile2Screen
import com.engineerfred.kotlin.next.presentation.screens.reels.ReelsScreen
import com.engineerfred.kotlin.next.presentation.screens.search.SearchScreen
import com.engineerfred.kotlin.next.presentation.screens.settings.SettingsScreen
import com.engineerfred.kotlin.next.presentation.screens.tagFriends.TagFriendsScreen
import com.engineerfred.kotlin.next.utils.Constants
import com.engineerfred.kotlin.next.utils.Constants.FEELING_KEY
import com.engineerfred.kotlin.next.utils.Constants.LOCATION_KEY
import com.engineerfred.kotlin.next.utils.Constants.TAGGED_FRIENDS_KEY

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun HomeNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onThemeToggled: () -> Unit,
    isDarkTheme: Boolean,
    viewModel: CommonViewModel
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = ScreenRoutes.Feeds.destination,
        route = Graphs.Home.name
    ) {

        composable(
            route = ScreenRoutes.Feeds.destination
        ) {
            FeedsScreen(
                onCreateClicked = {
                    navController.navigate( ScreenRoutes.Create.destination ) {
                        launchSingleTop = true
                    }
                }, onComment = {
                    navController.navigate( "${ScreenRoutes.PostDetail.destination}/$it" ) {
                        launchSingleTop = true
                    }
                },
                commonViewModel = viewModel,
                isDarkTheme = isDarkTheme,
                onUserProfileImageClicked = {
                    navController.navigate( "${ScreenRoutes.Profile2.destination}/$it" ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = ScreenRoutes.People.destination
        ) {
            PeopleScreen(
                commonViewModel = viewModel,
                onUserClicked = {
                    navController.navigate("${ScreenRoutes.Profile2.destination}/$it"){
                        launchSingleTop = true
                    }
                }, onSearchClicked = {
                    navController.navigate(ScreenRoutes.Search.destination){
                        launchSingleTop = true
                    }
                },
                isDarkTheme = isDarkTheme,
                onUserProfileImageClicked = {
                    navController.navigate( "${ScreenRoutes.Profile2.destination}/$it" ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = ScreenRoutes.Message.destination
        ) {
            MessageScreen(
                isDarkTheme = isDarkTheme,
                onChatWithGemini = {
                    navController.navigate( ScreenRoutes.ChatWithGemini.destination ){
                        launchSingleTop = true
                    }
                }, onChatClicked = {
                    navController.navigate( "${ScreenRoutes.Chat.destination}/$it" ){
                        launchSingleTop = true
                    }
                }, commonViewModel = viewModel
            )
        }

        composable(
            route = ScreenRoutes.Notifications.destination
        ) {
            NotificationsScreen(
                onSearchClicked = {
                    navController.navigate(ScreenRoutes.Search.destination){
                        launchSingleTop = true
                    }
                }, onFollowNotificationClicked = {
                    navController.navigate("${ScreenRoutes.Profile2.destination}/$it"){
                        launchSingleTop = true
                    }
                }, onLikeOrCommentNotificationClicked = {
                    navController.navigate("${ScreenRoutes.PostDetail.destination}/$it"){
                        launchSingleTop = true
                    }
                },
                isDarkTheme = isDarkTheme,
                onUserProfileImageClicked = {
                    navController.navigate( "${ScreenRoutes.Profile2.destination}/$it" ) {
                        launchSingleTop = true
                    }
                },
                onCommentReplyNotificationClicked = {
                   //todo
                }
            )
        }

        composable(
            route = ScreenRoutes.Reels.destination
        ) {
            ReelsScreen(
                onCreateReel = { navController.navigate(ScreenRoutes.Create.destination){ launchSingleTop = true } },
                onSearch = { navController.navigate(ScreenRoutes.Search.destination) { launchSingleTop = true } },
                onBack = { navController.popBackStack() },
                isDarkTheme = isDarkTheme,
                onUserProfileImageClicked = {
                    navController.navigate( "${ScreenRoutes.Profile2.destination}/$it" ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = ScreenRoutes.Settings.destination
        ) {
            SettingsScreen(
                onThemeToggled = onThemeToggled,
                isDarkTheme = isDarkTheme,
                onNavigateUp = {
                    navController.navigateUp()
                },
                onProfileClicked = {
                    navController.navigate(ScreenRoutes.Profile.destination) {
                        launchSingleTop = true
                    }
                }, commonViewModel = viewModel
            )
        }

        composable(
            route = ScreenRoutes.Search.destination
        ) {
            SearchScreen()
        }

        composable(
            route = ScreenRoutes.Create.destination
        ) {
            val feeling = it.savedStateHandle.get<String>(FEELING_KEY)
            val location = it.savedStateHandle.get<String>(LOCATION_KEY)
            val taggedFriends = it.savedStateHandle.get<ArrayList<String>>(TAGGED_FRIENDS_KEY)
            CreateScreen(
                feeling = feeling,
                location = location,
                taggedFriends = taggedFriends,
                onNavigateUp = { navController.navigateUp() },
                onPlayVideoClicked = {
                    navController.navigate("${ScreenRoutes.Player.destination}/$it") {
                        launchSingleTop = true
                    }
                }, onTagFriendsClicked = {
                    navController.navigate( ScreenRoutes.TagFriends.destination ) {
                        launchSingleTop = true
                    }
                }, onAddFeelingClicked = {
                    navController.navigate( ScreenRoutes.AddFeeling.destination ) {
                        launchSingleTop = true
                    }
                }, onAddLocationClicked = {
                    navController.navigate( ScreenRoutes.AddLocation.destination ) {
                        launchSingleTop = true
                    }
                }, commonViewModel = viewModel,
                onAddingPost = {
                    //val addingPost = navController.navigate( "${ScreenRoutes.Feeds.destination}" )
                    navController.navigateUp()
                }, isDarkTheme = isDarkTheme
            )
        }

        composable(
            route = "${ScreenRoutes.Player.destination}/{videoUrl}",
            arguments = listOf(
                navArgument("videoUrl") { NavType.StringType }
            )
        ) {
            val videoUrl = it.arguments?.getString("videoUrl")!!
            PlayerScreen( videoUrl) { navController.popBackStack() }
        }

        composable(
            route = ScreenRoutes.Profile.destination
        ) {
            ProfileScreen(
                onBackClicked = {
                    navController.navigateUp()
                }, commonViewModel = viewModel,
                onEditProfile = {
                    navController.navigate(ScreenRoutes.EditProfile.destination){
                        launchSingleTop = true
                    }
                }, onComment = {
                    navController.navigate( "${ScreenRoutes.PostDetail.destination}/$it" ) {
                        launchSingleTop = true
                    }
                }, onCreatePost = {
                    navController.navigate(ScreenRoutes.Create.destination){
                        launchSingleTop = true
                    }
                },
                isDarkTheme = isDarkTheme,
                onUserProfileImageClicked = {
                    navController.navigate( "${ScreenRoutes.Profile2.destination}/$it" ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = ScreenRoutes.TagFriends.destination
        ) {
            TagFriendsScreen(
                onBackClicked = {
                navController.navigateUp()
            }, onDoneTagFriends = {
                navController.previousBackStackEntry?.savedStateHandle?.set(TAGGED_FRIENDS_KEY, it)
                navController.popBackStack()
            }, commonViewModel = viewModel, isDarkTheme = isDarkTheme )
        }

        composable(
            route = ScreenRoutes.AddFeeling.destination
        ) {
            AddFeelingScreen(
                onBackClicked = { navController.navigateUp() },
                onFeelingSelected = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(FEELING_KEY, it)
                    navController.popBackStack()
                }, isDarkTheme = isDarkTheme
            )
        }

        composable(
            route = ScreenRoutes.AddLocation.destination
        ) {
            AddLocationScreen(
                onBackClicked = { navController.navigateUp() },
                onLocationSelected = { location ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(LOCATION_KEY, location)
                    navController.popBackStack()
                }, isDarkTheme = isDarkTheme
            )
        }

        composable(
            route = "${ScreenRoutes.PostDetail.destination}/{${Constants.POST_ID_KEY}}",
            arguments = listOf(
                navArgument(Constants.POST_ID_KEY) { NavType.StringType }
            )
        ) {
            val postId = it.arguments?.getString(Constants.POST_ID_KEY)!!
            PostDetailScreen(
                postId = postId,
                onBackClicked = {
                    navController.navigateUp()
                }, onReplyClicked = { commentId, postId, postOwnerName, commentOwnerName, commentOwnerId ->
                     navController.navigate("${ScreenRoutes.CommentReplies.destination}/$commentId/$postId/$postOwnerName/$commentOwnerName/$commentOwnerId") {
                         launchSingleTop = true
                     }
                },
                commonViewModel = viewModel,
                isDarkTheme = isDarkTheme,
                onUserProfileImageClicked = {
                    navController.navigate( "${ScreenRoutes.Profile2.destination}/$it" ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = ScreenRoutes.EditProfile.destination
        ){
            EditProfileScreen(onBackClicked = { navController.navigateUp() }, commonViewModel = viewModel, isDarkTheme = isDarkTheme)
        }

        composable(
            route = "${ScreenRoutes.Profile2.destination}/{${Constants.USER_ID_KEY}}",
            arguments = listOf(
                navArgument(Constants.USER_ID_KEY){ NavType.StringType }
            )
        ){
            val userId = it.arguments?.getString(Constants.USER_ID_KEY)!!
            Profile2Screen(
                userId = userId,
                onBackClicked = { navController.navigateUp() },
                commonViewModel = viewModel,
                onComment = {
                    navController.navigate( "${ScreenRoutes.PostDetail.destination}/$it" ) {
                        launchSingleTop = true
                    }
                },
                isDarkTheme = isDarkTheme,
                onUserProfileImageClicked = {
                    navController.navigate( "${ScreenRoutes.Profile2.destination}/$it" ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "${ScreenRoutes.CommentReplies.destination}/{${Constants.COMMENT_ID_KEY}}/{${Constants.POST_ID_KEY}}/{${Constants.POST_OWNER_NAME_KEY}}/{${Constants.COMMENT_OWNER_NAME_KEY}}/{${Constants.COMMENT_OWNER_ID_KEY}}",
            arguments = listOf(
                navArgument(Constants.COMMENT_ID_KEY){ NavType.StringType },
                navArgument(Constants.POST_ID_KEY){ NavType.StringType },
                navArgument(Constants.POST_OWNER_NAME_KEY){ NavType.StringType },
                navArgument(Constants.COMMENT_OWNER_NAME_KEY){ NavType.StringType },
                navArgument(Constants.COMMENT_OWNER_ID_KEY){ NavType.StringType }
            )
        ){
            val commentId = it.arguments?.getString(Constants.COMMENT_ID_KEY)!!
            val postId = it.arguments?.getString(Constants.POST_ID_KEY)!!
            val postOwnerName = it.arguments?.getString(Constants.POST_OWNER_NAME_KEY)!!
            val commentOwnerName = it.arguments?.getString(Constants.COMMENT_OWNER_NAME_KEY)!!
            val commentOwnerId = it.arguments?.getString(Constants.COMMENT_OWNER_ID_KEY)!!
            CommentRepliesScreen(
                commentId = commentId,
                postId = postId,
                postOwnerName = postOwnerName,
                commentOwnerName = commentOwnerName,
                commentOwnerId = commentOwnerId,
                onBackClicked = { navController.navigateUp() },
                onSearchClicked = { navController.navigate(ScreenRoutes.Search.destination){ launchSingleTop = true } },
                commonViewModel = viewModel,
                isDarkTheme = isDarkTheme,
                onUserProfileImageClicked = {
                    navController.navigate( "${ScreenRoutes.Profile2.destination}/$it" ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = ScreenRoutes.ChatWithGemini.destination
        ) {
            ChatWithGeminiScreen(isDarkTheme = isDarkTheme, onBackClicked = { navController.navigateUp() })
        }

        composable(
            route = "${ScreenRoutes.Chat.destination}/{${Constants.USER_ID_KEY}}",
            arguments = listOf(
                navArgument(Constants.USER_ID_KEY){ NavType.StringType }
            )
        ) {
            val receiverId = it.arguments?.getString(Constants.USER_ID_KEY)!!
            ChatScreen(
                receiverId = receiverId,
                isDarkTheme = isDarkTheme,
                onBackClicked = { navController.navigateUp() },
                onPlayVideo = {
                    navController.navigate("${ScreenRoutes.Player.destination}/$it") {
                        launchSingleTop = true
                    }
                }, onPlayChatVideo = {
                    navController.navigate("${ScreenRoutes.ChatVideoPayer.destination}/$it") {
                        launchSingleTop = true
                    }
                }, onViewChatImage = {
                    navController.navigate("${ScreenRoutes.ChatImageViewer.destination}/$it") {
                        launchSingleTop = true
                    }
                }, onViewUserProfile = {
                    navController.navigate("${ScreenRoutes.Profile2.destination}/$it") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "${ScreenRoutes.ChatVideoPayer.destination}/{${Constants.VIDEO_URL_KEY}}",
            arguments = listOf(
                navArgument(Constants.VIDEO_URL_KEY){ NavType.StringType }
            )
        ) {
            val videoUrl = it.arguments?.getString(Constants.VIDEO_URL_KEY)!!
            ChatVideoPlayer(onBackClicked = { navController.popBackStack() }, videoUrl = videoUrl )
        }
        
        composable(
            route = "${ScreenRoutes.ChatImageViewer.destination}/{${Constants.IMAGE_URL_KEY}}",
            arguments = listOf(
                navArgument(Constants.IMAGE_URL_KEY){ NavType.StringType }
            )
        ) {
            val imageUrl = it.arguments?.getString(Constants.IMAGE_URL_KEY)!!
            ChatImageViewer(imageUrl = imageUrl)
        }
    }
}