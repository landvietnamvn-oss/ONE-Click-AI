package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountStatus
import com.example.data.model.PostStatus
import com.example.data.model.SocialPlatform
import com.example.ui.theme.*

@Composable
fun AppTopHeader(
  userAvatarSeed: String = "Felix",
  onProfileClick: () -> Unit = {}
) {
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
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Indigo600),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = "OneClick AI Logo",
            tint = SurfaceWhite,
            modifier = Modifier.size(24.dp)
          )
        }
        Column {
          Text(
            text = "OneClick AI",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = (-0.5).sp,
              color = Slate900
            )
          )
          Text(
            text = "Đăng tin Đa nền tảng",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Slate500,
              fontSize = 11.sp
            )
          )
        }
      }

      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(Indigo50)
          .border(2.dp, Indigo100, CircleShape)
          .clickable(onClick = onProfileClick)
          .testTag("profile_avatar"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "VN",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Indigo700
          )
        )
      }
    }
  }
}

@Composable
fun MetricKpiCard(
  title: String,
  value: String,
  badgeText: String,
  badgeBg: Color = Emerald50,
  badgeColor: Color = Emerald600,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(SurfaceWhite)
      .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
      .padding(16.dp)
      .height(100.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
          color = Slate500,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp,
          fontSize = 10.sp
        )
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Text(
          text = value,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Slate900,
            fontSize = 24.sp
          )
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(badgeBg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = badgeText,
            style = MaterialTheme.typography.labelSmall.copy(
              color = badgeColor,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          )
        }
      }
    }
  }
}

@Composable
fun HeroStudioBanner(
  onStartStudio: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(32.dp))
      .background(
        Brush.linearGradient(
          colors = listOf(Indigo600, Indigo700)
        )
      )
      .padding(22.dp)
      .testTag("hero_studio_banner")
  ) {
    Column(
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "AI ENGINE 3.5",
            style = MaterialTheme.typography.labelSmall.copy(
              color = SurfaceWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Tạo 1 bài đăng\nAI viết riêng 5 nền tảng",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          color = SurfaceWhite,
          lineHeight = 28.sp
        )
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Tự động tối ưu tiêu đề, kịch bản TikTok, bài Facebook, Zalo & YouTube chỉ với 1 click.",
        style = MaterialTheme.typography.bodySmall.copy(
          color = Indigo100,
          fontSize = 13.sp
        )
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = onStartStudio,
        colors = ButtonDefaults.buttonColors(
          containerColor = SurfaceWhite,
          contentColor = Indigo600
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = Modifier.testTag("btn_start_ai_studio")
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Mở AI Studio Ngay",
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        )
      }
    }
  }
}

@Composable
fun PlatformIconBadge(
  platform: SocialPlatform,
  size: Int = 48,
  hasAlert: Boolean = false,
  onClick: (() -> Unit)? = null
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier = Modifier.then(
      if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    )
  ) {
    Box(
      modifier = Modifier
        .size(size.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(SurfaceWhite)
        .border(1.5.dp, if (hasAlert) Rose500 else BorderSubtle, RoundedCornerShape(16.dp)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = platform.shortLabel,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = platform.brandColor,
          fontSize = (size * 0.42).sp
        )
      )

      // Status indicator dot
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .offset(x = 2.dp, y = (-2).dp)
          .size(10.dp)
          .clip(CircleShape)
          .background(if (hasAlert) Amber500 else Emerald500)
          .border(1.5.dp, SurfaceWhite, CircleShape)
      )
    }

    Text(
      text = platform.displayName,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = Slate600
      ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
fun SystemAlertCard(
  message: String,
  actionText: String = "LÀM MỚI",
  onAction: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(SurfaceWhite)
      .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
      .padding(16.dp)
      .testTag("system_alert_card")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Rose50),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.WarningAmber,
          contentDescription = "Alert",
          tint = Rose500,
          modifier = Modifier.size(22.dp)
        )
      }

      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = "CẢNH BÁO HỆ THỐNG",
          style = MaterialTheme.typography.labelSmall.copy(
            color = Slate500,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp,
            letterSpacing = 0.5.sp
          )
        )
        Text(
          text = message,
          style = MaterialTheme.typography.bodySmall.copy(
            color = Slate800,
            fontWeight = FontWeight.Medium,
            fontSize = 12.5.sp
          ),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      Button(
        onClick = onAction,
        colors = ButtonDefaults.buttonColors(
          containerColor = Slate50,
          contentColor = Slate800
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        modifier = Modifier.testTag("btn_refresh_token")
      ) {
        Text(
          text = actionText,
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        )
      }
    }
  }
}
