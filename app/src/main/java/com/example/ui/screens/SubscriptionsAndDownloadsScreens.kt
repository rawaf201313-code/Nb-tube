package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.ChannelEntity
import com.example.data.VideoEntity
import com.example.ui.components.VideoFeedCard
import com.example.ui.components.formatCount

@Composable
fun SubscriptionsScreen(
    channels: List<ChannelEntity>,
    videos: List<VideoEntity>,
    onOpenVideo: (VideoEntity) -> Unit,
    onToggleSubscribe: (ChannelEntity) -> Unit,
    onToggleBell: (ChannelEntity) -> Unit,
    onToggleSave: (VideoEntity) -> Unit,
    onToggleDownload: (VideoEntity) -> Unit,
    onToggleLike: (VideoEntity) -> Unit
) {
    var selectedChannelId by remember { mutableStateOf<Int?>(null) }
    val subscribedChannels = channels.filter { it.isSubscribed }
    val subIds = subscribedChannels.map { it.id }.toSet()

    val filteredVideos = videos.filter { video ->
        if (selectedChannelId != null) {
            video.channelId == selectedChannelId
        } else {
            video.channelId in subIds
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("subscriptions_screen"),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        // Subscribed Channels Horizontal Carousel
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "القنوات المشترك بها (${subscribedChannels.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (selectedChannelId != null) {
                        Text(
                            text = "عرض الكل",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { selectedChannelId = null }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subscribedChannels, key = { it.id }) { ch ->
                        val isSelected = selectedChannelId == ch.id
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(78.dp)
                                .clickable {
                                    selectedChannelId = if (isSelected) null else ch.id
                                }
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color(ch.avatarColor))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.4f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ch.name.take(1),
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (ch.notificationsEnabled) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { onToggleBell(ch) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (ch.notificationsEnabled) Icons.Default.NotificationsActive
                                        else Icons.Default.NotificationsOff,
                                        contentDescription = "جرس الإشعارات",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = ch.name,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Directory of All Channels to Manage Subscriptions & Notification Bells
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔔 إدارة القنوات وجرس التنبيهات",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    channels.forEach { ch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(ch.avatarColor)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ch.name.take(1),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = ch.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${formatCount(ch.subscribersCount.toLong())} مشترك • ${ch.email}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (ch.isSubscribed) {
                                    IconButton(onClick = { onToggleBell(ch) }) {
                                        Icon(
                                            imageVector = if (ch.notificationsEnabled) Icons.Default.NotificationsActive
                                            else Icons.Default.NotificationsOff,
                                            contentDescription = "تبديل إشعارات القناة",
                                            tint = if (ch.notificationsEnabled) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                OutlinedButton(
                                    onClick = { onToggleSubscribe(ch) },
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (ch.isSubscribed) "مشترك ✓" else "اشتراك +",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "أحدث فيديوهات القنوات المشترك بها (${filteredVideos.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        items(filteredVideos, key = { it.id }) { video ->
            val ch = channels.find { it.id == video.channelId }
            VideoFeedCard(
                video = video,
                channel = ch,
                onVideoClick = { onOpenVideo(video) },
                onSubscribeClick = { if (ch != null) onToggleSubscribe(ch) },
                onSaveClick = { onToggleSave(video) },
                onDownloadClick = { onToggleDownload(video) },
                onLikeClick = { onToggleLike(video) }
            )
        }
    }
}

@Composable
fun LiveHubScreen(
    videos: List<VideoEntity>,
    channels: List<ChannelEntity>,
    onOpenVideo: (VideoEntity) -> Unit,
    onStartNewLive: () -> Unit,
    onToggleSubscribe: (ChannelEntity) -> Unit,
    onToggleSave: (VideoEntity) -> Unit,
    onToggleDownload: (VideoEntity) -> Unit,
    onToggleLike: (VideoEntity) -> Unit
) {
    val liveVideos = videos.filter { it.isLive }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("live_hub_screen"),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        item {
            // Go Live Hero Card with Camera vs Phone Screen info
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFFF0000).copy(alpha = 0.45f),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = Color(0xFFFF0000),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مركز البث المباشر (الكاميرا أو شاشة الجوال)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اختر طريقة البث المباشر المفضلة لديك: البث عبر الكاميرا الأمامية/الخلفية 🎥 أو بث شاشة الجوال مباشرة 📱 للألعاب والشروحات مع الدردشة الحية.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = Color(0xFFFF0000),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "بث الكاميرا 🎥",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = Color(0xFF3EA6FF),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "بث شاشة الجوال 📱",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onStartNewLive,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF0000),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_live_stream_cta")
                    ) {
                        Icon(Icons.Default.Sensors, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("بدء بث مباشر جديد الآن 🔴", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "🔴 البثوث المباشرة الجارية الآن (${liveVideos.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        items(liveVideos, key = { it.id }) { video ->
            val ch = channels.find { it.id == video.channelId }
            VideoFeedCard(
                video = video,
                channel = ch,
                onVideoClick = { onOpenVideo(video) },
                onSubscribeClick = { if (ch != null) onToggleSubscribe(ch) },
                onSaveClick = { onToggleSave(video) },
                onDownloadClick = { onToggleDownload(video) },
                onLikeClick = { onToggleLike(video) }
            )
        }
    }
}

@Composable
fun YouLibraryScreen(
    currentUserName: String,
    currentUserEmail: String,
    currentUserHandle: String,
    savedVideos: List<VideoEntity>,
    downloadedVideos: List<VideoEntity>,
    allVideos: List<VideoEntity>,
    isOfflineMode: Boolean,
    onToggleOfflineMode: () -> Unit,
    onOpenAccountSheet: () -> Unit,
    onOpenUpload: () -> Unit,
    onOpenGoLive: () -> Unit,
    onOpenVideo: (VideoEntity) -> Unit,
    onToggleSave: (VideoEntity) -> Unit,
    onToggleDownload: (VideoEntity) -> Unit
) {
    // Sub-tabs inside YouTube "You / Library" screen:
    // 0 = المحفوظات (Saved Videos), 1 = فيديوهاتي (My Uploaded Videos), 2 = التحميلات بدون إنترنت (Offline Downloads)
    var selectedSection by remember { mutableStateOf(0) }
    val myUploadedVideos = allVideos.filter { it.isMyUpload }
    val totalStorageMb = downloadedVideos.sumOf { it.fileSizeMb }
    val notDownloadedVideos = allVideos.filter { !it.isDownloaded && !it.isLive }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("you_library_screen"),
        contentPadding = PaddingValues(14.dp, 12.dp, 14.dp, 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // YouTube "You" Profile & Email Account Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF0000)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUserEmail.firstOrNull()?.uppercase() ?: "R",
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentUserName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = Color(0xFF3EA6FF),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentUserEmail,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF3EA6FF),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "$currentUserHandle • حساب يوتيوب نشط ✓",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onOpenAccountSheet,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("switch_email_account_button")
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("الحساب", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Upload & Go Live Buttons inside Library
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenUpload,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF0000),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("library_upload_video_btn")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("رفع فيديو جديد", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onOpenGoLive,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("library_go_live_btn")
                        ) {
                            Icon(Icons.Default.Sensors, contentDescription = null, tint = Color(0xFFFF0000), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("بث مباشر 🔴", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section Filter Tabs: Saved Videos | My Uploaded Videos | Offline Downloads
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    label = {
                        Text(
                            "🔖 المحفوظات (${savedVideos.size})",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.onBackground,
                        selectedLabelColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier.testTag("section_saved_videos")
                )
                FilterChip(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    label = {
                        Text(
                            "🎬 فيديوهاتي (${myUploadedVideos.size})",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.onBackground,
                        selectedLabelColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier.testTag("section_my_videos")
                )
                FilterChip(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    label = {
                        Text(
                            "📥 التحميلات (${downloadedVideos.size})",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.onBackground,
                        selectedLabelColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier.testTag("section_downloads")
                )
            }
        }

        if (selectedSection == 0) {
            // SAVED VIDEOS SECTION
            item {
                Text(
                    text = "🔖 قائمة الفيديوهات المحفوظة (${savedVideos.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            if (savedVideos.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.size(42.dp),
                                tint = Color(0xFF3EA6FF)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "لا توجد فيديوهات محفوظة بعد",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "اضغط على زر «حفظ» أسفل أي فيديو لإضافته إلى قائمة الفيديوهات المحفوظة للرجوع إليه في أي وقت.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(savedVideos, key = { "saved_${it.id}" }) { video ->
                    LibraryVideoRowCard(
                        video = video,
                        badgeText = "محفوظ في المكتبة 🔖",
                        badgeColor = Color(0xFF3EA6FF),
                        onOpen = { onOpenVideo(video) },
                        onRemove = { onToggleSave(video) }
                    )
                }
            }
        } else if (selectedSection == 1) {
            // MY UPLOADED VIDEOS SECTION
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎬 الفيديوهات التي رفعتها (${myUploadedVideos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "+ رفع فيديو",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFFF0000),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onOpenUpload() }
                    )
                }
            }

            items(myUploadedVideos, key = { "my_${it.id}" }) { video ->
                LibraryVideoRowCard(
                    video = video,
                    badgeText = if (video.isLive) "بث مباشر (${if (video.liveSourceType == "SCREEN") "شاشة الجوال" else "الكاميرا"})"
                    else "${formatCount(video.viewsCount)} مشاهدة • ${video.publishedAtText}",
                    badgeColor = if (video.isLive) Color(0xFFFF0000) else Color(0xFF2BA640),
                    onOpen = { onOpenVideo(video) },
                    onRemove = null
                )
            }
        } else {
            // OFFLINE DOWNLOADS SECTION
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isOfflineMode) Icons.Default.CloudOff else Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "وضع المشاهدة بدون إنترنت",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "${downloadedVideos.size} فيديوهات محملة • $totalStorageMb MB مستخدمة",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = isOfflineMode,
                                onCheckedChange = { onToggleOfflineMode() },
                                modifier = Modifier.testTag("downloads_offline_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { (totalStorageMb / 2048f).coerceIn(0.05f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF3EA6FF)
                        )
                    }
                }
            }

            items(downloadedVideos, key = { "dl_${it.id}" }) { video ->
                LibraryVideoRowCard(
                    video = video,
                    badgeText = "${video.downloadQuality} • ${video.fileSizeMb} MB بدون إنترنت",
                    badgeColor = Color(0xFF3EA6FF),
                    onOpen = { onOpenVideo(video) },
                    onRemove = { onToggleDownload(video) }
                )
            }

            if (notDownloadedVideos.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "فيديوهات متاحة للتنزيل السريع",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(notDownloadedVideos, key = { "avail_${it.id}" }) { video ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = video.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${video.channelName} • 1080p Full HD",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onToggleDownload(video) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تنزيل", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryVideoRowCard(
    video: VideoEntity,
    badgeText: String,
    badgeColor: Color,
    onOpen: () -> Unit,
    onRemove: (() -> Unit)?
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("library_video_item_${video.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = badgeColor.copy(alpha = 0.16f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "تشغيل",
                        tint = badgeColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${video.channelName} • $badgeText",
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor,
                    fontWeight = FontWeight.Bold
                )
            }
            if (onRemove != null) {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "إزالة",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
