package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.VideoFeedCard
import com.example.ui.components.VidioTopAppBar
import com.example.ui.screens.AdminModerationScreen
import com.example.ui.screens.AdminPinDialog
import com.example.ui.screens.CreateActionSheetDialog
import com.example.ui.screens.EmailAccountDialog
import com.example.ui.screens.LiveHubScreen
import com.example.ui.screens.NotificationsDialog
import com.example.ui.screens.StartLiveStreamDialog
import com.example.ui.screens.StudioAnalyticsAndMonetizationScreen
import com.example.ui.screens.SubscriptionsScreen
import com.example.ui.screens.UploadVideoDialog
import com.example.ui.screens.VideoPlayerAndLiveSheet
import com.example.ui.screens.YouLibraryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainTab
import com.example.viewmodel.VidioViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vidioViewModel: VidioViewModel = viewModel()
            val isDarkMode by vidioViewModel.isDarkMode.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MyApplicationTheme(darkTheme = isDarkMode) {
                    VidioTubeApp(vidioViewModel = vidioViewModel)
                }
            }
        }
    }
}

@Composable
fun VidioTubeApp(vidioViewModel: VidioViewModel) {
    val allVideos by vidioViewModel.allVideos.collectAsStateWithLifecycle()
    val downloadedVideos by vidioViewModel.downloadedVideos.collectAsStateWithLifecycle()
    val savedVideos by vidioViewModel.savedVideos.collectAsStateWithLifecycle()
    val allChannels by vidioViewModel.allChannels.collectAsStateWithLifecycle()
    val allNotifications by vidioViewModel.allNotifications.collectAsStateWithLifecycle()
    val allComments by vidioViewModel.allComments.collectAsStateWithLifecycle()

    val currentTab by vidioViewModel.currentTab.collectAsStateWithLifecycle()
    val selectedCategory by vidioViewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by vidioViewModel.searchQuery.collectAsStateWithLifecycle()
    val isDarkMode by vidioViewModel.isDarkMode.collectAsStateWithLifecycle()
    val isOfflineMode by vidioViewModel.isOfflineMode.collectAsStateWithLifecycle()
    val activeVideoId by vidioViewModel.activeVideoId.collectAsStateWithLifecycle()

    val currentUserEmail by vidioViewModel.currentUserEmail.collectAsStateWithLifecycle()
    val currentUserName by vidioViewModel.currentUserName.collectAsStateWithLifecycle()
    val currentUserHandle by vidioViewModel.currentUserHandle.collectAsStateWithLifecycle()

    val showCreateSheet by vidioViewModel.showCreateSheet.collectAsStateWithLifecycle()
    val showUploadDialog by vidioViewModel.showUploadDialog.collectAsStateWithLifecycle()
    val showGoLiveDialog by vidioViewModel.showGoLiveDialog.collectAsStateWithLifecycle()
    val showNotificationsSheet by vidioViewModel.showNotificationsSheet.collectAsStateWithLifecycle()
    val showAccountSheet by vidioViewModel.showAccountSheet.collectAsStateWithLifecycle()
    val showAdminPinDialog by vidioViewModel.showAdminPinDialog.collectAsStateWithLifecycle()
    val showAdminPanel by vidioViewModel.showAdminPanel.collectAsStateWithLifecycle()

    val statusBannerMessage by vidioViewModel.statusBannerMessage.collectAsStateWithLifecycle()

    val preRollAds by vidioViewModel.preRollAdsEnabled.collectAsStateWithLifecycle()
    val midRollAds by vidioViewModel.midRollAdsEnabled.collectAsStateWithLifecycle()
    val sponsorCards by vidioViewModel.sponsorCardsEnabled.collectAsStateWithLifecycle()
    val superChat by vidioViewModel.superChatEnabled.collectAsStateWithLifecycle()

    var initialLiveSourceType by remember { mutableStateOf("CAMERA") }

    val unreadCount = allNotifications.count { !it.isRead }

    LaunchedEffect(statusBannerMessage) {
        if (statusBannerMessage != null) {
            delay(3200L)
            vidioViewModel.clearBannerMessage()
        }
    }

    val activeVideo = allVideos.find { it.id == activeVideoId }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (activeVideo == null && !showAdminPanel) {
                VidioTopAppBar(
                    isDarkMode = isDarkMode,
                    isOfflineMode = isOfflineMode,
                    unreadNotificationsCount = unreadCount,
                    currentUserEmail = currentUserEmail,
                    searchQuery = searchQuery,
                    onSearchQueryChange = vidioViewModel::updateSearchQuery,
                    onToggleDarkMode = vidioViewModel::toggleDarkMode,
                    onToggleOfflineMode = vidioViewModel::toggleOfflineMode,
                    onOpenGoLive = {
                        initialLiveSourceType = "CAMERA"
                        vidioViewModel.setShowGoLiveDialog(true)
                    },
                    onOpenNotifications = { vidioViewModel.setShowNotificationsSheet(true) },
                    onOpenAccountSheet = { vidioViewModel.setShowAccountSheet(true) },
                    onFiveSecondHoldAdmin = vidioViewModel::triggerAdminPinPrompt
                )
            }
        },
        bottomBar = {
            if (activeVideo == null && !showAdminPanel) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == MainTab.HOME,
                        onClick = { vidioViewModel.selectTab(MainTab.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                        label = { Text("الرئيسية") },
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.LIVE,
                        onClick = { vidioViewModel.selectTab(MainTab.LIVE) },
                        icon = { Icon(Icons.Default.Sensors, contentDescription = "بث مباشر") },
                        label = { Text("مباشر") },
                        modifier = Modifier.testTag("nav_live")
                    )
                    // Authentic YouTube Center (+) Create Button
                    NavigationBarItem(
                        selected = false,
                        onClick = { vidioViewModel.setShowCreateSheet(true) },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.5.dp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "إنشاء محتوى أو بث مباشر",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        },
                        label = { Text("إنشاء") },
                        modifier = Modifier.testTag("nav_create_plus")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.SUBSCRIPTIONS,
                        onClick = { vidioViewModel.selectTab(MainTab.SUBSCRIPTIONS) },
                        icon = { Icon(Icons.Default.Subscriptions, contentDescription = "الاشتراكات") },
                        label = { Text("الاشتراكات") },
                        modifier = Modifier.testTag("nav_subscriptions")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.LIBRARY_YOU,
                        onClick = { vidioViewModel.selectTab(MainTab.LIBRARY_YOU) },
                        icon = { Icon(Icons.Default.VideoLibrary, contentDescription = "أنت والمكتبة") },
                        label = { Text("أنت") },
                        modifier = Modifier.testTag("nav_you_library")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.STUDIO,
                        onClick = { vidioViewModel.selectTab(MainTab.STUDIO) },
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "الاستوديو") },
                        label = { Text("الاستوديو") },
                        modifier = Modifier.testTag("nav_studio")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showAdminPanel) {
                AdminModerationScreen(
                    channels = allChannels,
                    videos = allVideos,
                    comments = allComments,
                    onCloseAdmin = vidioViewModel::closeAdminPanel,
                    onToggleBanChannel = vidioViewModel::adminToggleBanChannel,
                    onSendWarningToChannel = vidioViewModel::adminSendWarningToChannel,
                    onDeleteChannel = vidioViewModel::adminDeleteChannelAndVideos,
                    onDeleteVideo = vidioViewModel::adminDeleteVideo,
                    onDeleteComment = vidioViewModel::adminDeleteComment
                )
            } else if (activeVideo != null) {
                val comments by vidioViewModel.getCommentsForVideo(activeVideo.id)
                    .collectAsStateWithLifecycle(initialValue = emptyList())
                val liveChat by vidioViewModel.getLiveChatForVideo(activeVideo.id)
                    .collectAsStateWithLifecycle(initialValue = emptyList())
                val channel = allChannels.find { it.id == activeVideo.channelId }

                VideoPlayerAndLiveSheet(
                    video = activeVideo,
                    channel = channel,
                    comments = comments,
                    liveChatMessages = liveChat,
                    onBack = vidioViewModel::closeVideoPlayer,
                    onToggleLike = { vidioViewModel.toggleLikeVideo(activeVideo) },
                    onToggleDislike = { vidioViewModel.toggleDislikeVideo(activeVideo) },
                    onToggleSave = { vidioViewModel.toggleSaveVideo(activeVideo) },
                    onToggleDownload = { quality -> vidioViewModel.toggleDownloadVideo(activeVideo, quality) },
                    onToggleSubscribe = {
                        if (channel != null) vidioViewModel.toggleSubscribe(channel)
                    },
                    onToggleBell = {
                        if (channel != null) vidioViewModel.toggleChannelNotificationBell(channel)
                    },
                    onAddComment = { text -> vidioViewModel.addComment(activeVideo.id, text) },
                    onLikeComment = vidioViewModel::toggleCommentLike,
                    onHeartComment = vidioViewModel::toggleCommentHeart,
                    onSendLiveChat = { msg, superChatUsd ->
                        vidioViewModel.sendLiveChatMessage(activeVideo, msg, superChatUsd)
                    },
                    onRecordAdImpression = {
                        vidioViewModel.recordAdImpressionAndEarn(activeVideo)
                    },
                    onToggleMonetization = {
                        vidioViewModel.toggleVideoMonetization(activeVideo)
                    },
                    onSwitchLiveSource = { newSource, useFront ->
                        vidioViewModel.toggleLiveSourceOnActiveVideo(activeVideo, newSource, useFront)
                    }
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Offline Mode Banner
                    AnimatedVisibility(visible = isOfflineMode) {
                        Surface(
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { vidioViewModel.toggleOfflineMode() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CloudOff,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "وضع المشاهدة بدون إنترنت مفعل • تُعرض الفيديوهات المحملة فقط",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "إلغاء",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    when (currentTab) {
                        MainTab.HOME -> {
                            CategoryFilterRow(
                                selectedCategory = selectedCategory,
                                onSelectCategory = vidioViewModel::selectCategory
                            )

                            val baseVideos = if (isOfflineMode) downloadedVideos else allVideos
                            val filteredVideos = baseVideos.filter { v ->
                                val matchesCategory = when (selectedCategory) {
                                    "الكل" -> true
                                    "بث مباشر 🔴" -> v.isLive
                                    "المحفوظات 🔖" -> v.isSaved
                                    "فيديوهاتي" -> v.isMyUpload
                                    else -> v.category == selectedCategory
                                }
                                val matchesQuery = searchQuery.isBlank() ||
                                    v.title.contains(searchQuery, ignoreCase = true) ||
                                    v.channelName.contains(searchQuery, ignoreCase = true) ||
                                    v.category.contains(searchQuery, ignoreCase = true)
                                matchesCategory && matchesQuery
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("home_video_feed"),
                                contentPadding = PaddingValues(bottom = 88.dp)
                            ) {
                                items(filteredVideos, key = { it.id }) { video ->
                                    val ch = allChannels.find { it.id == video.channelId }
                                    VideoFeedCard(
                                        video = video,
                                        channel = ch,
                                        onVideoClick = { vidioViewModel.openVideo(video) },
                                        onSubscribeClick = {
                                            if (ch != null) vidioViewModel.toggleSubscribe(ch)
                                        },
                                        onSaveClick = {
                                            vidioViewModel.toggleSaveVideo(video)
                                        },
                                        onDownloadClick = {
                                            vidioViewModel.toggleDownloadVideo(video)
                                        },
                                        onLikeClick = {
                                            vidioViewModel.toggleLikeVideo(video)
                                        }
                                    )
                                }
                            }
                        }

                        MainTab.SUBSCRIPTIONS -> {
                            SubscriptionsScreen(
                                channels = allChannels,
                                videos = allVideos,
                                onOpenVideo = vidioViewModel::openVideo,
                                onToggleSubscribe = vidioViewModel::toggleSubscribe,
                                onToggleBell = vidioViewModel::toggleChannelNotificationBell,
                                onToggleSave = vidioViewModel::toggleSaveVideo,
                                onToggleDownload = { vidioViewModel.toggleDownloadVideo(it) },
                                onToggleLike = vidioViewModel::toggleLikeVideo
                            )
                        }

                        MainTab.LIVE -> {
                            LiveHubScreen(
                                videos = allVideos,
                                channels = allChannels,
                                onOpenVideo = vidioViewModel::openVideo,
                                onStartNewLive = {
                                    initialLiveSourceType = "CAMERA"
                                    vidioViewModel.setShowGoLiveDialog(true)
                                },
                                onToggleSubscribe = vidioViewModel::toggleSubscribe,
                                onToggleSave = vidioViewModel::toggleSaveVideo,
                                onToggleDownload = { vidioViewModel.toggleDownloadVideo(it) },
                                onToggleLike = vidioViewModel::toggleLikeVideo
                            )
                        }

                        MainTab.LIBRARY_YOU -> {
                            YouLibraryScreen(
                                currentUserName = currentUserName,
                                currentUserEmail = currentUserEmail,
                                currentUserHandle = currentUserHandle,
                                savedVideos = savedVideos,
                                downloadedVideos = downloadedVideos,
                                allVideos = allVideos,
                                isOfflineMode = isOfflineMode,
                                onToggleOfflineMode = vidioViewModel::toggleOfflineMode,
                                onOpenAccountSheet = { vidioViewModel.setShowAccountSheet(true) },
                                onOpenUpload = { vidioViewModel.setShowUploadDialog(true) },
                                onOpenGoLive = { vidioViewModel.setShowGoLiveDialog(true) },
                                onOpenVideo = vidioViewModel::openVideo,
                                onToggleSave = vidioViewModel::toggleSaveVideo,
                                onToggleDownload = { vidioViewModel.toggleDownloadVideo(it) }
                            )
                        }

                        MainTab.STUDIO -> {
                            StudioAnalyticsAndMonetizationScreen(
                                videos = allVideos,
                                channels = allChannels,
                                preRollAdsEnabled = preRollAds,
                                midRollAdsEnabled = midRollAds,
                                sponsorCardsEnabled = sponsorCards,
                                superChatEnabled = superChat,
                                onTogglePreRoll = vidioViewModel::togglePreRollAds,
                                onToggleMidRoll = vidioViewModel::toggleMidRollAds,
                                onToggleSponsorCards = vidioViewModel::toggleSponsorCards,
                                onToggleSuperChat = vidioViewModel::toggleSuperChat,
                                onToggleVideoMonetization = vidioViewModel::toggleVideoMonetization,
                                onSimulateAdCampaign = vidioViewModel::simulateAdCampaignBoost,
                                onOpenUpload = { vidioViewModel.setShowUploadDialog(true) },
                                onOpenGoLive = { vidioViewModel.setShowGoLiveDialog(true) },
                                onOpenVideo = vidioViewModel::openVideo
                            )
                        }
                    }
                }
            }

            // Floating Toast / Status Banner
            AnimatedVisibility(
                visible = statusBannerMessage != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(14.dp),
                    tonalElevation = 8.dp
                ) {
                    Text(
                        text = statusBannerMessage.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }

    if (showCreateSheet) {
        CreateActionSheetDialog(
            onDismiss = { vidioViewModel.setShowCreateSheet(false) },
            onSelectUploadVideo = {
                vidioViewModel.setShowCreateSheet(false)
                vidioViewModel.setShowUploadDialog(true)
            },
            onSelectGoLiveCamera = {
                vidioViewModel.setShowCreateSheet(false)
                initialLiveSourceType = "CAMERA"
                vidioViewModel.setShowGoLiveDialog(true)
            },
            onSelectGoLiveScreen = {
                vidioViewModel.setShowCreateSheet(false)
                initialLiveSourceType = "SCREEN"
                vidioViewModel.setShowGoLiveDialog(true)
            }
        )
    }

    if (showUploadDialog) {
        UploadVideoDialog(
            onDismiss = { vidioViewModel.setShowUploadDialog(false) },
            onPublish = vidioViewModel::uploadNewVideo
        )
    }

    if (showGoLiveDialog) {
        StartLiveStreamDialog(
            initialSourceType = initialLiveSourceType,
            onDismiss = { vidioViewModel.setShowGoLiveDialog(false) },
            onStartLive = vidioViewModel::startNewLiveStream
        )
    }

    if (showAccountSheet) {
        EmailAccountDialog(
            currentEmail = currentUserEmail,
            currentName = currentUserName,
            currentHandle = currentUserHandle,
            onDismiss = { vidioViewModel.setShowAccountSheet(false) },
            onSaveAccount = vidioViewModel::updateEmailAccount
        )
    }

    if (showAdminPinDialog) {
        AdminPinDialog(
            onDismiss = vidioViewModel::dismissAdminPinPrompt,
            onVerifyPin = vidioViewModel::verifyAdminPin
        )
    }

    if (showNotificationsSheet) {
        NotificationsDialog(
            notifications = allNotifications,
            onDismiss = { vidioViewModel.setShowNotificationsSheet(false) },
            onMarkAllRead = vidioViewModel::markAllNotificationsRead,
            onSendTestNotification = vidioViewModel::sendTestSubscriberNotification,
            onNotificationClick = { notif ->
                vidioViewModel.markNotificationRead(notif.id)
                vidioViewModel.setShowNotificationsSheet(false)
                val targetVideo = allVideos.find { it.id == notif.relatedVideoId }
                if (targetVideo != null) {
                    vidioViewModel.openVideo(targetVideo)
                }
            }
        )
    }
}
