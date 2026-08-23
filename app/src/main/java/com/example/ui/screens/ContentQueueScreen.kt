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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PostModel
import com.example.data.model.PostStatus
import com.example.data.model.SocialPlatform
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationItem

@Composable
fun ContentQueueScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val posts by viewModel.posts.collectAsState()

  var selectedStatusFilter by remember { mutableStateOf<PostStatus?>(null) }
  var selectedPlatformFilter by remember { mutableStateOf<SocialPlatform?>(null) }
  var selectedPostForDetail by remember { mutableStateOf<PostModel?>(null) }

  val filteredPosts = posts.filter { post ->
    (selectedStatusFilter == null || post.status == selectedStatusFilter) &&
    (selectedPlatformFilter == null || post.platforms.contains(selectedPlatformFilter))
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundCanvas)
  ) {
    // Top Bar
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = SurfaceWhite,
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Lịch Đăng & Hàng Đợi",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Slate900
            )
          )
          Text(
            text = "${posts.size} bài viết trong hệ thống",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Slate500,
              fontSize = 11.5.sp
            )
          )
        }

        Button(
          onClick = { viewModel.setTab(NavigationItem.AI_STUDIO) },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "New Post",
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Tạo Bài",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.5.sp
            )
          )
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
      // 1. Status Filter Pills
      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          item {
            FilterChipItem(
              label = "Tất cả (${posts.size})",
              isSelected = selectedStatusFilter == null,
              onClick = { selectedStatusFilter = null }
            )
          }
          items(PostStatus.entries) { status ->
            val count = posts.count { it.status == status }
            FilterChipItem(
              label = "${status.label} ($count)",
              isSelected = selectedStatusFilter == status,
              onClick = { selectedStatusFilter = status }
            )
          }
        }
      }

      // 2. Timeline Queue Helper Banner
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
            .padding(16.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = Indigo600,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "LỊCH TRÌNH TỐI ƯU HÔM NAY",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Indigo600,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  letterSpacing = 0.5.sp
                )
              )
            }
            Text(
              text = "08:00 → Facebook  |  09:30 → TikTok  |  11:00 → YouTube  |  14:00 → Zalo OA",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Slate700,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
              )
            )
          }
        }
      }

      // 3. Post List
      if (filteredPosts.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.LayersClear,
                contentDescription = null,
                tint = Slate400,
                modifier = Modifier.size(48.dp)
              )
              Text(
                text = "Không có bài viết nào phù hợp bộ lọc",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = Slate500,
                  fontWeight = FontWeight.Medium
                )
              )
            }
          }
        }
      } else {
        items(filteredPosts) { post ->
          PostQueueCard(
            post = post,
            onRetry = { viewModel.retryPost(post.id) },
            onDelete = { viewModel.deletePost(post.id) },
            onClick = { selectedPostForDetail = post }
          )
        }
      }
    }
  }

  // Post Detail Dialog
  if (selectedPostForDetail != null) {
    val post = selectedPostForDetail!!
    AlertDialog(
      onDismissRequest = { selectedPostForDetail = null },
      title = {
        Text(
          text = post.title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Slate900
          )
        )
      },
      text = {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            Text(
              text = "Chiến dịch: ${post.campaignName ?: "Đăng đơn lẻ"}",
              style = MaterialTheme.typography.labelSmall.copy(color = Indigo600, fontWeight = FontWeight.Bold)
            )
          }
          item {
            Text(
              text = "Trạng thái: ${post.status.label} (${post.scheduledTime})",
              style = MaterialTheme.typography.bodySmall.copy(color = post.status.color, fontWeight = FontWeight.SemiBold)
            )
          }
          items(post.platforms) { platform ->
            val content = post.platformContents[platform]
            if (content != null) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(Slate50)
                  .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                  .padding(10.dp)
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(
                    text = "${platform.displayName} Content:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = platform.brandColor)
                  )
                  Text(
                    text = content.body,
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate700, fontSize = 11.5.sp)
                  )
                }
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { selectedPostForDetail = null },
          colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
        ) {
          Text("Đóng")
        }
      },
      shape = RoundedCornerShape(24.dp),
      containerColor = SurfaceWhite
    )
  }
}

@Composable
fun FilterChipItem(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(if (isSelected) Slate900 else SurfaceWhite)
      .border(1.dp, if (isSelected) Slate900 else BorderSubtle, RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 7.dp)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) SurfaceWhite else Slate600,
        fontSize = 11.5.sp
      )
    )
  }
}

@Composable
fun PostQueueCard(
  post: PostModel,
  onRetry: () -> Unit,
  onDelete: () -> Unit,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(SurfaceWhite)
      .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
      .clickable(onClick = onClick)
      .padding(16.dp)
      .testTag("post_queue_card_${post.id}")
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      // Top Row: Platforms & Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          post.platforms.forEach { p ->
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(p.brandColor.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = p.shortLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = p.brandColor,
                  fontSize = 11.sp
                )
              )
            }
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(post.status.bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = post.status.label,
            style = MaterialTheme.typography.labelSmall.copy(
              color = post.status.color,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          )
        }
      }

      // Title & Campaign
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (post.campaignName != null) {
          Text(
            text = post.campaignName.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              color = Indigo600,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp,
              letterSpacing = 0.5.sp
            )
          )
        }
        Text(
          text = post.title,
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = Slate900,
            fontSize = 14.sp
          ),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Error banner if failed
      if (post.errorMessage != null && post.status == PostStatus.FAILED) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Rose50)
            .padding(8.dp)
        ) {
          Text(
            text = "Lỗi: ${post.errorMessage}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Rose500,
              fontSize = 11.sp
            )
          )
        }
      }

      // Footer: Scheduled Time, Metrics, Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = Slate400,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = post.scheduledTime,
            style = MaterialTheme.typography.bodySmall.copy(
              color = Slate500,
              fontSize = 11.sp
            )
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (post.status == PostStatus.FAILED) {
            Button(
              onClick = onRetry,
              colors = ButtonDefaults.buttonColors(containerColor = Rose50, contentColor = Rose500),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(28.dp)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Thử lại", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.Delete,
              contentDescription = "Delete",
              tint = Slate400,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}
