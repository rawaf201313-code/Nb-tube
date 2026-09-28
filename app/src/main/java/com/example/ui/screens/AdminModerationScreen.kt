package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.CommentEntity
import com.example.data.VideoEntity
import com.example.ui.components.formatCount

@Composable
fun AdminModerationScreen(
    channels: List<ChannelEntity>,
    videos: List<VideoEntity>,
    comments: List<CommentEntity>,
    onCloseAdmin: () -> Unit,
    onToggleBanChannel: (ChannelEntity) -> Unit,
    onSendWarningToChannel: (ChannelEntity, String) -> Unit,
    onDeleteChannel: (ChannelEntity) -> Unit,
    onDeleteVideo: (VideoEntity) -> Unit,
    onDeleteComment: (CommentEntity) -> Unit
) {
    BackHandler { onCloseAdmin() }

    // 0 = إدارة الحسابات والقنوات (باند / تحذير / حذف), 1 = إدارة وحذف الفيديوهات, 2 = إدارة وحذف التعليقات
    var selectedAdminTab by remember { mutableIntStateOf(0) }
    var warningTargetChannel by remember { mutableStateOf<ChannelEntity?>(null) }
    var warningMessageInput by remember {
        mutableStateOf("تحذير إداري رسمي: يرجى الالتزام بسياسات المنصة وعدم نشر محتوى مخالف لتجنب حظر الحساب نهائياً.")
    }

    val bannedCount = channels.count { it.isBanned }
    val warnedCount = channels.sumOf { it.warningCount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_moderation_screen")
    ) {
        // Admin Header Bar
        Surface(
            color = Color(0xFF1F1212),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onCloseAdmin,
                        modifier = Modifier.testTag("close_admin_panel_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "إغلاق لوحة الإدارة",
                            tint = Color.White
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFFFF0000),
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "لوحة الإدارة والتحكم العليا 🛡️",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "صلاحيات المدير (الرقم السري 1234): باند • حذف • إرسال تحذير",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp, 12.dp, 14.dp, 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Admin Summary Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminStatCard(
                        title = "القنوات المسجلة",
                        value = "${channels.size}",
                        color = Color(0xFF3EA6FF),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "محظور (باند)",
                        value = "$bannedCount",
                        color = Color(0xFFFF0000),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "إنذارات مرسلة",
                        value = "$warnedCount",
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "الفيديوهات",
                        value = "${videos.size}",
                        color = Color(0xFF2BA640),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Admin Sub-Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedAdminTab == 0,
                        onClick = { selectedAdminTab = 0 },
                        label = { Text("👤 القنوات والحسابات (${channels.size})", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF0000),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("admin_tab_channels")
                    )
                    FilterChip(
                        selected = selectedAdminTab == 1,
                        onClick = { selectedAdminTab = 1 },
                        label = { Text("🎬 الفيديوهات (${videos.size})", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF0000),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("admin_tab_videos")
                    )
                    FilterChip(
                        selected = selectedAdminTab == 2,
                        onClick = { selectedAdminTab = 2 },
                        label = { Text("💬 التعليقات (${comments.size})", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF0000),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("admin_tab_comments")
                    )
                }
            }

            if (selectedAdminTab == 0) {
                item {
                    Text(
                        text = "إدارة حسابات المستخدمين والقنوات (باند / رسالة تحذير / حذف)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                items(channels, key = { "admin_ch_${it.id}" }) { ch ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (ch.isBanned) 1.5.dp else 0.5.dp,
                                color = if (ch.isBanned) Color(0xFFFF0000) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("admin_channel_card_${ch.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(if (ch.isBanned) Color.Gray else Color(ch.avatarColor)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ch.name.take(1),
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = ch.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            if (ch.isBanned) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = Color(0xFFFF0000),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "محظور (باند)",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = null,
                                                tint = Color(0xFF3EA6FF),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = ch.email,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF3EA6FF),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = "${formatCount(ch.subscribersCount.toLong())} مشترك • إنذارات سابقة: ${ch.warningCount}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            if (!ch.lastWarningMessage.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFF59E0B).copy(alpha = 0.16f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "آخر تحذير: ${ch.lastWarningMessage}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFF59E0B),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Three Admin Action Buttons: Ban/Unban | Send Warning | Delete Channel
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 1. Ban / Unban Button
                                Button(
                                    onClick = { onToggleBanChannel(ch) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (ch.isBanned) Color(0xFF2BA640) else Color(0xFFFF0000),
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_ban_btn_${ch.id}")
                                ) {
                                    Icon(
                                        imageVector = if (ch.isBanned) Icons.Default.CheckCircle else Icons.Default.Block,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (ch.isBanned) "فك الباند" else "باند (حظر)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // 2. Warning Message Button
                                OutlinedButton(
                                    onClick = { warningTargetChannel = ch },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_warn_btn_${ch.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "رسالة تحذير",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // 3. Delete Channel Button
                                if (!ch.isMyChannel) {
                                    OutlinedButton(
                                        onClick = { onDeleteChannel(ch) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("admin_delete_channel_btn_${ch.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteForever,
                                            contentDescription = "حذف القناة",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "حذف",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (selectedAdminTab == 1) {
                item {
                    Text(
                        text = "إدارة وحذف الفيديوهات والبثوث المباشرة (${videos.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                items(videos, key = { "admin_vid_${it.id}" }) { video ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "القناة: ${video.channelName} • ${formatCount(video.viewsCount)} مشاهدة",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onDeleteVideo(video) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF0000),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("admin_delete_video_${video.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("حذف الفيديو", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "مراقبة وحذف التعليقات المخالفة (${comments.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                items(comments, key = { "admin_comm_${it.id}" }) { comm ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                    text = "${comm.authorName} (${comm.authorEmail})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF3EA6FF),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = comm.content,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(
                                onClick = { onDeleteComment(comm) },
                                modifier = Modifier.testTag("admin_delete_comment_${comm.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = "حذف التعليق",
                                    tint = Color(0xFFFF0000)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Send Official Warning Message Modal
    val targetCh = warningTargetChannel
    if (targetCh != null) {
        AlertDialog(
            onDismissRequest = { warningTargetChannel = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إرسال رسالة تحذير إدارية ⚠️", fontWeight = FontWeight.ExtraBold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "إلى القناة: ${targetCh.name} (${targetCh.email})",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = warningMessageInput,
                        onValueChange = { warningMessageInput = it },
                        label = { Text("نص رسالة التحذير") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_warning_message_input")
                    )

                    Text(
                        text = "رسائل تحذير جاهزة:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "تحذير رسمي: يرجى الالتزام بحقوق الطبع والنشر وسياسات يوتيوب لتجنب حظر القناة.",
                            "إنذار نهائي: تم رصد مخالفة في البث المباشر، أي تكرار سيؤدي إلى باند دائم للحساب.",
                            "تنبيه إداري: يرجى مراجعة عنوان ووصف الفيديو ليتوافق مع إرشادات المجتمع."
                        ).forEach { preset ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { warningMessageInput = preset }
                            ) {
                                Text(
                                    text = preset,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSendWarningToChannel(targetCh, warningMessageInput)
                        warningTargetChannel = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("confirm_send_warning_button")
                ) {
                    Text("إرسال التحذير الآن ⚠️", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { warningTargetChannel = null }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
