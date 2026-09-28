package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val channelId: Int,
    val channelName: String,
    val channelAvatarColor: Long,
    val category: String,
    val durationSeconds: Int,
    val viewsCount: Long,
    val likesCount: Int,
    val dislikesCount: Int = 0,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSaved: Boolean = false, // حفظ الفيديو في المكتبة / المشاهدة لاحقاً
    val publishedAtText: String,
    val thumbnailKey: String, // "tech", "gaming", "doc", "podcast"
    val customMediaUri: String? = null,
    val isLive: Boolean = false,
    val liveSourceType: String = "CAMERA", // "CAMERA" (بث الكاميرا) or "SCREEN" (بث شاشة الجوال)
    val useFrontCamera: Boolean = true,
    val liveViewersCount: Int = 0,
    val isDownloaded: Boolean = false,
    val downloadQuality: String = "1080p Full HD",
    val fileSizeMb: Int = 145,
    val isMonetized: Boolean = true,
    val adFormat: String = "إعلانات فيديو قابلة للتخطي + بانر",
    val cpmUsd: Double = 6.50,
    val adRevenueUsd: Double = 0.0,
    val adImpressions: Int = 0,
    val avgRetentionPercent: Int = 76,
    val ctrPercent: Double = 8.9,
    val watchTimeHours: Double = 120.0,
    val isMyUpload: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val handle: String,
    val email: String = "creator@gmail.com",
    val subscribersCount: Int,
    val isSubscribed: Boolean,
    val notificationsEnabled: Boolean = true,
    val avatarColor: Long,
    val bio: String,
    val isMyChannel: Boolean = false,
    val isBanned: Boolean = false,
    val warningCount: Int = 0,
    val lastWarningMessage: String? = null
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: Int,
    val authorName: String,
    val authorEmail: String = "",
    val authorAvatarColor: Long,
    val content: String,
    val timestampText: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isCreatorHearted: Boolean = false,
    val isPinned: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: String, // "UPLOAD", "LIVE", "MONETIZATION", "WARNING"
    val relatedVideoId: Int? = null,
    val isRead: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "live_chat")
data class LiveChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: Int,
    val senderName: String,
    val senderColor: Long,
    val message: String,
    val superChatAmountUsd: Double = 0.0,
    val timestampMillis: Long = System.currentTimeMillis()
)
