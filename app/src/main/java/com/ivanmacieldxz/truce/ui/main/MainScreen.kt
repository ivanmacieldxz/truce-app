package com.ivanmacieldxz.truce.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ivanmacieldxz.truce.ui.dashboard.DashboardScreen
import com.ivanmacieldxz.truce.ui.friendships.FriendshipsScreen
import com.ivanmacieldxz.truce.ui.inbox.InboxScreen
import com.ivanmacieldxz.truce.ui.preferences.PreferencesScreen

enum class MainTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Dashboard("Dashboard", Icons.Filled.Home, Icons.Outlined.Home),
    Friendships("Friends", Icons.Filled.People, Icons.Outlined.People),
    Inbox("Inbox", Icons.Filled.Inbox, Icons.Outlined.Inbox),
    Preferences("Prefs", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun MainScreen() {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Dashboard) }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MainTab.values().forEach { tab ->
                            val isSelected = selectedTab == tab
                            IconButton(
                                onClick = { selectedTab = tab },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            ) {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            when (selectedTab) {
                MainTab.Dashboard -> DashboardScreen()
                MainTab.Friendships -> FriendshipsScreen()
                MainTab.Inbox -> InboxScreen()
                MainTab.Preferences -> PreferencesScreen()
            }
        }
    }
}
