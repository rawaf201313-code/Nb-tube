package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChannelEntity
import com.example.data.CommentEntity
import com.example.data.LiveChatMessageEntity
import com.example.data.NotificationEntity
import com.example.data.VideoEntity
import com.example.data.VidioDatabase
import com.example.data.VidioRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    LIVE,
    SUBSCRIPTIONS,
    LIBRARY_YOU,
    STUDIO
}

class VidioViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: VidioRepository

    val allVideos: StateFlow<List<VideoEntity>>
    val downloadedVideos: StateFlow<List<VideoEntity>>
    val savedVideos: StateFlow<List<VideoEntity>>
    val allChannels: StateFlow<List<ChannelEntity>>
    val allNotifications: StateFlow<List<NotificationEntity>>
    val allComments: StateFlow<List<CommentEntity>>

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    private val _activeVideoId = MutableStateFlow<Int?>(null)
    val activeVideoId: StateFlow<Int?> = _activeVideoId.asStateFlow()

    // Current signed-in Email Account
    private val _currentUserEmail = MutableStateFlow("rawaf.code@gmail.com")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    private val _currentUserName = MutableStateFlow("قناتي الرسمية • Rawaf")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    private val _currentUserHandle = MutableStateFlow("@rawaf.code")
    val currentUserHandle: StateFlow<String> = _currentUserHandle.asStateFlow()

    // Dialogs & Sheets
    private val _showCreateSheet = MutableStateFlow(false)
    val showCreateSheet: StateFlow<Boolean> = _showCreateSheet.asStateFlow()

    private val _showUploadDialog = MutableStateFlow(false)
    val showUploadDialog: StateFlow<Boolean> = _showUploadDialog.asStateFlow()

    private val _showGoLiveDialog = MutableStateFlow(false)
    val showGoLiveDialog: StateFlow<Boolean> = _showGoLiveDialog.asStateFlow()

    private val _showNotificationsSheet = MutableStateFlow(false)
    val showNotificationsSheet: StateFlow<Boolean> = _showNotificationsSheet.asStateFlow()

    private val _showAccountSheet = MutableStateFlow(false)
    val showAccountSheet: StateFlow<Boolean> = _showAccountSheet.asStateFlow()

    // Secret 5-second hold Admin Panel (PIN: 1234)
    private val _showAdminPinDialog = MutableStateFlow(false)
    val showAdminPinDialog: StateFlow<Boolean> = _showAdminPinDialog.asStateFlow()

    private val _showAdminPanel = MutableStateFlow(false)
    val showAdminPanel: StateFlow<Boolean> = _showAdminPanel.asStateFlow()

    // Channel-wide monetization toggles
    private val _preRollAdsEnabled = MutableStateFlow(true)
    val preRollAdsEnabled: StateFlow<Boolean> = _preRollAdsEnabled.asStateFlow()

    private val _midRollAdsEnabled = MutableStateFlow(true)
    val midRollAdsEnabled: StateFlow<Boolean> = _midRollAdsEnabled.asStateFlow()

    private val _sponsorCardsEnabled = MutableStateFlow(true)
    val sponsorCardsEnabled: StateFlow<Boolean> = _sponsorCardsEnabled.asStateFlow()

    private val _superChatEnabled = MutableStateFlow(true)
    val superChatEnabled: StateFlow<Boolean> = _superChatEnabled.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    init {
        val dao = VidioDatabase.getDatabase(application).vidioDao()
        repository = VidioRepository(dao)

        allVideos = repository.allVideos.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        downloadedVideos = repository.downloadedVideos.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        savedVideos = repository.savedVideos.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        allChannels = repository.allChannels.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        allNotifications = repository.allNotifications.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        allComments = repository.allComments.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.ensureSeedData()
        }
    }

    fun getCommentsForVideo(videoId: Int): Flow<List<CommentEntity>> =
        repository.getCommentsForVideo(videoId)

    fun getLiveChatForVideo(videoId: Int): Flow<List<LiveChatMessageEntity>> =
        repository.getLiveChatForVideo(videoId)

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleOfflineMode() {
        val next = !_isOfflineMode.value
        _isOfflineMode.value = next
        if (next) {
            showToastMessage("تم تفعيل وضع المشاهدة بدون إنترنت (تُعرض التحميلات المحفوظة)")
        } else {
            showToastMessage("تم الاتصال بالشبكة وعرض جميع الفيديوهات والبثوث")
        }
    }

    fun updateEmailAccount(email: String, name: String, handle: String) {
        if (email.isBlank()) return
        _currentUserEmail.value = email.trim()
        _currentUserName.value = name.ifBlank { email.substringBefore("@") }
        _currentUserHandle.value = handle.ifBlank { "@${email.substringBefore("@")}" }
        viewModelScope.launch {
            val myCh = allChannels.value.firstOrNull { it.isMyChannel }
            if (myCh != null) {
                repository.updateChannel(
                    myCh.copy(
                        name = _currentUserName.value,
                        handle = _currentUserHandle.value,
                        email = _currentUserEmail.value
                    )
                )
            }
        }
        _showAccountSheet.value = false
        showToastMessage("تم تسجيل الدخول بالحساب: ${_currentUserEmail.value} ✓")
    }

    fun openVideo(video: VideoEntity) {
        val channel = allChannels.value.find { it.id == video.channelId }
        if (channel?.isBanned == true) {
            showToastMessage("⛔ هذه القناة محظورة من قِبل الإدارة ولا يمكن تشغيل محتواها")
            return
        }
        _activeVideoId.value = video.id
        viewModelScope.launch {
            val addedAdRev = if (video.isMonetized) (video.cpmUsd / 1000.0) * 12.0 else 0.0
            val updated = video.copy(
                viewsCount = video.viewsCount + 1,
                adImpressions = if (video.isMonetized) video.adImpressions + 1 else video.adImpressions,
                adRevenueUsd = video.adRevenueUsd + addedAdRev,
                watchTimeHours = video.watchTimeHours + 0.25
            )
            repository.updateVideo(updated)
        }
    }

    fun closeVideoPlayer() {
        _activeVideoId.value = null
    }

    fun setShowCreateSheet(show: Boolean) {
        _showCreateSheet.value = show
    }

    fun setShowUploadDialog(show: Boolean) {
        _showUploadDialog.value = show
    }

    fun setShowGoLiveDialog(show: Boolean) {
        _showGoLiveDialog.value = show
    }

    fun setShowNotificationsSheet(show: Boolean) {
        _showNotificationsSheet.value = show
    }

    fun setShowAccountSheet(show: Boolean) {
        _showAccountSheet.value = show
    }

    // Secret 5-Second Hold on App Icon -> Admin PIN (1234)
    fun triggerAdminPinPrompt() {
        _showAdminPinDialog.value = true
    }

    fun dismissAdminPinPrompt() {
        _showAdminPinDialog.value = false
    }

    fun verifyAdminPin(pin: String): Boolean {
        return if (pin.trim() == "1234") {
            _showAdminPinDialog.value = false
            _showAdminPanel.value = true
            showToastMessage("🔓 تم فتح لوحة الإدارة والتحكم بنجاح")
            true
        } else {
            showToastMessage("❌ الرقم السري غير صحيح!")
            false
        }
    }

    fun closeAdminPanel() {
        _showAdminPanel.value = false
    }

    // Admin Moderation Actions: Ban, Delete, Warning
    fun adminToggleBanChannel(channel: ChannelEntity) {
        viewModelScope.launch {
            val nowBanned = !channel.isBanned
            repository.updateChannel(channel.copy(isBanned = nowBanned))
            repository.insertNotification(
                NotificationEntity(
                    title = if (nowBanned) "⛔ قرار إداري: حظر قناة ${channel.name}"
                    else "✅ قرار إداري: فك الحظر عن قناة ${channel.name}",
                    message = if (nowBanned) "تم حظر الحساب (${channel.email}) ومحتواه لمخالفة سياسات المنصة."
                    else "تمت استعادة الحساب (${channel.email}) وإلغاء الحظر الإداري.",
                    timeAgo = "الآن",
                    type = "WARNING",
                    isRead = false
                )
            )
            showToastMessage(
                if (nowBanned) "⛔ تم حظر (باند) قناة «${channel.name}» (${channel.email})"
                else "✅ تم فك الحظر عن قناة «${channel.name}»"
            )
        }
    }

    fun adminSendWarningToChannel(channel: ChannelEntity, warningMessage: String) {
        val cleanMsg = warningMessage.ifBlank { "تحذير رسمي من الإدارة: يرجى الالتزام بمعايير المجتمع لتجنب إغلاق القناة." }
        viewModelScope.launch {
            val newWarnings = channel.warningCount + 1
            repository.updateChannel(
                channel.copy(
                    warningCount = newWarnings,
                    lastWarningMessage = cleanMsg
                )
            )
            val notifTitle = "⚠️ إنذار إداري (#$newWarnings) موجه إلى: ${channel.name}"
            repository.insertNotification(
                NotificationEntity(
                    title = notifTitle,
                    message = "إلى (${channel.email}): $cleanMsg",
                    timeAgo = "الآن",
                    type = "WARNING",
                    isRead = false
                )
            )
            NotificationHelper.sendSystemNotification(getApplication(), notifTitle, cleanMsg)
            showToastMessage("⚠️ تم إرسال رسالة التحذير إلى «${channel.name}» (${channel.email})")
        }
    }

    fun adminDeleteVideo(video: VideoEntity) {
        viewModelScope.launch {
            if (_activeVideoId.value == video.id) {
                _activeVideoId.value = null
            }
            repository.deleteVideo(video.id)
            showToastMessage("🗑️ تم حذف الفيديو «${video.title.take(25)}...» نهائياً")
        }
    }

    fun adminDeleteChannelAndVideos(channel: ChannelEntity) {
        viewModelScope.launch {
            repository.deleteVideosByChannel(channel.id)
            repository.deleteChannel(channel.id)
            showToastMessage("🗑️ تم حذف قناة «${channel.name}» وجميع فيديوهاتها")
        }
    }

    fun adminDeleteComment(comment: CommentEntity) {
        viewModelScope.launch {
            repository.deleteComment(comment.id)
            showToastMessage("🗑️ تم حذف التعليق المخالف")
        }
    }

    fun clearBannerMessage() {
        _statusBannerMessage.value = null
    }

    fun showToastMessage(msg: String) {
        _statusBannerMessage.value = msg
    }

    fun toggleLikeVideo(video: VideoEntity) {
        viewModelScope.launch {
            val nowLiked = !video.isLiked
            val newLikes = if (nowLiked) video.likesCount + 1 else (video.likesCount - 1).coerceAtLeast(0)
            val newDislikes = if (nowLiked && video.isDisliked) (video.dislikesCount - 1).coerceAtLeast(0) else video.dislikesCount
            repository.updateVideo(
                video.copy(
                    isLiked = nowLiked,
                    isDisliked = if (nowLiked) false else video.isDisliked,
                    likesCount = newLikes,
                    dislikesCount = newDislikes
                )
            )
        }
    }

    fun toggleDislikeVideo(video: VideoEntity) {
        viewModelScope.launch {
            val nowDisliked = !video.isDisliked
            val newDislikes = if (nowDisliked) video.dislikesCount + 1 else (video.dislikesCount - 1).coerceAtLeast(0)
            val newLikes = if (nowDisliked && video.isLiked) (video.likesCount - 1).coerceAtLeast(0) else video.likesCount
            repository.updateVideo(
                video.copy(
                    isDisliked = nowDisliked,
                    isLiked = if (nowDisliked) false else video.isLiked,
                    dislikesCount = newDislikes,
                    likesCount = newLikes
                )
            )
        }
    }

    fun toggleSaveVideo(video: VideoEntity) {
        viewModelScope.launch {
            val nowSaved = !video.isSaved
            repository.updateVideo(video.copy(isSaved = nowSaved))
            showToastMessage(
                if (nowSaved) "🔖 تم حفظ الفيديو في مكتبتك (الفيديوهات المحفوظة)"
                else "تمت إزالة الفيديو من المحفوظات"
            )
        }
    }

    fun toggleDownloadVideo(video: VideoEntity, quality: String = "1080p Full HD") {
        viewModelScope.launch {
            val nowDownloaded = !video.isDownloaded
            val sizeMb = when {
                quality.contains("1080") -> 225
                quality.contains("720") -> 130
                else -> 65
            }
            repository.updateVideo(
                video.copy(
                    isDownloaded = nowDownloaded,
                    downloadQuality = quality,
                    fileSizeMb = sizeMb
                )
            )
            if (nowDownloaded) {
                showToastMessage("📥 تم تنزيل «${video.title.take(28)}...» للمشاهدة بدون إنترنت ($quality)")
            } else {
                showToastMessage("تمت إزالة الفيديو من التحميلات المحفوظة")
            }
        }
    }

    fun toggleSubscribe(channel: ChannelEntity) {
        viewModelScope.launch {
            val nowSub = !channel.isSubscribed
            val delta = if (nowSub) 1 else -1
            repository.updateChannel(
                channel.copy(
                    isSubscribed = nowSub,
                    subscribersCount = (channel.subscribersCount + delta).coerceAtLeast(0),
                    notificationsEnabled = if (nowSub) true else false
                )
            )
            if (nowSub) {
                showToastMessage("تم الاشتراك في «${channel.name}» وتفعيل جرس الإشعارات 🔔")
            } else {
                showToastMessage("تم إلغاء الاشتراك من «${channel.name}»")
            }
        }
    }

    fun toggleChannelNotificationBell(channel: ChannelEntity) {
        viewModelScope.launch {
            val nextBell = !channel.notificationsEnabled
            repository.updateChannel(channel.copy(notificationsEnabled = nextBell))
            showToastMessage(
                if (nextBell) "تم تفعيل جميع إشعارات قناة ${channel.name} 🔔"
                else "تم كتم إشعارات قناة ${channel.name} 🔕"
            )
        }
    }

    fun addComment(videoId: Int, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.insertComment(
                CommentEntity(
                    videoId = videoId,
                    authorName = _currentUserName.value,
                    authorEmail = _currentUserEmail.value,
                    authorAvatarColor = 0xFFFF0000,
                    content = text.trim(),
                    timestampText = "الآن",
                    likesCount = 1,
                    isLiked = true,
                    isCreatorHearted = false
                )
            )
        }
    }

    fun toggleCommentLike(comment: CommentEntity) {
        viewModelScope.launch {
            val next = !comment.isLiked
            val count = if (next) comment.likesCount + 1 else (comment.likesCount - 1).coerceAtLeast(0)
            repository.updateComment(comment.copy(isLiked = next, likesCount = count))
        }
    }

    fun toggleCommentHeart(comment: CommentEntity) {
        viewModelScope.launch {
            repository.updateComment(comment.copy(isCreatorHearted = !comment.isCreatorHearted))
        }
    }

    fun sendLiveChatMessage(video: VideoEntity, message: String, superChatUsd: Double = 0.0) {
        if (message.isBlank() && superChatUsd <= 0.0) return
        viewModelScope.launch {
            repository.insertLiveChatMessage(
                LiveChatMessageEntity(
                    videoId = video.id,
                    senderName = "${_currentUserName.value} (${_currentUserEmail.value.substringBefore("@")})",
                    senderColor = if (superChatUsd > 0) 0xFF10B981 else 0xFFFF0000,
                    message = if (message.isBlank()) "دعم مباشر للبث عبر Super Chat 💎" else message.trim(),
                    superChatAmountUsd = superChatUsd
                )
            )
            if (superChatUsd > 0.0) {
                repository.updateVideo(
                    video.copy(
                        adRevenueUsd = video.adRevenueUsd + superChatUsd,
                        likesCount = video.likesCount + 5
                    )
                )
                showToastMessage("تم إرسال Super Chat بقيمة $${superChatUsd.toInt()} وإضافته لأرباح البث المباشر!")
            }
        }
    }

    fun uploadNewVideo(
        title: String,
        description: String,
        category: String,
        durationMinutes: Int,
        thumbnailKey: String,
        customMediaUri: String?,
        isMonetized: Boolean,
        adFormat: String,
        cpmUsd: Double,
        sendNotificationToSubscribers: Boolean
    ) {
        viewModelScope.launch {
            val newVideo = VideoEntity(
                title = title.ifBlank { "فيديو جديد من قناتي (${_currentUserName.value})" },
                description = description.ifBlank { "تم رفع هذا الفيديو عبر حساب ${_currentUserEmail.value} مع تفعيل التفاعل والإعلانات." },
                channelId = 1,
                channelName = _currentUserName.value,
                channelAvatarColor = 0xFFFF0000,
                category = category,
                durationSeconds = (durationMinutes.coerceAtLeast(1)) * 60 + 25,
                viewsCount = 1,
                likesCount = 1,
                isSaved = true,
                publishedAtText = "تم الرفع الآن",
                thumbnailKey = thumbnailKey,
                customMediaUri = customMediaUri,
                isLive = false,
                isDownloaded = false,
                isMonetized = isMonetized,
                adFormat = adFormat,
                cpmUsd = cpmUsd,
                adRevenueUsd = if (isMonetized) 1.25 else 0.0,
                adImpressions = if (isMonetized) 180 else 0,
                avgRetentionPercent = 85,
                ctrPercent = 11.2,
                watchTimeHours = 4.5,
                isMyUpload = true
            )
            val insertedId = repository.insertVideo(newVideo)
            if (sendNotificationToSubscribers) {
                val notifTitle = "🎬 فيديو جديد: ${newVideo.channelName}"
                val notifBody = "تم رفع فيديو جديد: «${newVideo.title}» — شاهد الآن!"
                repository.insertNotification(
                    NotificationEntity(
                        title = notifTitle,
                        message = notifBody,
                        timeAgo = "الآن",
                        type = "UPLOAD",
                        relatedVideoId = insertedId,
                        isRead = false
                    )
                )
                NotificationHelper.sendSystemNotification(
                    getApplication(),
                    notifTitle,
                    notifBody
                )
            }
            _showUploadDialog.value = false
            showToastMessage("تم نشر الفيديو في قناتك وحفظه في مكتبتك 🔔")
        }
    }

    fun startNewLiveStream(
        title: String,
        category: String,
        thumbnailKey: String,
        liveSourceType: String, // "CAMERA" or "SCREEN"
        useFrontCamera: Boolean,
        enableLiveAds: Boolean
    ) {
        viewModelScope.launch {
            val sourceLabel = if (liveSourceType == "SCREEN") "شاشة الجوال 📱" else "الكاميرا 🎥"
            val liveVideo = VideoEntity(
                title = title.ifBlank { "بث مباشر ($sourceLabel) مع المتابعين 🔴" },
                description = "بث مباشر حي عبر $sourceLabel من حساب ${_currentUserEmail.value}! شاركنا في الدردشة المباشرة.",
                channelId = 1,
                channelName = _currentUserName.value,
                channelAvatarColor = 0xFFFF0000,
                category = category,
                durationSeconds = 0,
                viewsCount = 125,
                likesCount = 34,
                isSaved = true,
                publishedAtText = "يبث الآن ($sourceLabel)",
                thumbnailKey = thumbnailKey,
                isLive = true,
                liveSourceType = liveSourceType,
                useFrontCamera = useFrontCamera,
                liveViewersCount = 145,
                isDownloaded = false,
                isMonetized = enableLiveAds,
                adFormat = "إعلانات البث المباشر + Super Chat",
                cpmUsd = 9.0,
                adRevenueUsd = if (enableLiveAds) 18.50 else 0.0,
                adImpressions = if (enableLiveAds) 420 else 0,
                avgRetentionPercent = 88,
                ctrPercent = 13.5,
                watchTimeHours = 32.0,
                isMyUpload = true
            )
            val newId = repository.insertVideo(liveVideo)
            repository.insertLiveChatMessage(
                LiveChatMessageEntity(
                    videoId = newId,
                    senderName = "نظام البث المباشر",
                    senderColor = 0xFF10B981,
                    message = "بدأ البث المباشر عبر ($sourceLabel) بنجاح! تم تنبيه المشتركين 🔔",
                    superChatAmountUsd = 0.0
                )
            )
            val notifTitle = "🔴 بث مباشر ($sourceLabel) من ${_currentUserName.value}"
            val notifBody = "انضم الآن للبث المباشر: «${liveVideo.title}»"
            repository.insertNotification(
                NotificationEntity(
                    title = notifTitle,
                    message = notifBody,
                    timeAgo = "الآن",
                    type = "LIVE",
                    relatedVideoId = newId,
                    isRead = false
                )
            )
            NotificationHelper.sendSystemNotification(getApplication(), notifTitle, notifBody)
            _showGoLiveDialog.value = false
            _activeVideoId.value = newId
            showToastMessage("أنت الآن على الهواء مباشرة عبر $sourceLabel 🔴")
        }
    }

    fun toggleLiveSourceOnActiveVideo(video: VideoEntity, newSourceType: String, useFront: Boolean) {
        viewModelScope.launch {
            repository.updateVideo(
                video.copy(
                    liveSourceType = newSourceType,
                    useFrontCamera = useFront
                )
            )
            val label = if (newSourceType == "SCREEN") "بث شاشة الجوال 📱" else "بث الكاميرا 🎥"
            showToastMessage("تم التبديل إلى: $label")
        }
    }

    fun recordAdImpressionAndEarn(video: VideoEntity, bonusUsd: Double = 0.85) {
        viewModelScope.launch {
            val updated = video.copy(
                adImpressions = video.adImpressions + 120,
                adRevenueUsd = video.adRevenueUsd + bonusUsd,
                viewsCount = video.viewsCount + 50
            )
            repository.updateVideo(updated)
            showToastMessage("تم احتساب ظهور إعلاني جديد (+$${String.format("%.2f", bonusUsd)}) في أرباح الفيديو 💰")
        }
    }

    fun toggleVideoMonetization(video: VideoEntity) {
        viewModelScope.launch {
            val next = !video.isMonetized
            repository.updateVideo(video.copy(isMonetized = next))
            showToastMessage(
                if (next) "تم تفعيل الربح من الإعلانات على «${video.title.take(24)}...»"
                else "تم إيقاف الإعلانات على هذا الفيديو"
            )
        }
    }

    fun simulateAdCampaignBoost() {
        viewModelScope.launch {
            val currentList = allVideos.value
            currentList.filter { it.isMonetized }.forEach { v ->
                val addedImpressions = 1000
                val addedRevenue = (v.cpmUsd * 1.0)
                repository.updateVideo(
                    v.copy(
                        viewsCount = v.viewsCount + 1250,
                        adImpressions = v.adImpressions + addedImpressions,
                        adRevenueUsd = v.adRevenueUsd + addedRevenue,
                        watchTimeHours = v.watchTimeHours + 42.0
                    )
                )
            }
            repository.insertNotification(
                NotificationEntity(
                    title = "📈 دفعة جديدة من أرباح الإعلانات (AdSense)",
                    message = "تم تسجيل +1,000 ظهور إعلاني جديد لكل فيديو مفعل للربح وتحديث رصيدك في الاستوديو.",
                    timeAgo = "الآن",
                    type = "MONETIZATION",
                    isRead = false
                )
            )
            showToastMessage("تم تحديث الإحصائيات وإضافة عوائد +1,000 ظهور إعلاني إلى رصيد قناتك 💰")
        }
    }

    fun togglePreRollAds() { _preRollAdsEnabled.value = !_preRollAdsEnabled.value }
    fun toggleMidRollAds() { _midRollAdsEnabled.value = !_midRollAdsEnabled.value }
    fun toggleSponsorCards() { _sponsorCardsEnabled.value = !_sponsorCardsEnabled.value }
    fun toggleSuperChat() { _superChatEnabled.value = !_superChatEnabled.value }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun markNotificationRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun sendTestSubscriberNotification() {
        viewModelScope.launch {
            val title = "🔔 تنبيه محتوى جديد - YouTube"
            val msg = "تم رفع فيديو جديد على القناة المشترك بها! اضغط للمشاهدة والتفاعل الآن."
            repository.insertNotification(
                NotificationEntity(
                    title = title,
                    message = msg,
                    timeAgo = "الآن",
                    type = "UPLOAD",
                    relatedVideoId = allVideos.value.firstOrNull()?.id,
                    isRead = false
                )
            )
            NotificationHelper.sendSystemNotification(getApplication(), title, msg)
            showToastMessage("تم إرسال إشعار فوري إلى شريط النظام ومركز الإشعارات 🔔")
        }
    }
}
