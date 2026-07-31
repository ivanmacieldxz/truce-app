package com.ivanmacieldxz.truce.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
            NavigationBar {
                MainTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(text = tab.title) }
                    )
                }
            }
        }
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
