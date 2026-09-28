package com.example.data

import kotlinx.coroutines.flow.Flow

class VidioRepository(private val dao: VidioDao) {
    val allVideos: Flow<List<VideoEntity>> = dao.getAllVideos()
    val downloadedVideos: Flow<List<VideoEntity>> = dao.getDownloadedVideos()
    val savedVideos: Flow<List<VideoEntity>> = dao.getSavedVideos()
    val allChannels: Flow<List<ChannelEntity>> = dao.getAllChannels()
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val allComments: Flow<List<CommentEntity>> = dao.getAllComments()

    fun getCommentsForVideo(videoId: Int): Flow<List<CommentEntity>> = dao.getCommentsForVideo(videoId)
    fun getLiveChatForVideo(videoId: Int): Flow<List<LiveChatMessageEntity>> = dao.getLiveChatForVideo(videoId)

    suspend fun ensureSeedData() {
        if (dao.getVideoCount() > 0) return

        val channels = listOf(
            ChannelEntity(
                id = 1,
                name = "قناتي الرسمية • Rawaf",
                handle = "@rawaf.code",
                email = "rawaf.code@gmail.com",
                subscribersCount = 148500,
                isSubscribed = true,
                notificationsEnabled = true,
                avatarColor = 0xFFFF0000,
                bio = "القناة الرسمية لصناعة المحتوى التقني، مراجعات الفيديو، والبثوث المباشرة التفاعلية.",
                isMyChannel = true
            ),
            ChannelEntity(
                id = 2,
                name = "أكاديمية الكود العربي",
                handle = "@arab_code_academy",
                email = "academy.code@gmail.com",
                subscribersCount = 392000,
                isSubscribed = true,
                notificationsEnabled = true,
                avatarColor = 0xFF06B6D4,
                bio = "شروحات معمقة في هندسة البرمجيات وتطوير تطبيقات أندرويد الحديثة."
            ),
            ChannelEntity(
                id = 3,
                name = "ساحة الألعاب المباشرة",
                handle = "@cyber_arena_live",
                email = "arena.gaming@gmail.com",
                subscribersCount = 610000,
                isSubscribed = true,
                notificationsEnabled = true,
                avatarColor = 0xFF8B5CF6,
                bio = "بثوث يومية لأقوى البطولات الإلكترونية وتحديات المتابعين من شاشة الجوال والكاميرا."
            ),
            ChannelEntity(
                id = 4,
                name = "آفاق وثائقية 4K",
                handle = "@arabia_docs_4k",
                email = "arabia.docs@gmail.com",
                subscribersCount = 845000,
                isSubscribed = false,
                notificationsEnabled = false,
                avatarColor = 0xFFF59E0B,
                bio = "رحلات سينمائية في صحاري الجزيرة العربية، العلا، والطبيعة الخلابة بدقة 4K."
            ),
            ChannelEntity(
                id = 5,
                name = "مايكروفون البودكاست",
                handle = "@studio_podcast_ar",
                email = "podcast.studio@gmail.com",
                subscribersCount = 215000,
                isSubscribed = true,
                notificationsEnabled = false,
                avatarColor = 0xFF10B981,
                bio = "حوارات أسبوعية مع رواد الأعمال وصناع المحتوى حول بناء القنوات الناجحة."
            )
        )
        dao.insertChannels(channels)

        val now = System.currentTimeMillis()
        val videos = listOf(
            VideoEntity(
                id = 1,
                title = "بث مباشر شاشة الجوال: نهائي بطولة السباقات 2026 + تحديات المتابعين 🔥",
                description = "بث مباشر حي من شاشة الجوال! شاركوا في الدردشة المباشرة وصوتوا لأفضل متسابق. الإعلانات والسوبر شات مفعلة لدعم جوائز القناة.",
                channelId = 3,
                channelName = "ساحة الألعاب المباشرة",
                channelAvatarColor = 0xFF8B5CF6,
                category = "ألعاب",
                durationSeconds = 0,
                viewsCount = 48200,
                likesCount = 6420,
                isSaved = true,
                publishedAtText = "يبث الآن مباشر",
                thumbnailKey = "gaming",
                isLive = true,
                liveSourceType = "SCREEN",
                liveViewersCount = 3480,
                isDownloaded = false,
                isMonetized = true,
                adFormat = "إعلانات البث المباشر + Super Chat",
                cpmUsd = 8.20,
                adRevenueUsd = 395.40,
                adImpressions = 48200,
                avgRetentionPercent = 84,
                ctrPercent = 12.4,
                watchTimeHours = 3120.5,
                isMyUpload = false,
                createdAtMillis = now - 600_000L
            ),
            VideoEntity(
                id = 2,
                title = "كيف صممت استوديو صناعة المحتوى والبرمجة المتكامل بأقل تكلفة! (دليل شامل)",
                description = "في هذا الفيديو من قناتي أشارككم تفاصيل تجهيز المكتب، الإضاءة السينمائية، الكاميرا، وإعدادات المونتاج لرفع جودة الفيديوهات ومضاعفة أرباح الإعلانات.",
                channelId = 1,
                channelName = "قناتي الرسمية • Rawaf",
                channelAvatarColor = 0xFFFF0000,
                category = "تقنية وبرمجة",
                durationSeconds = 1125, // 18:45
                viewsCount = 186400,
                likesCount = 19400,
                isSaved = true,
                publishedAtText = "قبل 5 ساعات",
                thumbnailKey = "tech",
                isLive = false,
                isDownloaded = true,
                downloadQuality = "1080p Full HD",
                fileSizeMb = 210,
                isMonetized = true,
                adFormat = "إعلانات فيديو قابلة للتخطي + منتصف الفيديو",
                cpmUsd = 7.50,
                adRevenueUsd = 1398.00,
                adImpressions = 186400,
                avgRetentionPercent = 81,
                ctrPercent = 10.8,
                watchTimeHours = 4720.0,
                isMyUpload = true,
                createdAtMillis = now - 3_600_000L * 5
            ),
            VideoEntity(
                id = 3,
                title = "رحلة ليلية في صحراء العلا تحت مجرة درب التبانة | وثائقي سينمائي 4K",
                description = "استكشاف بصري مذهل لتكوينات الصخور الرملية في العلا والنجوم بعيداً عن التلوث الضوئي، مصور بدقة 4K HDR.",
                channelId = 4,
                channelName = "آفاق وثائقية 4K",
                channelAvatarColor = 0xFFF59E0B,
                category = "وثائقي",
                durationSeconds = 1640, // 27:20
                viewsCount = 524000,
                likesCount = 58900,
                isSaved = true,
                publishedAtText = "قبل يومين",
                thumbnailKey = "doc",
                isLive = false,
                isDownloaded = true,
                downloadQuality = "1080p Full HD",
                fileSizeMb = 340,
                isMonetized = true,
                adFormat = "إعلانات فيديو قابلة للتخطي",
                cpmUsd = 6.10,
                adRevenueUsd = 3196.40,
                adImpressions = 524000,
                avgRetentionPercent = 74,
                ctrPercent = 9.2,
                watchTimeHours = 9450.0,
                isMyUpload = false,
                createdAtMillis = now - 3_600_000L * 48
            ),
            VideoEntity(
                id = 4,
                title = "بث مباشر بالكاميرا: دردشة مسائية من الاستوديو ومراجعة قنوات المتابعين 🎥",
                description = "بث مباشر عبر كاميرا الاستوديو للرد على استفساراتكم ومراجعة قنواتكم مباشرة على الهواء.",
                channelId = 1,
                channelName = "قناتي الرسمية • Rawaf",
                channelAvatarColor = 0xFFFF0000,
                category = "بودكاست وإنتاج",
                durationSeconds = 0,
                viewsCount = 94300,
                likesCount = 11200,
                isSaved = false,
                publishedAtText = "يبث الآن مباشر",
                thumbnailKey = "podcast",
                isLive = true,
                liveSourceType = "CAMERA",
                liveViewersCount = 1420,
                isDownloaded = false,
                isMonetized = true,
                adFormat = "إعلانات فيديو + بطاقات رعاية",
                cpmUsd = 8.80,
                adRevenueUsd = 829.84,
                adImpressions = 94300,
                avgRetentionPercent = 86,
                ctrPercent = 11.5,
                watchTimeHours = 2110.0,
                isMyUpload = true,
                createdAtMillis = now - 3_600_000L * 2
            ),
            VideoEntity(
                id = 5,
                title = "كورس بناء تطبيقات أندرويد الحديثة بـ Jetpack Compose و Room من الصفر",
                description = "الدرس الشامل لبناء واجهات مستخدم تفاعلية وسريعة مع قواعد بيانات محلية تدعم العمل بدون إنترنت والوضع الليلي.",
                channelId = 2,
                channelName = "أكاديمية الكود العربي",
                channelAvatarColor = 0xFF06B6D4,
                category = "تقنية وبرمجة",
                durationSeconds = 2715, // 45:15
                viewsCount = 312000,
                likesCount = 34500,
                isSaved = false,
                publishedAtText = "قبل أسبوع",
                thumbnailKey = "tech",
                isLive = false,
                isDownloaded = false,
                isMonetized = true,
                adFormat = "إعلانات فيديو قابلة للتخطي",
                cpmUsd = 6.90,
                adRevenueUsd = 2152.80,
                adImpressions = 312000,
                avgRetentionPercent = 69,
                ctrPercent = 8.4,
                watchTimeHours = 6840.0,
                isMyUpload = false,
                createdAtMillis = now - 3_600_000L * 168
            )
        )
        dao.insertVideos(videos)

        val comments = listOf(
            CommentEntity(
                videoId = 2,
                authorName = "سلمان الحربي",
                authorEmail = "salman.h@gmail.com",
                authorAvatarColor = 0xFF06B6D4,
                content = "ما شاء الله جودة الإضاءة والصوت في هذا الفيديو خرافية! استفدت جداً من توزيع الإضاءة الخلفية.",
                timestampText = "قبل 3 ساعات",
                likesCount = 142,
                isCreatorHearted = true,
                isPinned = true
            ),
            CommentEntity(
                videoId = 2,
                authorName = "سارة العتيبي",
                authorEmail = "sara.otaibi@gmail.com",
                authorAvatarColor = 0xFF10B981,
                content = "حفظت الفيديو في مكتبتي وحملته للمشاهدة بدون إنترنت في الطائرة، الشرح واضح ومرتب جداً 👏",
                timestampText = "قبل ساعتين",
                likesCount = 58,
                isCreatorHearted = true,
                isPinned = false
            ),
            CommentEntity(
                videoId = 1,
                authorName = "فهد الدوسري",
                authorEmail = "fahad.d@gmail.com",
                authorAvatarColor = 0xFFF59E0B,
                content = "أقوى بث مباشر من شاشة الجوال! الدقة 60 فريم واضحة جداً 🔥🔥",
                timestampText = "قبل 10 دقائق",
                likesCount = 89,
                isCreatorHearted = false,
                isPinned = true
            ),
            CommentEntity(
                videoId = 3,
                authorName = "نورة المنصور",
                authorEmail = "noura.m@gmail.com",
                authorAvatarColor = 0xFFEC4899,
                content = "مشاهد العلا في الليل كأنها من كوكب آخر، فخورين بهذا الإبداع البصري.",
                timestampText = "قبل يوم",
                likesCount = 215,
                isCreatorHearted = true,
                isPinned = false
            )
        )
        dao.insertComments(comments)

        val notifications = listOf(
            NotificationEntity(
                title = "🔴 بث مباشر الآن: ساحة الألعاب المباشرة",
                message = "بدأ البث المباشر من شاشة الجوال: نهائي بطولة السباقات 2026 + تحديات المتابعين",
                timeAgo = "منذ 10 دقائق",
                type = "LIVE",
                relatedVideoId = 1,
                isRead = false,
                createdAtMillis = now - 600_000L
            ),
            NotificationEntity(
                title = "💰 تحديث أرباح الإعلانات في قناتك",
                message = "حقق فيديو «كيف صممت استوديو صناعة المحتوى» +$142.50 إضافية من إعلانات الفيديو اليوم!",
                timeAgo = "منذ ساعتين",
                type = "MONETIZATION",
                relatedVideoId = 2,
                isRead = false,
                createdAtMillis = now - 7_200_000L
            ),
            NotificationEntity(
                title = "🎬 فيديو جديد من أكاديمية الكود العربي",
                message = "تم رفع: كورس بناء تطبيقات أندرويد الحديثة بـ Jetpack Compose و Room",
                timeAgo = "منذ يوم",
                type = "UPLOAD",
                relatedVideoId = 5,
                isRead = true,
                createdAtMillis = now - 86_400_000L
            )
        )
        dao.insertNotifications(notifications)

        val liveChats = listOf(
            LiveChatMessageEntity(
                videoId = 1,
                senderName = "ريان القحطاني",
                senderColor = 0xFF06B6D4,
                message = "يا سلام على الدرفت في المنعطف الثالث! بث الشاشة واضح جداً 🏎️💨",
                superChatAmountUsd = 0.0,
                timestampMillis = now - 180_000L
            ),
            LiveChatMessageEntity(
                videoId = 1,
                senderName = "خالد الجاسر",
                senderColor = 0xFFF59E0B,
                message = "دعم للقناة على التغطية الأسطورية استمروا يا أبطال! 🌟",
                superChatAmountUsd = 20.0,
                timestampMillis = now - 120_000L
            ),
            LiveChatMessageEntity(
                videoId = 1,
                senderName = "ليلى الشمري",
                senderColor = 0xFF10B981,
                message = "الصوت والصورة 10/10 بدون أي تقطيع 👏",
                superChatAmountUsd = 0.0,
                timestampMillis = now - 60_000L
            )
        )
        dao.insertLiveChatMessages(liveChats)
    }

    suspend fun insertVideo(video: VideoEntity): Int = dao.insertVideo(video).toInt()
    suspend fun updateVideo(video: VideoEntity) = dao.updateVideo(video)
    suspend fun deleteVideo(videoId: Int) = dao.deleteVideo(videoId)
    suspend fun deleteVideosByChannel(channelId: Int) = dao.deleteVideosByChannel(channelId)

    suspend fun updateChannel(channel: ChannelEntity) = dao.updateChannel(channel)
    suspend fun deleteChannel(channelId: Int) = dao.deleteChannel(channelId)

    suspend fun insertComment(comment: CommentEntity) = dao.insertComment(comment)
    suspend fun updateComment(comment: CommentEntity) = dao.updateComment(comment)
    suspend fun deleteComment(commentId: Int) = dao.deleteComment(commentId)

    suspend fun insertNotification(notification: NotificationEntity) = dao.insertNotification(notification)
    suspend fun markAllNotificationsRead() = dao.markAllNotificationsRead()
    suspend fun markNotificationRead(id: Int) = dao.markNotificationRead(id)

    suspend fun insertLiveChatMessage(msg: LiveChatMessageEntity) = dao.insertLiveChatMessage(msg)
}
