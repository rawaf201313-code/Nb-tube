package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.ChannelEntity
import com.example.data.CommentEntity
import com.example.data.LiveChatMessageEntity
import com.example.data.VideoEntity
import com.example.ui.components.formatCount
import com.example.ui.components.formatDuration
import com.example.ui.components.getThumbnailResId
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerAndLiveSheet(
    video: VideoEntity,
    channel: ChannelEntity?,
    comments: List<CommentEntity>,
    liveChatMessages: List<LiveChatMessageEntity>,
    onBack: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleDislike: () -> Unit,
    onToggleSave: () -> Unit,
    onToggleDownload: (String) -> Unit,
    onToggleSubscribe: () -> Unit,
    onToggleBell: () -> Unit,
    onAddComment: (String) -> Unit,
    onLikeComment: (CommentEntity) -> Unit,
    onHeartComment: (CommentEntity) -> Unit,
    onSendLiveChat: (String, Double) -> Unit,
    onRecordAdImpression: () -> Unit,
    onToggleMonetization: () -> Unit,
    onSwitchLiveSource: (String, Boolean) -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isPlaying by remember { mutableStateOf(true) }
    var progressFraction by remember { mutableFloatStateOf(0.18f) }
    var playbackSpeed by remember { mutableStateOf("1.0x") }
    var selectedQuality by remember { mutableStateOf(video.downloadQuality) }
    var showQualityDialog by remember { mutableStateOf(false) }
    var showDownloadQualityDialog by remember { mutableStateOf(false) }

    // Camera permission state for Live Camera mode
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(video.isLive, video.liveSourceType) {
        if (video.isLive && video.liveSourceType == "CAMERA" && !hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Pre-roll Ad Simulation on Monetized Videos
    var showAdOverlay by remember(video.id) { mutableStateOf(video.isMonetized && !video.isLive) }
    var adSecondsLeft by remember(video.id) { mutableIntStateOf(5) }

    var commentText by remember { mutableStateOf("") }
    var liveChatInput by remember { mutableStateOf("") }
    var floatingReactionCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(isPlaying, showAdOverlay, video.id) {
        while (isPlaying) {
            delay(1000L)
            if (showAdOverlay) {
                if (adSecondsLeft > 0) {
                    adSecondsLeft -= 1
                }
            } else if (!video.isLive) {
                progressFraction = (progressFraction + 0.01f).coerceAtMost(1f)
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "cinema_wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_phase"
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("video_player_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 16:9 Interactive Video / Live Stream Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                if (video.isLive && video.liveSourceType == "CAMERA") {
                    // LIVE CAMERA BROADCAST MODE
                    Image(
                        painter = painterResource(id = getThumbnailResId(video.thumbnailKey)),
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (hasCameraPermission) {
                        AndroidView(
                            factory = { ctx ->
                                PreviewView(ctx).apply {
                                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                }
                            },
                            update = { previewView ->
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                                cameraProviderFuture.addListener({
                                    try {
                                        val cameraProvider = cameraProviderFuture.get()
                                        val preview = Preview.Builder().build().also {
                                            it.surfaceProvider = previewView.surfaceProvider
                                        }
                                        val selector = if (video.useFrontCamera) {
                                            CameraSelector.DEFAULT_FRONT_CAMERA
                                        } else {
                                            CameraSelector.DEFAULT_BACK_CAMERA
                                        }
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            selector,
                                            preview
                                        )
                                    } catch (_: Exception) {
                                        // Fallback gracefully if virtual camera is busy
                                    }
                                }, ContextCompat.getMainExecutor(context))
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    // Camera HUD Overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.72f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = Color(0xFFFF0000),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (video.useFrontCamera) "بث الكاميرا الأمامية نشط • 1080p"
                                else "بث الكاميرا الخلفية نشط • 1080p",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (video.isLive && video.liveSourceType == "SCREEN") {
                    // LIVE PHONE SCREEN BROADCAST MODE (بث شاشة الجوال)
                    Image(
                        painter = painterResource(id = getThumbnailResId(video.thumbnailKey)),
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Screen Broadcast Frame & Interactive HUD
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, Color(0xFF3EA6FF).copy(alpha = 0.8f))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = Color(0xFF3EA6FF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بث شاشة الجوال مباشر • 60 FPS • صوت النظام والمايك",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (!video.customMediaUri.isNullOrBlank() && video.customMediaUri.startsWith("content://")) {
                    Image(
                        painter = painterResource(id = getThumbnailResId(video.thumbnailKey)),
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                try {
                                    setVideoURI(Uri.parse(video.customMediaUri))
                                    setOnPreparedListener { mp ->
                                        mp.isLooping = true
                                        start()
                                    }
                                } catch (_: Exception) {}
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = getThumbnailResId(video.thumbnailKey)),
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Dynamic Visualizer & Cinema Overlay Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    drawRect(color = Color.Black.copy(alpha = 0.28f))
                    if (isPlaying) {
                        val barCount = 28
                        val barWidth = w / (barCount * 2.2f)
                        for (i in 0 until barCount) {
                            val dynamicHeight = (10f + ((i * 17 + (wavePhase * 100).toInt()) % 30))
                            drawLine(
                                color = if (video.isLive) Color(0xFFFF0000).copy(alpha = 0.65f)
                                else Color(0xFF3EA6FF).copy(alpha = 0.55f),
                                start = Offset(x = (i * 2.1f + 1f) * barWidth, y = h - 6f),
                                end = Offset(x = (i * 2.1f + 1f) * barWidth, y = h - 6f - dynamicHeight),
                                strokeWidth = barWidth
                            )
                        }
                    }
                }

                // Top Player Bar: Back Button + Live Source Switcher (Screen vs Camera) or Quality/Speed
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("player_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (video.isLive) {
                            // Live Badge
                            Surface(
                                color = Color(0xFFFF0000),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sensors,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "مباشر • ${video.liveViewersCount + floatingReactionCount}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Switch between Phone Screen Broadcast & Camera Broadcast live!
                            Surface(
                                color = Color.Black.copy(alpha = 0.72f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .clickable {
                                        val nextSource = if (video.liveSourceType == "SCREEN") "CAMERA" else "SCREEN"
                                        onSwitchLiveSource(nextSource, video.useFrontCamera)
                                    }
                                    .testTag("switch_live_source_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (video.liveSourceType == "SCREEN") Icons.Default.PhoneAndroid
                                        else Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = Color(0xFF3EA6FF),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (video.liveSourceType == "SCREEN") "وضع: شاشة الجوال" else "وضع: الكاميرا",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (video.liveSourceType == "CAMERA") {
                                Surface(
                                    color = Color.Black.copy(alpha = 0.72f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.clickable {
                                        onSwitchLiveSource("CAMERA", !video.useFrontCamera)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cameraswitch,
                                        contentDescription = "تبديل الكاميرا",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .size(16.dp)
                                    )
                                }
                            }
                        } else {
                            // Quality chip
                            Surface(
                                color = Color.Black.copy(alpha = 0.65f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { showQualityDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HighQuality,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = selectedQuality.substringBefore(" "),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Speed chip
                            Surface(
                                color = Color.Black.copy(alpha = 0.65f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable {
                                    playbackSpeed = when (playbackSpeed) {
                                        "1.0x" -> "1.25x"
                                        "1.25x" -> "1.5x"
                                        "1.5x" -> "2.0x"
                                        else -> "1.0x"
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = playbackSpeed,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Center Play/Pause Button
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Pre-roll In-App Ad Monetization Overlay
                androidx.compose.animation.AnimatedVisibility(
                    visible = showAdOverlay,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    Surface(
                        color = Color(0xFF0F0F0F).copy(alpha = 0.94f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    color = Color(0xFFF59E0B),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "إعلان",
                                        color = Color.Black,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "يدعم صانع المحتوى (CPM $${String.format("%.2f", video.cpmUsd)})",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Button(
                                onClick = {
                                    onRecordAdImpression()
                                    showAdOverlay = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF0000)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("skip_ad_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (adSecondsLeft > 0) "تخطي الإعلان ($adSecondsLeft)" else "تخطي الإعلان +ربح",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Timeline Scrubber for non-live videos
            if (!video.isLive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentSec = (video.durationSeconds * progressFraction).toInt()
                    Text(
                        text = formatDuration(currentSec),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = progressFraction,
                        onValueChange = { progressFraction = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF0000),
                            activeTrackColor = Color(0xFFFF0000)
                        )
                    )
                    Text(
                        text = formatDuration(video.durationSeconds),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Scrollable Video Info + Channel + Action Strip + Comments / Live Chat
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${formatCount(video.viewsCount)} مشاهدة • ${video.publishedAtText} • نسبة المشاهدة ${video.avgRetentionPercent}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Channel Subscription & Notification Bell Row (YouTube style)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(video.channelAvatarColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = video.channelName.take(1),
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = video.channelName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${formatCount((channel?.subscribersCount ?: 148500).toLong())} مشترك",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (channel?.isSubscribed == true) {
                                IconButton(
                                    onClick = onToggleBell,
                                    modifier = Modifier.testTag("player_channel_bell_button")
                                ) {
                                    Icon(
                                        imageVector = if (channel.notificationsEnabled) Icons.Default.NotificationsActive
                                        else Icons.Default.NotificationsOff,
                                        contentDescription = "تبديل إشعارات القناة",
                                        tint = if (channel.notificationsEnabled) MaterialTheme.colorScheme.onBackground
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = onToggleSubscribe,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (channel?.isSubscribed == true)
                                        MaterialTheme.colorScheme.surfaceVariant
                                    else MaterialTheme.colorScheme.onBackground,
                                    contentColor = if (channel?.isSubscribed == true)
                                        MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.background
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.testTag("player_subscribe_button")
                            ) {
                                Text(
                                    text = if (channel?.isSubscribed == true) "مشترك ✓" else "اشتراك",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // YouTube Scrollable Pill Action Row (Like, Dislike, Save to Library, Download Offline, Monetize)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Like
                        OutlinedButton(
                            onClick = onToggleLike,
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("player_like_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "إعجاب",
                                tint = if (video.isLiked) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(formatCount(video.likesCount.toLong()))
                        }

                        // Dislike
                        OutlinedButton(
                            onClick = onToggleDislike,
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbDown,
                                contentDescription = "عدم إعجاب",
                                tint = if (video.isDisliked) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Save Video to Library ("حفظ الفيديو")
                        OutlinedButton(
                            onClick = onToggleSave,
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("player_save_button")
                        ) {
                            Icon(
                                imageVector = if (video.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "حفظ الفيديو",
                                tint = if (video.isSaved) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (video.isSaved) "محفوظ في المكتبة ✓" else "حفظ الفيديو")
                        }

                        // Offline Download
                        OutlinedButton(
                            onClick = {
                                if (video.isDownloaded) onToggleDownload(video.downloadQuality)
                                else showDownloadQualityDialog = true
                            },
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("player_download_button")
                        ) {
                            Icon(
                                imageVector = if (video.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                                contentDescription = "تنزيل بدون إنترنت",
                                tint = if (video.isDownloaded) Color(0xFF3EA6FF) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (video.isDownloaded) "تم التنزيل (${video.downloadQuality.substringBefore(" ")})" else "تنزيل")
                        }

                        // Monetization Toggle
                        OutlinedButton(
                            onClick = onToggleMonetization,
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("player_monetize_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "الربح من الإعلانات",
                                tint = if (video.isMonetized) Color(0xFF2BA640) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (video.isMonetized) "$${String.format("%.1f", video.adRevenueUsd)}" else "إعلانات معطلة")
                        }
                    }
                }

                // Video Description & Retention Box
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📊 نسبة مشاهدة الفيديو: ${video.avgRetentionPercent}%",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "عائد الإعلانات: $${String.format("%.2f", video.adRevenueUsd)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF2BA640),
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (video.avgRetentionPercent / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFFFF0000)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = video.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // LIVE CHAT or COMMENTS
                if (video.isLive) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💬 الدردشة المباشرة (${liveChatMessages.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                            TextButton(onClick = { floatingReactionCount += 5 }) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color(0xFFFF0000),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إرسال قلب ❤️")
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = liveChatInput,
                                onValueChange = { liveChatInput = it },
                                placeholder = { Text("الدردشة المباشرة...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("live_chat_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(24.dp)
                            )
                            IconButton(
                                onClick = {
                                    if (liveChatInput.isNotBlank()) {
                                        onSendLiveChat(liveChatInput, 0.0)
                                        liveChatInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF0000))
                                    .testTag("send_live_chat_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "إرسال",
                                    tint = Color.White
                                )
                            }
                            Button(
                                onClick = {
                                    onSendLiveChat(
                                        liveChatInput.ifBlank { "دعم مميز للبث المباشر! 🚀" },
                                        10.0
                                    )
                                    liveChatInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2BA640)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("super_chat_10_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("10$", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(liveChatMessages, key = { it.id }) { msg ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (msg.superChatAmountUsd > 0)
                                    Color(0xFF064E3B)
                                else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(msg.senderColor)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = msg.senderName.take(1),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = msg.senderName,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (msg.superChatAmountUsd > 0) Color(0xFFA7F3D0) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (msg.superChatAmountUsd > 0) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = Color(0xFF10B981),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "Super Chat $${msg.superChatAmountUsd.toInt()}",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = msg.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (msg.superChatAmountUsd > 0) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                } else {
                    item {
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "التعليقات (${comments.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                placeholder = { Text("إضافة تعليق...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("comment_input_field"),
                                singleLine = true,
                                shape = RoundedCornerShape(24.dp)
                            )
                            IconButton(
                                onClick = {
                                    if (commentText.isNotBlank()) {
                                        onAddComment(commentText)
                                        commentText = ""
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF0000))
                                    .testTag("submit_comment_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "إرسال التعليق",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    items(comments, key = { it.id }) { comment ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(Color(comment.authorAvatarColor)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = comment.authorName.take(1),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = comment.authorName,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "• ${comment.timestampText}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (comment.isPinned) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "📌 مثبت",
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = comment.content,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { onLikeComment(comment) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ThumbUp,
                                            contentDescription = "إعجاب بالتعليق",
                                            tint = if (comment.isLiked) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = comment.likesCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { onHeartComment(comment) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "قلب من صانع المحتوى",
                                            tint = if (comment.isCreatorHearted) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (comment.isCreatorHearted) "أعجب القناة ❤️" else "تمييز بقلب",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showQualityDialog) {
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = { Text("جودة تشغيل الفيديو", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf("2160p 4K HDR", "1080p Full HD", "720p HD", "480p توفير البيانات").forEach { q ->
                        TextButton(
                            onClick = {
                                selectedQuality = q
                                showQualityDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = q, fontWeight = if (selectedQuality == q) FontWeight.ExtraBold else FontWeight.Normal)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQualityDialog = false }) { Text("إغلاق") }
            }
        )
    }

    if (showDownloadQualityDialog) {
        AlertDialog(
            onDismissRequest = { showDownloadQualityDialog = false },
            title = { Text("اختر جودة التنزيل للمشاهدة بدون إنترنت", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "1080p Full HD" to "225 MB - دقة فائقة الوضوح",
                        "720p HD" to "130 MB - متوازنة وموصى بها",
                        "360p توفير المساحة" to "65 MB - سريعة التحميل"
                    ).forEach { (qual, desc) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onToggleDownload(qual)
                                    showDownloadQualityDialog = false
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(qual, fontWeight = FontWeight.Bold)
                                Text(desc, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDownloadQualityDialog = false }) { Text("إلغاء") }
            }
        )
    }
}
