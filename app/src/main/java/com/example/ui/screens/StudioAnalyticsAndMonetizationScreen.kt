package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.ChannelEntity
import com.example.data.VideoEntity
import com.example.ui.components.formatCount

@Composable
fun StudioAnalyticsAndMonetizationScreen(
    videos: List<VideoEntity>,
    channels: List<ChannelEntity>,
    preRollAdsEnabled: Boolean,
    midRollAdsEnabled: Boolean,
    sponsorCardsEnabled: Boolean,
    superChatEnabled: Boolean,
    onTogglePreRoll: () -> Unit,
    onToggleMidRoll: () -> Unit,
    onToggleSponsorCards: () -> Unit,
    onToggleSuperChat: () -> Unit,
    onToggleVideoMonetization: (VideoEntity) -> Unit,
    onSimulateAdCampaign: () -> Unit,
    onOpenUpload: () -> Unit,
    onOpenGoLive: () -> Unit,
    onOpenVideo: (VideoEntity) -> Unit
) {
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0 = التحليلات ونسبة المشاهدة, 1 = الربح من الإعلانات

    val totalViews = videos.sumOf { it.viewsCount }
    val totalWatchHours = videos.sumOf { it.watchTimeHours }
    val totalRevenueUsd = videos.sumOf { it.adRevenueUsd }
    val totalAdImpressions = videos.sumOf { it.adImpressions }
    val avgRetentionAll = if (videos.isNotEmpty()) videos.map { it.avgRetentionPercent }.average().toInt() else 80
    val avgCtrAll = if (videos.isNotEmpty()) videos.map { it.ctrPercent }.average() else 9.8
    val myChannel = channels.firstOrNull { it.isMyChannel } ?: channels.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("studio_screen"),
        contentPadding = PaddingValues(14.dp, 12.dp, 14.dp, 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Creator Studio Header & Quick Actions
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = myChannel?.name ?: "قناتي الإبداعية • Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "${formatCount((myChannel?.subscribersCount ?: 148500).toLong())} مشترك • شريك إعلاني موثق ✓",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF10B981)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onOpenUpload,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("studio_upload_btn")
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("رفع فيديو", style = MaterialTheme.typography.labelSmall)
                            }
                            Button(
                                onClick = onOpenGoLive,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("studio_golive_btn")
                            ) {
                                Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("بث مباشر", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sub-tab selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedSubTab == 0,
                            onClick = { selectedSubTab = 0 },
                            label = { Text("📊 تحليلات القناة ونسبة المشاهدة") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("studio_tab_analytics")
                        )
                        FilterChip(
                            selected = selectedSubTab == 1,
                            onClick = { selectedSubTab = 1 },
                            label = { Text("💰 الربح من الإعلانات") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF059669),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("studio_tab_monetization")
                        )
                    }
                }
            }
        }

        // KPI Grid (Always visible for instant insights)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "إجمالي المشاهدات",
                    value = formatCount(totalViews),
                    subtitle = "+18.4% آخر 28 يوم",
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "متوسط نسبة المشاهدة",
                    value = "$avgRetentionAll%",
                    subtitle = "CTR النقر ${String.format("%.1f", avgCtrAll)}%",
                    accentColor = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "ساعات المشاهدة",
                    value = "${String.format("%.0f", totalWatchHours)} ساعة",
                    subtitle = "تجاوز شرط الـ 4,000 ساعة ✓",
                    accentColor = Color(0xFF06B6D4),
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "أرباح الإعلانات المقدرة",
                    value = "$${String.format("%.2f", totalRevenueUsd)}",
                    subtitle = "${formatCount(totalAdImpressions.toLong())} ظهور إعلاني",
                    accentColor = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (selectedSubTab == 0) {
            // ANALYTICS & AUDIENCE RETENTION CURVE
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
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "منحنى الاحتفاظ بالجمهور ونسبة المشاهدة",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.16f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "ممتاز ($avgRetentionAll%)",
                                    color = Color(0xFF10B981),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "يوضح الرسم البياني نسبة المشاهدين المستمرين في مشاهدة الفيديو من الدقيقة الأولى حتى النهاية:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom Canvas Retention Curve
                        val retentionPoints = listOf(1.0f, 0.94f, 0.89f, 0.86f, 0.83f, 0.85f, 0.79f, 0.76f, 0.74f)
                        val primaryColor = MaterialTheme.colorScheme.primary
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                .padding(12.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val stepX = w / (retentionPoints.size - 1).coerceAtLeast(1)

                                // Horizontal reference grid lines
                                for (r in 1..3) {
                                    val yGrid = h * (r / 4f)
                                    drawLine(
                                        color = Color.Gray.copy(alpha = 0.25f),
                                        start = Offset(0f, yGrid),
                                        end = Offset(w, yGrid),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }

                                val linePath = Path()
                                val fillPath = Path()

                                retentionPoints.forEachIndexed { index, ratio ->
                                    val px = index * stepX
                                    val py = h * (1f - ratio * 0.88f)
                                    if (index == 0) {
                                        linePath.moveTo(px, py)
                                        fillPath.moveTo(px, h)
                                        fillPath.lineTo(px, py)
                                    } else {
                                        linePath.lineTo(px, py)
                                        fillPath.lineTo(px, py)
                                    }
                                }
                                fillPath.lineTo(w, h)
                                fillPath.close()

                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            primaryColor.copy(alpha = 0.42f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                drawPath(
                                    path = linePath,
                                    color = primaryColor,
                                    style = Stroke(width = 3.5.dp.toPx())
                                )

                                // Data points
                                retentionPoints.forEachIndexed { index, ratio ->
                                    val px = index * stepX
                                    val py = h * (1f - ratio * 0.88f)
                                    drawCircle(
                                        color = Color.White,
                                        radius = 4.5.dp.toPx(),
                                        center = Offset(px, py)
                                    )
                                    drawCircle(
                                        color = primaryColor,
                                        radius = 3.dp.toPx(),
                                        center = Offset(px, py)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("00:00 (البداية 100%)", style = MaterialTheme.typography.labelSmall)
                            Text("منتصف الفيديو (ذروة التفاعل 85%)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text("النهاية (74%)", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Traffic Sources Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🔍 مصادر الزيارات واكتشاف المشاهدين",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        TrafficSourceRow("الصفحة الرئيسية والمقترحات", 0.46f, Color(0xFFFF2A54))
                        TrafficSourceRow("إشعارات المشتركين الفورية 🔔", 0.28f, Color(0xFF10B981))
                        TrafficSourceRow("بحث التطبيق والكلمات المفتاحية", 0.16f, Color(0xFF06B6D4))
                        TrafficSourceRow("خلاصة الاشتراكات والبث المباشر", 0.10f, Color(0xFFF59E0B))
                    }
                }
            }

            // Per-video performance & view percentage breakdown
            item {
                Text(
                    text = "🎬 تحليل أداء الفيديوهات ونسبة المشاهدة التفصيلية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            items(videos, key = { it.id }) { video ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenVideo(video) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = video.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$${String.format("%.2f", video.adRevenueUsd)}",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "المشاهدات: ${formatCount(video.viewsCount)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "نسبة المشاهدة: ${video.avgRetentionPercent}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFF59E0B),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "النقر CTR: ${video.ctrPercent}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (video.avgRetentionPercent / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (video.avgRetentionPercent >= 80) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        } else {
            // AD MONETIZATION & REVENUE CENTER
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
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
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "مركز تحقيق الربح والإعلانات (AdSense)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "متوسط الـ CPM: $7.45 • RPM الصافي: $5.90 لكل 1000 مشاهدة",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Simulate Ad Campaign Boost Button
                        Button(
                            onClick = onSimulateAdCampaign,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("simulate_ad_campaign_button")
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "محاكاة حملة إعلانية (+1,000 ظهور إعلاني وتحديث الأرباح)",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "⚙️ إعدادات صيغ الإعلانات داخل التطبيق",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        AdFormatSwitchRow(
                            title = "إعلانات الفيديو القابلة للتخطي (Pre-roll Ads)",
                            subtitle = "تظهر قبل بدء الفيديو وتمنح أعلى عائد CPM لصانع المحتوى",
                            checked = preRollAdsEnabled,
                            onCheckedChange = { onTogglePreRoll() }
                        )
                        AdFormatSwitchRow(
                            title = "إعلانات منتصف الفيديو (Mid-roll Ads)",
                            subtitle = "تظهر تلقائياً في الفيديوهات الأطول من 8 دقائق",
                            checked = midRollAdsEnabled,
                            onCheckedChange = { onToggleMidRoll() }
                        )
                        AdFormatSwitchRow(
                            title = "بطاقات الرعاية والبانر التفاعلي (Sponsor Cards)",
                            subtitle = "شريط إعلاني غير مزعج أسفل مشغل الفيديو",
                            checked = sponsorCardsEnabled,
                            onCheckedChange = { onToggleSponsorCards() }
                        )
                        AdFormatSwitchRow(
                            title = "دعم المشاهدين المباشر (Super Chat & Stickers)",
                            subtitle = "يتيح للمشاهدين إرسال دعم مالي مباشر أثناء البث الحي",
                            checked = superChatEnabled,
                            onCheckedChange = { onToggleSuperChat() }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "💵 التحكم في إعلانات الفيديوهات وأرباح كل فيديو",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            items(videos, key = { it.id }) { video ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = video.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "الربح: $${String.format("%.2f", video.adRevenueUsd)} • الظهور: ${formatCount(video.adImpressions.toLong())} • CPM $${video.cpmUsd}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (video.isMonetized) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = video.isMonetized,
                            onCheckedChange = { onToggleVideoMonetization(video) },
                            modifier = Modifier.testTag("monetize_switch_${video.id}")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricStatCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = accentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TrafficSourceRow(label: String, fraction: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Text(
                text = "${(fraction * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color
        )
    }
}

@Composable
private fun AdFormatSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
