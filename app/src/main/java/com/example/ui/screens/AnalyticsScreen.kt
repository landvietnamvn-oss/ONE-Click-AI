package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeadModel
import com.example.data.model.SocialPlatform
import com.example.ui.components.MetricKpiCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AnalyticsScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val leads by viewModel.leads.collectAsState()
  var showAddLeadDialog by remember { mutableStateOf(false) }
  var selectedTab by remember { mutableStateOf(0) } // 0: Hiệu Quả & Thống Kê, 1: Khách Hàng / CRM

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundCanvas)
  ) {
    // Header
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = SurfaceWhite,
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Hiệu Quả & CRM Khách Hàng",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Slate900
              )
            )
            Text(
              text = "Báo cáo tiếp cận & chuyển đổi đa kênh",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Slate500,
                fontSize = 11.5.sp
              )
            )
          }

          if (selectedTab == 1) {
            Button(
              onClick = { showAddLeadDialog = true },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Thêm Khách", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Sub Tab Switcher
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Slate100)
            .padding(3.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(11.dp))
              .background(if (selectedTab == 0) SurfaceWhite else Color.Transparent)
              .clickable { selectedTab = 0 }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "📊 Thống Kê Đa Kênh",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedTab == 0) Indigo600 else Slate600,
                fontSize = 12.sp
              )
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(11.dp))
              .background(if (selectedTab == 1) SurfaceWhite else Color.Transparent)
              .clickable { selectedTab = 1 }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "👥 Khách Hàng (CRM)",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedTab == 1) Indigo600 else Slate600,
                fontSize = 12.sp
              )
            )
          }
        }
      }
    }

    if (selectedTab == 0) {
      // TAB 1: Analytics & AI Insights
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
      ) {
        // KPI Overview Cards
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            MetricKpiCard(
              title = "Tổng Lượt Xem",
              value = "142.6k",
              badgeText = "+28%",
              badgeBg = Emerald50,
              badgeColor = Emerald600,
              modifier = Modifier.weight(1f)
            )
            MetricKpiCard(
              title = "Tương Tác / Comment",
              value = "4,820",
              badgeText = "+19%",
              badgeBg = Indigo50,
              badgeColor = Indigo600,
              modifier = Modifier.weight(1f)
            )
          }
        }

        // AI Intelligence Advice Card
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(24.dp))
              .background(Indigo50)
              .border(1.dp, Indigo100, RoundedCornerShape(24.dp))
              .padding(18.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Indigo600),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(16.dp)
                  )
                }
                Text(
                  text = "AI INSIGHTS & ĐỀ XUẤT NỘI DUNG",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Indigo700,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp,
                    letterSpacing = 0.5.sp
                  )
                )
              }

              Text(
                text = "💡 Nội dung dạng Video ngắn TikTok & Shorts đang đem lại 68% tổng lượng khách nhắn tin (Lead). Đề xuất đăng vào khung giờ 11:30 và 19:30 để tối đa tỷ lệ tiếp cận khách mua nhà.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Slate800,
                  fontSize = 12.5.sp,
                  lineHeight = 18.sp
                )
              )
            }
          }
        }

        // Platform Performance Breakdown
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(24.dp))
              .background(SurfaceWhite)
              .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
              .padding(18.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
              Text(
                text = "HIỆU QUẢ THEO NỀN TẢNG",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Slate500,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  letterSpacing = 0.5.sp
                )
              )

              PlatformProgressItem(
                platform = SocialPlatform.TIKTOK,
                metricLabel = "64.2k lượt xem (45%)",
                progress = 0.45f
              )
              PlatformProgressItem(
                platform = SocialPlatform.FACEBOOK,
                metricLabel = "42.8k tiếp cận (30%)",
                progress = 0.30f
              )
              PlatformProgressItem(
                platform = SocialPlatform.YOUTUBE,
                metricLabel = "22.5k lượt xem (16%)",
                progress = 0.16f
              )
              PlatformProgressItem(
                platform = SocialPlatform.ZALO,
                metricLabel = "13.1k tương tác (9%)",
                progress = 0.09f
              )
            }
          }
        }
      }
    } else {
      // TAB 2: Real Estate CRM Leads
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
      ) {
        items(leads) { lead ->
          LeadItemCard(
            lead = lead,
            onStatusChange = { newStatus -> viewModel.updateLeadStatus(lead.id, newStatus) }
          )
        }
      }
    }
  }

  // Add Lead Modal Dialog
  if (showAddLeadDialog) {
    AddLeadModal(
      onDismiss = { showAddLeadDialog = false },
      onSave = { name, phone, platform, prop, notes ->
        viewModel.addLead(name, phone, platform, prop, notes)
        showAddLeadDialog = false
      }
    )
  }
}

@Composable
fun PlatformProgressItem(
  platform: SocialPlatform,
  metricLabel: String,
  progress: Float
) {
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = platform.shortLabel,
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = platform.brandColor
          )
        )
        Text(
          text = platform.displayName,
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Slate900
          )
        )
      }

      Text(
        text = metricLabel,
        style = MaterialTheme.typography.bodySmall.copy(
          color = Slate600,
          fontWeight = FontWeight.Medium,
          fontSize = 11.5.sp
        )
      )
    }

    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = platform.brandColor,
      trackColor = Slate100
    )
  }
}

@Composable
fun LeadItemCard(
  lead: LeadModel,
  onStatusChange: (String) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(SurfaceWhite)
      .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(lead.sourcePlatform.brandColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = lead.sourcePlatform.shortLabel,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = lead.sourcePlatform.brandColor,
                fontSize = 11.sp
              )
            )
          }

          Text(
            text = lead.customerName,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Slate900,
              fontSize = 14.sp
            )
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
              when (lead.status) {
                "Đã chốt cọc" -> Emerald50
                "Đang tư vấn" -> Indigo50
                else -> Amber50
              }
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = lead.status,
            style = MaterialTheme.typography.labelSmall.copy(
              color = when (lead.status) {
                "Đã chốt cọc" -> Emerald600
                "Đang tư vấn" -> Indigo600
                else -> Amber500
              },
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
          )
        }
      }

      Text(
        text = "Quan tâm: ${lead.propertyInterest}",
        style = MaterialTheme.typography.bodySmall.copy(
          color = Slate800,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.5.sp
        )
      )

      Text(
        text = lead.notes,
        style = MaterialTheme.typography.bodySmall.copy(
          color = Slate600,
          fontSize = 11.5.sp
        )
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.Phone, contentDescription = null, tint = Emerald600, modifier = Modifier.size(14.dp))
          Text(
            text = lead.phone,
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Emerald600,
              fontSize = 12.sp
            )
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (lead.status != "Đang tư vấn") {
            Button(
              onClick = { onStatusChange("Đang tư vấn") },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Indigo50, contentColor = Indigo700),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier.height(28.dp)
            ) {
              Text("Tư Vấn", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          if (lead.status != "Đã chốt cọc") {
            Button(
              onClick = { onStatusChange("Đã chốt cọc") },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Emerald50, contentColor = Emerald600),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier.height(28.dp)
            ) {
              Text("Chốt Cọc", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
fun AddLeadModal(
  onDismiss: () -> Unit,
  onSave: (String, String, SocialPlatform, String, String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var platform by remember { mutableStateOf(SocialPlatform.FACEBOOK) }
  var property by remember { mutableStateOf("Nhà 3 tầng Hải Châu Đà Nẵng") }
  var notes by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Thêm Khách Hàng Tiềm Năng", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Tên Khách Hàng") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Số Điện Thoại") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
          value = property,
          onValueChange = { property = it },
          label = { Text("Bất Động Sản Quan Tâm") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Ghi Chú Nhu Cầu") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank() && phone.isNotBlank()) {
            onSave(name, phone, platform, property, notes)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
      ) {
        Text("Lưu Khách Hàng")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Hủy") }
    },
    shape = RoundedCornerShape(24.dp),
    containerColor = SurfaceWhite
  )
}
