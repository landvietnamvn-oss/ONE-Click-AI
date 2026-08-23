package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.AccountStatus
import com.example.data.model.SocialAccount
import com.example.data.model.SocialPlatform
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SocialAccountsScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val accounts by viewModel.accounts.collectAsState()
  var showAddAccountDialog by remember { mutableStateOf(false) }

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
            text = "Tài Khoản Mạng Xã Hội",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Slate900
            )
          )
          Text(
            text = "${accounts.count { it.status == AccountStatus.CONNECTED }} kênh đang kết nối trực tiếp",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Slate500,
              fontSize = 11.5.sp
            )
          )
        }

        Button(
          onClick = { showAddAccountDialog = true },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
          modifier = Modifier.testTag("btn_add_account")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Kết Nối Kênh", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
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
      // Security Notice Card
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Indigo50)
            .border(1.dp, Indigo100, RoundedCornerShape(20.dp))
            .padding(16.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = Indigo600,
              modifier = Modifier.size(24.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = "BẢO MẬT CHUẨN OAUTH 2.0 & ENCRYPTION",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Indigo700,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  letterSpacing = 0.5.sp
                )
              )
              Text(
                text = "Token truy cập được mã hóa an toàn. Ứng dụng không lưu mật khẩu cá nhân của bạn.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Slate700,
                  fontSize = 11.5.sp
                )
              )
            }
          }
        }
      }

      // Account List Cards
      items(accounts) { acc ->
        AccountItemCard(
          account = acc,
          onRefreshToken = { viewModel.refreshAccountToken(acc.id) },
          onToggleConnection = { viewModel.toggleAccountConnection(acc.id) }
        )
      }
    }
  }

  // Add Account Dialog
  if (showAddAccountDialog) {
    AddAccountModal(
      onDismiss = { showAddAccountDialog = false },
      onConnect = { platform, name, handle ->
        viewModel.addSocialAccount(platform, name, handle)
        showAddAccountDialog = false
      }
    )
  }
}

@Composable
fun AccountItemCard(
  account: SocialAccount,
  onRefreshToken: () -> Unit,
  onToggleConnection: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(SurfaceWhite)
      .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
      .padding(18.dp)
      .testTag("account_card_${account.id}")
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      // Header: Avatar, Name, Platform Badge, Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(account.platform.brandColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = account.platform.shortLabel,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = account.platform.brandColor,
                fontSize = 18.sp
              )
            )
          }

          Column {
            Text(
              text = account.accountName,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Slate900,
                fontSize = 14.sp
              )
            )
            Text(
              text = account.accountHandle,
              style = MaterialTheme.typography.bodySmall.copy(
                color = Slate500,
                fontSize = 11.5.sp
              )
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(account.status.badgeBg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = account.status.label,
            style = MaterialTheme.typography.labelSmall.copy(
              color = account.status.badgeColor,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          )
        }
      }

      // Stats: Followers, Posts, Token Validity
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(Slate50)
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Người theo dõi",
            style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp)
          )
          Text(
            text = account.followersCount,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
          )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Bài đã đăng",
            style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp)
          )
          Text(
            text = "${account.postsCount} bài",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
          )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Hạn Token",
            style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp)
          )
          Text(
            text = if (account.status == AccountStatus.CONNECTED) "${account.tokenExpiresDays} ngày" else "Hết hạn",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = if (account.tokenExpiresDays < 7) Amber500 else Emerald600
            )
          )
        }
      }

      // Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onToggleConnection,
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (account.status == AccountStatus.DISCONNECTED) Emerald600 else Slate600
          )
        ) {
          Text(
            text = if (account.status == AccountStatus.DISCONNECTED) "Kết nối lại" else "Ngắt kết nối",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
          onClick = onRefreshToken,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Làm mới Token", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun AddAccountModal(
  onDismiss: () -> Unit,
  onConnect: (SocialPlatform, String, String) -> Unit
) {
  var selectedPlatform by remember { mutableStateOf(SocialPlatform.FACEBOOK) }
  var accountName by remember { mutableStateOf("") }
  var accountHandle by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Kết Nối Kênh Mạng Xã Hội Mới",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "Chọn nền tảng cần ủy quyền đăng bài:",
          style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(SocialPlatform.entries) { p ->
            val isSelected = selectedPlatform == p
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) p.brandColor.copy(alpha = 0.15f) else Slate50)
                .border(1.5.dp, if (isSelected) p.brandColor else BorderSubtle, RoundedCornerShape(12.dp))
                .clickable { selectedPlatform = p }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Text(
                text = "${p.shortLabel} ${p.displayName}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) p.brandColor else Slate700
                )
              )
            }
          }
        }

        OutlinedTextField(
          value = accountName,
          onValueChange = { accountName = it },
          label = { Text("Tên Kênh / Fanpage / Trang") },
          placeholder = { Text("Ví dụ: Bất Động Sản Miền Trung") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = accountHandle,
          onValueChange = { accountHandle = it },
          label = { Text("Handle / ID Kênh") },
          placeholder = { Text("@kenhcuaban") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (accountName.isNotBlank()) {
            onConnect(selectedPlatform, accountName, if (accountHandle.isNotBlank()) accountHandle else "@${accountName.lowercase().replace(" ", "")}")
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
      ) {
        Text("Ủy Quyền & Kết Nối")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Hủy")
      }
    },
    shape = RoundedCornerShape(24.dp),
    containerColor = SurfaceWhite
  )
}
