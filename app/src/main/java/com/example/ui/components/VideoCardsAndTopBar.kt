package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.ChannelEntity
import com.example.data.VideoEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun getThumbnailResId(key: String): Int {
    return when (key) {
        "tech" -> R.drawable.img_thumb_tech_review
        "gaming" -> R.drawable.img_thumb_gaming_live
        "doc" -> R.drawable.img_thumb_cinema_doc
        "podcast" -> R.drawable.img_thumb_studio_podcast
        else -> R.drawable.img_thumb_tech_review
    }
}

fun formatDuration(seconds: Int): String {
    if (seconds <= 0) return "مباشر"
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}

fun formatCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format("%.1f مليون", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1f ألف", count / 1_000.0)
        else -> count.toString()
    }
}

@Composable
fun VidioTopAppBar(
    isDarkMode: Boolean,
    isOfflineMode: Boolean,
    unreadNotificationsCount: Int,
    currentUserEmail: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onToggleDarkMode: () -> Unit,
    onToggleOfflineMode: () -> Unit,
    onOpenGoLive: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAccountSheet: () -> Unit,
    onFiveSecondHoldAdmin: () -> Unit
) {
    var showSearchField by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // YouTube Logo + 5-Second Hold Secret Admin Trigger
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("app_logo_admin_hold")
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false)
                                var triggered = false
                                val job: Job = coroutineScope.launch {
                                    val steps = 50 // 50 * 100ms = 5000ms (5 seconds)
                                    for (i in 1..steps) {
                                        delay(100L)
                                        holdProgress = i / steps.toFloat()
                                    }
                                    triggered = true
                                    holdProgress = 0f
                                    onFiveSecondHoldAdmin()
                                }
                                waitForUpOrCancellation()
                                job.cancel()
                                if (!triggered) {
                                    holdProgress = 0f
                                }
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // YouTube Iconic Red Play Pill
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(26.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFF0000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "شعار التطبيق (اضغط مطولاً 5 ثوانٍ للإدارة)",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (holdProgress > 0f) {
                            CircularProgressIndicator(
                                progress = { holdProgress },
                                modifier = Modifier.size(42.dp),
                                color = Color(0xFFFF0000),
                                strokeWidth = 3.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "YouTube",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "AR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        if (holdProgress > 0f) {
                            val secRemaining = (5 - (holdProgress * 5).toInt()).coerceAtLeast(1)
                            Text(
                                text = "استمر بالضغط $secRemaining ث لفتح الإدارة...",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFF0000),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Right/End YouTube Action Icons: Go Live, Offline, Dark Mode, Notifications, Search, Email Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Go Live quick button
                    IconButton(
                        onClick = onOpenGoLive,
                        modifier = Modifier.testTag("top_go_live_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "بدء بث مباشر",
                            tint = Color(0xFFFF0000)
                        )
                    }

                    // Offline Mode toggle
                    IconButton(
                        onClick = onToggleOfflineMode,
                        modifier = Modifier.testTag("offline_mode_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isOfflineMode) Icons.Default.CloudOff else Icons.Default.CloudDone,
                            contentDescription = "وضع بدون إنترنت",
                            tint = if (isOfflineMode) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Dark Mode toggle
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier.testTag("dark_mode_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "تبديل الوضع الليلي",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Notifications Bell with Red Badge
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier.testTag("notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFFF0000),
                                        contentColor = Color.White
                                    ) {
                                        Text(unreadNotificationsCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "مركز الإشعارات",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Search toggle
                    IconButton(
                        onClick = { showSearchField = !showSearchField },
                        modifier = Modifier.testTag("search_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (showSearchField) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "بحث في يوتيوب",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // User Email Avatar (Tap = Account Sheet, Hold 5s = Admin Panel too)
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF0000))
                            .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                            .testTag("user_email_avatar_button")
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    awaitFirstDown(requireUnconsumed = false)
                                    var held5Sec = false
                                    val job = coroutineScope.launch {
                                        for (i in 1..50) {
                                            delay(100L)
                                            holdProgress = i / 50f
                                        }
                                        held5Sec = true
                                        holdProgress = 0f
                                        onFiveSecondHoldAdmin()
                                    }
                                    val up = waitForUpOrCancellation()
                                    job.cancel()
                                    if (!held5Sec) {
                                        holdProgress = 0f
                                        if (up != null) {
                                            onOpenAccountSheet()
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUserEmail.firstOrNull()?.uppercase() ?: "R",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            AnimatedVisibility(visible = showSearchField) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("البحث في يوتيوب...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "مسح البحث")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("search_input_field")
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        }
    }
}

@Composable
fun CategoryFilterRow(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    val categories = listOf(
        "الكل",
        "بث مباشر 🔴",
        "المحفوظات 🔖",
        "فيديوهاتي",
        "تقنية وبرمجة",
        "ألعاب",
        "وثائقي",
        "بودكاست وإنتاج"
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { cat ->
            val selected = selectedCategory == cat
            FilterChip(
                selected = selected,
                onClick = { onSelectCategory(cat) },
                shape = RoundedCornerShape(8.dp),
                label = {
                    Text(
                        text = cat,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.onBackground,
                    selectedLabelColor = MaterialTheme.colorScheme.background,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = null,
                modifier = Modifier.testTag("category_chip_$cat")
            )
        }
    }
}

@Composable
fun VideoFeedCard(
    video: VideoEntity,
    channel: ChannelEntity?,
    onVideoClick: () -> Unit,
    onSubscribeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    val isBanned = channel?.isBanned == true

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVideoClick() }
            .padding(bottom = 16.dp)
            .testTag("video_card_${video.id}")
    ) {
        // Authentic YouTube Full-Bleed 16:9 Thumbnail
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color(0xFF0A0A0A))
        ) {
            if (!video.customMediaUri.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(video.customMediaUri),
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = painterResource(id = getThumbnailResId(video.thumbnailKey)),
                    placeholder = painterResource(id = getThumbnailResId(video.thumbnailKey))
                )
            } else {
                Image(
                    painter = painterResource(id = getThumbnailResId(video.thumbnailKey)),
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Subtle bottom gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.65f)
                            ),
                            startY = 200f
                        )
                    )
            )

            // Top-Start Badges: Live Broadcast Type (Screen / Camera) or Monetized / Saved
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (video.isLive) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.78f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (video.liveSourceType == "SCREEN") Icons.Default.PhoneAndroid
                                else Icons.Default.Videocam,
                                contentDescription = null,
                                tint = Color(0xFF3EA6FF),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (video.liveSourceType == "SCREEN") "بث شاشة الجوال" else "بث الكاميرا",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (video.isMonetized) {
                    Surface(
                        color = Color(0xFF2BA640).copy(alpha = 0.90f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$${String.format("%.0f", video.adRevenueUsd)}",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (isBanned) {
                    Surface(
                        color = Color(0xFFCC0000),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "⛔ قناة محظورة",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Bottom-End YouTube Duration or Red LIVE Pill
            Surface(
                color = if (video.isLive) Color(0xFFFF0000) else Color.Black.copy(alpha = 0.88f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (video.isLive) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "مباشر • ${video.liveViewersCount}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = formatDuration(video.durationSeconds),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // YouTube Metadata Row: Channel Avatar + Title + Subtitle + Quick Save Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Channel Avatar
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
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 21.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${video.channelName} • ${formatCount(video.viewsCount)} مشاهدة • ${video.publishedAtText}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if ((channel?.warningCount ?: 0) > 0) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "عليها ${channel?.warningCount} تحذير إداري",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }

                // Quick Save / Bookmark Icon
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier.testTag("save_video_btn_${video.id}")
                ) {
                    Icon(
                        imageVector = if (video.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "حفظ الفيديو",
                        tint = if (video.isSaved) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // YouTube-style Pill Action Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Like Pill
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (video.isLiked) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { onLikeClick() }
                            .testTag("like_card_btn_${video.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "إعجاب",
                                tint = if (video.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = formatCount(video.likesCount.toLong()),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Save to Library Pill ("حفظ")
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (video.isSaved) Color(0xFFFF0000).copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { onSaveClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (video.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "حفظ الفيديو",
                                tint = if (video.isSaved) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (video.isSaved) "محفوظ ✓" else "حفظ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Offline Download Pill ("تنزيل")
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (video.isDownloaded) Color(0xFF3EA6FF).copy(alpha = 0.18f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { onDownloadClick() }
                            .testTag("download_card_btn_${video.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (video.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                                contentDescription = "تنزيل بدون إنترنت",
                                tint = if (video.isDownloaded) Color(0xFF3EA6FF) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (video.isDownloaded) "تم التنزيل" else "تنزيل",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // YouTube Subscribe Pill
                if (channel != null) {
                    val isSub = channel.isSubscribed
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isSub) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .clickable { onSubscribeClick() }
                            .testTag("sub_card_btn_${video.id}")
                    ) {
                        Text(
                            text = if (isSub) "مشترك ✓" else "اشتراك",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSub) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.background,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
