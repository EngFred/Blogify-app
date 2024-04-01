package com.engineerfred.kotlin.next.presentation.navigation

sealed class ScreenRoutes( val destination: String ){

    //bottom navigation screens
    data object Feeds : ScreenRoutes( Destinations.Feeds.name )
    data object People : ScreenRoutes( Destinations.People.name )
    data object Message: ScreenRoutes( Destinations.Message.name )
    data object Notifications : ScreenRoutes( Destinations.Notifications.name )
    data object Reels : ScreenRoutes( Destinations.Reels.name )

    //other home screens
    data object Search : ScreenRoutes( Destinations.Search.name )
    data object Settings : ScreenRoutes( Destinations.Settings.name )
    data object Create : ScreenRoutes( Destinations.Create.name )
    data object Player : ScreenRoutes( Destinations.Player.name )
    data object Profile : ScreenRoutes( Destinations.Profile.name )
    data object Profile2 : ScreenRoutes( Destinations.Profile2.name )
    data object TagFriends : ScreenRoutes( Destinations.TagFriends.name )
    data object AddFeeling : ScreenRoutes( Destinations.AddFeeling.name )
    data object AddLocation : ScreenRoutes( Destinations.AddLocation.name )
    data object PostDetail : ScreenRoutes( Destinations.PostDetail.name )
    data object EditProfile : ScreenRoutes( Destinations.EditProfile.name )
    data object CommentReplies : ScreenRoutes( Destinations.CommentReplies.name )
    data object ChatWithGemini : ScreenRoutes( Destinations.ChatWithGemini.name )
    data object Chat : ScreenRoutes( Destinations.Chat.name )
    data object ChatVideoPayer : ScreenRoutes( Destinations.ChatVideoPlayer.name )
    data object ChatImageViewer : ScreenRoutes( Destinations.ChatImageViewer.name )

    //auth
    data object Login : ScreenRoutes( Destinations.Login.name )
    data object JoinNext : ScreenRoutes( Destinations.JoinNext.name )
    data object EnterName : ScreenRoutes( Destinations.EnterName.name )
    data object EnterEmail : ScreenRoutes( Destinations.EnterEmail.name )
    data object CreatePassword : ScreenRoutes( Destinations.CreatePassword.name )
    data object SaveLoginInfo : ScreenRoutes( Destinations.SaveLoginInfo.name )
}

enum class Destinations{
    Feeds,
    People,
    Message,
    Notifications,
    Reels,
    Search,
    Settings,
    Create,
    Player,
    Profile,
    Profile2,
    TagFriends,
    AddFeeling,
    AddLocation,
    PostDetail,
    Login,
    JoinNext,
    EnterName,
    EnterEmail,
    CreatePassword,
    SaveLoginInfo,
    EditProfile,
    CommentReplies,
    ChatWithGemini,
    Chat,
    ChatVideoPlayer,
    ChatImageViewer,
}
