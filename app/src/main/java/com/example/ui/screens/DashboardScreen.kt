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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountStatus
import com.example.data.model.PostModel
import com.example.data.model.SocialPlatform
import com.example.ui.components.AppTopHeader
import com.example.ui.components.HeroStudioBanner
import com.example.ui.components.MetricKpiCard
import com.example.ui.components.PlatformIconBadge
import com.example.ui.components.SystemAlertCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationItem

@Composable
fun DashboardScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val accounts by viewModel.accounts.collectAsState()
  val posts by viewModel.posts.collectAsState()
  val campaigns by viewModel.campaigns.collectAsState()

  val totalReach = "24.8k"
  val scheduledCount = posts.count { it.status == com.example.data.model.PostStatus.SCHEDULED }
  val publishedCount = posts.count { it.status == com.example.data.model.PostStatus.PUBLISHED }
  val failedPost = posts.firstOrNull { it.status == com.example.data.model.PostStatus.FAILED }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundCanvas)
  ) {
    AppTopHeader(
      userAvatarSeed = "Felix",
      onProfileClick = { viewModel.setTab(NavigationItem.ACCOUNTS) }
    )

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
      contentPadding = PaddingValues(top = 18.dp, bottom = 100.dp)
    ) {
      // 1. KPI Metric Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          MetricKpiCard(
            title = "Tổng Tiếp Cận",
            value = totalReach,
            badgeText = "+12%",
            badgeBg = Emerald50,
            badgeColor = Emerald600,
            modifier = Modifier.weight(1f)
          )
          MetricKpiCard(
            title = "Đã Lên Lịch",
            value = scheduledCount.toString(),
            badgeText = "Hôm nay",
            badgeBg = Indigo50,
            badgeColor = Indigo600,
            modifier = Modifier.weight(1f)
          )
        }
      }

      // 2. Hero Action Banner
      item {
        HeroStudioBanner(
          onStartStudio = { viewModel.setTab(NavigationItem.AI_STUDIO) }
        )
      }

      // 3. Connected Channels Strip
      item {
        Column(
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Kênh Đã Kết Nối",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Slate800,
                fontSize = 15.sp
              )
            )
            Text(
              text = "QUẢN LÝ",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Indigo600,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
              ),
              modifier = Modifier
                .clickable { viewModel.setTab(NavigationItem.ACCOUNTS) }
                .testTag("manage_accounts_link")
            )
          }

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
          ) {
            items(accounts) { acc ->
              PlatformIconBadge(
                platform = acc.platform,
                size = 54,
                hasAlert = acc.status == AccountStatus.EXPIRED_SOON || acc.status == AccountStatus.RECONNECT_REQUIRED,
                onClick = { viewModel.setTab(NavigationItem.ACCOUNTS) }
              )
            }

            item {
              // Add Account Card
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable { viewModel.setTab(NavigationItem.ACCOUNTS) }
              ) {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Slate100)
                    .border(1.dp, Slate300, RoundedCornerShape(16.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Channel",
                    tint = Slate500,
                    modifier = Modifier.size(24.dp)
                  )
                }
                Text(
                  text = "Thêm",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate400
                  )
                )
              }
            }
          }
        }
      }

      // 4. System Alert (if any account requires token refresh)
      item {
        val expiredAccount = accounts.firstOrNull { it.status == AccountStatus.EXPIRED_SOON || it.status == AccountStatus.RECONNECT_REQUIRED }
        if (expiredAccount != null) {
          SystemAlertCard(
            message = "Token ${expiredAccount.platform.displayName} hết hạn trong ${expiredAccount.tokenExpiresDays} ngày. Vui lòng làm mới để tránh gián đoạn đăng tin.",
            actionText = "LÀM MỚI",
            onAction = { viewModel.refreshAccountToken(expiredAccount.id) }
          )
        }
      }

      // 5. Active Campaign Overview
      item {
        val activeCampaign = campaigns.firstOrNull()
        if (activeCampaign != null) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(24.dp))
              .background(SurfaceWhite)
              .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
              .padding(18.dp)
          ) {
            Column(
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(Emerald500)
                  )
                  Text(
                    text = "CHIẾN DỊCH ĐANG CHẠY",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Slate500,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp,
                      letterSpacing = 0.5.sp
                    )
                  )
                }

                Text(
                  text = "${activeCampaign.completedPosts}/${activeCampaign.totalPosts} bài",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Indigo600
                  )
                )
              }

              Text(
                text = activeCampaign.name,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Slate900
                )
              )

              Text(
                text = activeCampaign.target,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Slate600,
                  fontSize = 12.sp
                )
              )

              // Progress Bar
              val progress = activeCampaign.completedPosts.toFloat() / activeCampaign.totalPosts.coerceAtLeast(1)
              LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = Indigo600,
                trackColor = Indigo50
              )
            }
          }
        }
      }

      // 6. Recent Posts & Publishing Queue Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Hàng Đợi & Bài Gần Đây",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Slate800,
              fontSize = 15.sp
            )
          )
          Text(
            text = "XEM TẤT CẢ",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Indigo600,
              fontSize = 11.sp,
              letterSpacing = 0.5.sp
            ),
            modifier = Modifier
              .clickable { viewModel.setTab(NavigationItem.CONTENT_QUEUE) }
              .testTag("view_all_queue_link")
          )
        }
      }

      // 7. Post Items List
      items(posts.take(3)) { post ->
        PostDashboardCard(
          post = post,
          onRetry = { viewModel.retryPost(post.id) },
          onClick = { viewModel.setTab(NavigationItem.CONTENT_QUEUE) }
        )
      }
    }
  }
}

@Composable
fun PostDashboardCard(
  post: PostModel,
  onRetry: () -> Unit,
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
      .testTag("post_dashboard_card_${post.id}")
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Platform Chips
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          post.platforms.forEach { platform ->
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(platform.brandColor.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = platform.shortLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = platform.brandColor,
                  fontSize = 11.sp
                )
              )
            }
          }
        }

        // Status Badge
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

        if (post.status == com.example.data.model.PostStatus.FAILED) {
          Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
              containerColor = Rose50,
              contentColor = Rose500
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Retry",
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Thử lại",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            )
          }
        } else if (post.views > 0) {
          Text(
            text = "👀 ${post.views} lượt xem",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              color = Slate600,
              fontSize = 11.sp
            )
          )
        }
      }
    }
  }
}
