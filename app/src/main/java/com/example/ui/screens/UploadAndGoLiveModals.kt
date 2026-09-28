package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.NotificationEntity
import com.example.ui.components.getThumbnailResId

@Composable
fun CreateActionSheetDialog(
    onDismiss: () -> Unit,
    onSelectUploadVideo: () -> Unit,
    onSelectGoLiveCamera: () -> Unit,
    onSelectGoLiveScreen: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إنشاء محتوى في يوتيوب",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Option 1: Upload Video
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectUploadVideo() }
                        .testTag("create_sheet_upload_video")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF0000).copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = Color(0xFFFF0000)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("تحميـل فيديو (رفع فيديوهاتي)", fontWeight = FontWeight.ExtraBold)
                            Text(
                                "اختر فيديو من جوالك وانشره في قناتك واحفظه في مكتبتك",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Option 2: Go Live via Camera
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectGoLiveCamera() }
                        .testTag("create_sheet_live_camera")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF0000).copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = Color(0xFFFF0000)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("بث مباشر عبر الكاميرا 🎥", fontWeight = FontWeight.ExtraBold)
                            Text(
                                "افتح كاميرا جوالك الأمامية أو الخلفية للبث المباشر مع المتابعين",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Option 3: Go Live via Phone Screen
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectGoLiveScreen() }
                        .testTag("create_sheet_live_screen")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3EA6FF).copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = Color(0xFF3EA6FF)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("بث مباشر لشاشة الجوال 📱", fontWeight = FontWeight.ExtraBold)
                            Text(
                                "شارك شاشة جوالك مباشرة للألعاب والشروحات بدقة 60FPS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("إغلاق") }
        }
    )
}

@Composable
fun UploadVideoDialog(
    onDismiss: () -> Unit,
    onPublish: (
        title: String,
        description: String,
        category: String,
        durationMinutes: Int,
        thumbnailKey: String,
        customMediaUri: String?,
        isMonetized: Boolean,
        adFormat: String,
        cpmUsd: Double,
        sendNotification: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("تقنية وبرمجة") }
    var durationMinutesText by remember { mutableStateOf("14") }
    var thumbnailKey by remember { mutableStateOf("tech") }
    var pickedUriString by remember { mutableStateOf<String?>(null) }
    var isMonetized by remember { mutableStateOf(true) }
    var sendNotification by remember { mutableStateOf(true) }

    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedUriString = uri.toString()
        }
    }

    val notifPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = Color(0xFFFF0000)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("رفع فيديو جديد وحفظه في قناتي", fontWeight = FontWeight.ExtraBold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        mediaPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pick_device_media_button")
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (pickedUriString != null) "تم اختيار فيديو/صورة من جوالك ✓"
                        else "اختيار فيديو أو صورة مصغرة من الجهاز"
                    )
                }

                Text("أو اختر غلاف فيديو احترافي:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "tech" to "تقنية",
                        "gaming" to "ألعاب",
                        "doc" to "وثائقي",
                        "podcast" to "بودكاست"
                    ).forEach { (key, label) ->
                        val selected = thumbnailKey == key
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { thumbnailKey = key }
                        ) {
                            Image(
                                painter = painterResource(id = getThumbnailResId(key)),
                                contentDescription = label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(48.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (selected) 2.5.dp else 0.dp,
                                        color = if (selected) Color(0xFFFF0000) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                            Text(label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الفيديو") },
                    placeholder = { Text("مثال: فلوق جديد أو شرح تقني كامل") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("وصف الفيديو") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_desc_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("تقنية وبرمجة", "ألعاب", "وثائقي", "بودكاست وإنتاج").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.substringBefore(" "), style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = durationMinutesText,
                    onValueChange = { durationMinutesText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("مدة الفيديو (بالدقائق)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("💰 تفعيل الربح من الإعلانات", fontWeight = FontWeight.Bold)
                        Text(
                            "إعلانات فيديو قابلة للتخطي (CPM $7.50)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isMonetized,
                        onCheckedChange = { isMonetized = it },
                        modifier = Modifier.testTag("upload_monetize_switch")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🔔 إرسال إشعار للمشتركين", fontWeight = FontWeight.Bold)
                        Text(
                            "تنبيه فوري عند نشر الفيديو وحفظه في مكتبتك",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = sendNotification,
                        onCheckedChange = {
                            sendNotification = it
                            if (it && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (sendNotification && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    onPublish(
                        title,
                        description,
                        category,
                        durationMinutesText.toIntOrNull() ?: 12,
                        thumbnailKey,
                        pickedUriString,
                        isMonetized,
                        "إعلانات فيديو قابلة للتخطي + منتصف الفيديو",
                        7.50,
                        sendNotification
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF0000),
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("confirm_upload_button")
            ) {
                Text("نشر وحفظ الفيديو الآن", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun StartLiveStreamDialog(
    initialSourceType: String = "CAMERA",
    onDismiss: () -> Unit,
    onStartLive: (
        title: String,
        category: String,
        thumbnailKey: String,
        liveSourceType: String,
        useFrontCamera: Boolean,
        enableAds: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ألعاب") }
    var thumbnailKey by remember { mutableStateOf("gaming") }
    // User explicitly chooses between "CAMERA" (الكاميرا حقته) or "SCREEN" (شاشة جواله)
    var liveSourceType by remember { mutableStateOf(initialSourceType) }
    var useFrontCamera by remember { mutableStateOf(true) }
    var enableAds by remember { mutableStateOf(true) }

    val cameraPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = Color(0xFFFF0000)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("إعداد البث المباشر 🔴", fontWeight = FontWeight.ExtraBold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "اختر مصدر البث المباشر:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                // Choice 1: Camera vs Choice 2: Phone Screen
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isCameraSelected = liveSourceType == "CAMERA"
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCameraSelected)
                                Color(0xFFFF0000).copy(alpha = 0.16f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isCameraSelected) 2.dp else 1.dp,
                                color = if (isCameraSelected) Color(0xFFFF0000) else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                liveSourceType = "CAMERA"
                                cameraPermLauncher.launch(Manifest.permission.CAMERA)
                            }
                            .testTag("select_live_camera_mode")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "الكاميرا",
                                tint = if (isCameraSelected) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "الكاميرا 🎥",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "بث بالكاميرا الأمامية أو الخلفية",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    val isScreenSelected = liveSourceType == "SCREEN"
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isScreenSelected)
                                Color(0xFF3EA6FF).copy(alpha = 0.16f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isScreenSelected) 2.dp else 1.dp,
                                color = if (isScreenSelected) Color(0xFF3EA6FF) else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { liveSourceType = "SCREEN" }
                            .testTag("select_live_screen_mode")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "شاشة الجوال",
                                tint = if (isScreenSelected) Color(0xFF3EA6FF) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "شاشة الجوال 📱",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "بث شاشة جوالك للألعاب والشروحات",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // If Camera is selected, allow picking Front or Back lens
                if (liveSourceType == "CAMERA") {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Cameraswitch,
                                    contentDescription = null,
                                    tint = Color(0xFFFF0000),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (useFrontCamera) "الكاميرا الأمامية (سيلفي)" else "الكاميرا الخلفية",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            TextButton(onClick = { useFrontCamera = !useFrontCamera }) {
                                Text("تبديل العدسة")
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان البث المباشر") },
                    placeholder = {
                        Text(
                            if (liveSourceType == "SCREEN") "مثال: بث مباشر شاشة الجوال وتحديات مع المتابعين 📱"
                            else "مثال: بث مباشر بالكاميرا ودردشة مع المتابعين 🎥"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("live_title_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ألعاب" to "gaming", "تقنية وبرمجة" to "tech", "بودكاست وإنتاج" to "podcast").forEach { (cat, key) ->
                        FilterChip(
                            selected = category == cat,
                            onClick = {
                                category = cat
                                thumbnailKey = key
                            },
                            label = { Text(cat.substringBefore(" ")) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("تفعيل إعلانات البث المباشر + Super Chat", fontWeight = FontWeight.Bold)
                        Text(
                            "إشعار فوري لجميع المشتركين عند بدء البث",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = enableAds, onCheckedChange = { enableAds = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onStartLive(
                        title,
                        category,
                        thumbnailKey,
                        liveSourceType,
                        useFrontCamera,
                        enableAds
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF0000),
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("confirm_start_live_button")
            ) {
                Text(
                    text = if (liveSourceType == "SCREEN") "بدء بث شاشة الجوال 🔴" else "بدء بث الكاميرا 🔴",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun EmailAccountDialog(
    currentEmail: String,
    currentName: String,
    currentHandle: String,
    onDismiss: () -> Unit,
    onSaveAccount: (email: String, name: String, handle: String) -> Unit
) {
    var email by remember { mutableStateOf(currentEmail) }
    var name by remember { mutableStateOf(currentName) }
    var handle by remember { mutableStateOf(currentHandle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = Color(0xFFFF0000)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("حساب البريد الإلكتروني في يوتيوب", fontWeight = FontWeight.ExtraBold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Active Account Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF0000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = email.firstOrNull()?.uppercase() ?: "R",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(name, fontWeight = FontWeight.ExtraBold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "متصل",
                                    tint = Color(0xFF2BA640),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = email,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF3EA6FF),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = handle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = "تعديل أو تبديل حساب البريد الإلكتروني:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("البريد الإلكتروني (Google / Email)") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_email_input")
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم القناة / صاحب الحساب") },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_name_input")
                )

                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("معرف القناة (@Handle)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Switch Presets
                Text(
                    text = "حسابات سريعة محفوظة:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = email == "rawaf.code@gmail.com",
                        onClick = {
                            email = "rawaf.code@gmail.com"
                            name = "قناتي الرسمية • Rawaf"
                            handle = "@rawaf.code"
                        },
                        label = { Text("rawaf.code@gmail.com", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveAccount(email, name, handle) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF0000),
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("save_email_account_button")
            ) {
                Text("حفظ وتفعيل الحساب ✓", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إغلاق") }
        }
    )
}

@Composable
fun AdminPinDialog(
    onDismiss: () -> Unit,
    onVerifyPin: (String) -> Boolean
) {
    var pinInput by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFFFF0000)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("دخول لوحة الإدارة العليا 🔐", fontWeight = FontWeight.ExtraBold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "تم تفعيل الدخول السري بعد الضغط المطول لمدة 5 ثوانٍ على الأيقونة. أدخل الرقم السري للإدارة للتحكم في القنوات (باند / حذف / تحذير):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        pinInput = it.filter { ch -> ch.isDigit() }.take(6)
                        hasError = false
                    },
                    label = { Text("الرقم السري للإدارة") },
                    placeholder = { Text("أدخل الرقم السري (1234)") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    isError = hasError,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )

                if (hasError) {
                    Text(
                        text = "❌ الرقم السري غير صحيح! جرب 1234",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ok = onVerifyPin(pinInput)
                    if (!ok) {
                        hasError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF0000),
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("confirm_admin_pin_button")
            ) {
                Text("دخول لوحة الإدارة 🔓", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun NotificationsDialog(
    notifications: List<NotificationEntity>,
    onDismiss: () -> Unit,
    onMarkAllRead: () -> Unit,
    onSendTestNotification: () -> Unit,
    onNotificationClick: (NotificationEntity) -> Unit
) {
    val notifPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        onSendTestNotification()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Color(0xFFFF0000)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("الإشعارات والتنبيهات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }
                IconButton(onClick = onMarkAllRead) {
                    Icon(Icons.Default.DoneAll, contentDescription = "تحديد الكل كمقروء")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onSendTestNotification()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_test_notification_btn")
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إرسال تنبيه فوري تجريبي للمشتركين 🔔")
                }

                LazyColumn(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(notifications, key = { it.id }) { item ->
                        val isWarning = item.type == "WARNING"
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isWarning -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                    !item.isRead -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNotificationClick(item) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                if (!item.isRead) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isWarning) Color(0xFFF59E0B) else Color(0xFFFF0000))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.timeAgo,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isWarning) Color(0xFFF59E0B) else Color(0xFFFF0000)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("إغلاق") }
        }
    )
}
