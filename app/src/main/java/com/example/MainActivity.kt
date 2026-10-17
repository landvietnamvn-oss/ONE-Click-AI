package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationItem

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppRoot()
      }
    }
  }
}

@Composable
fun MainAppRoot(
  viewModel: MainViewModel = viewModel()
) {
  val currentTab by viewModel.currentTab.collectAsState()
  val snackbarMessage by viewModel.snackbarMessage.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let { msg ->
      snackbarHostState.showSnackbar(
        message = msg,
        duration = SnackbarDuration.Short
      )
      viewModel.clearSnackbar()
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(BackgroundCanvas),
    snackbarHost = {
      SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
          .padding(bottom = 90.dp)
          .navigationBarsPadding()
      ) { data ->
        Snackbar(
          snackbarData = data,
          containerColor = Slate900,
          contentColor = SurfaceWhite,
          shape = RoundedCornerShape(14.dp)
        )
      }
    },
    bottomBar = {
      CleanMinimalBottomNav(
        currentTab = currentTab,
        onTabSelected = { viewModel.setTab(it) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(
          top = innerPadding.calculateTopPadding()
        )
    ) {
      AnimatedContent(
        targetState = currentTab,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "TabTransition"
      ) { targetTab ->
        when (targetTab) {
          NavigationItem.DASHBOARD -> DashboardScreen(viewModel = viewModel)
          NavigationItem.AI_STUDIO -> AiStudioScreen(viewModel = viewModel)
          NavigationItem.CONTENT_QUEUE -> ContentQueueScreen(viewModel = viewModel)
          NavigationItem.ACCOUNTS -> SocialAccountsScreen(viewModel = viewModel)
          NavigationItem.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun CleanMinimalBottomNav(
  currentTab: NavigationItem,
  onTabSelected: (NavigationItem) -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
      .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
    color = SurfaceWhite,
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      BottomNavItem(
        item = NavigationItem.DASHBOARD,
        isSelected = currentTab == NavigationItem.DASHBOARD,
        icon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        onClick = { onTabSelected(NavigationItem.DASHBOARD) }
      )

      BottomNavItem(
        item = NavigationItem.AI_STUDIO,
        isSelected = currentTab == NavigationItem.AI_STUDIO,
        icon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome,
        onClick = { onTabSelected(NavigationItem.AI_STUDIO) }
      )

      BottomNavItem(
        item = NavigationItem.CONTENT_QUEUE,
        isSelected = currentTab == NavigationItem.CONTENT_QUEUE,
        icon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        onClick = { onTabSelected(NavigationItem.CONTENT_QUEUE) }
      )

      BottomNavItem(
        item = NavigationItem.ACCOUNTS,
        isSelected = currentTab == NavigationItem.ACCOUNTS,
        icon = Icons.Filled.Groups,
        unselectedIcon = Icons.Outlined.Groups,
        onClick = { onTabSelected(NavigationItem.ACCOUNTS) }
      )

      BottomNavItem(
        item = NavigationItem.ANALYTICS,
        isSelected = currentTab == NavigationItem.ANALYTICS,
        icon = Icons.Filled.Insights,
        unselectedIcon = Icons.Outlined.Insights,
        onClick = { onTabSelected(NavigationItem.ANALYTICS) }
      )
    }
  }
}

@Composable
fun BottomNavItem(
  item: NavigationItem,
  isSelected: Boolean,
  icon: ImageVector,
  unselectedIcon: ImageVector,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 4.dp)
      .testTag("nav_tab_${item.name.lowercase()}")
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .background(if (isSelected) Indigo50 else Color.Transparent)
        .padding(horizontal = 12.dp, vertical = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (isSelected) icon else unselectedIcon,
        contentDescription = item.label,
        tint = if (isSelected) Indigo600 else Slate400,
        modifier = Modifier.size(22.dp)
      )
    }

    Text(
      text = item.label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) Indigo600 else Slate500,
        fontSize = 9.5.sp
      )
    )
  }
}
