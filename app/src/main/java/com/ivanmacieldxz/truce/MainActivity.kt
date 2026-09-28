package com.ivanmacieldxz.truce

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ivanmacieldxz.truce.services.NotificationHelper
import com.ivanmacieldxz.truce.theme.TruceTheme
import com.ivanmacieldxz.truce.ui.main.MainTab

import dagger.hilt.android.AndroidEntryPoint

import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  @Inject
  lateinit var supabaseClient: SupabaseClient

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    NotificationHelper.createNotificationChannels(this)
    supabaseClient.handleDeeplinks(intent)
    handleNotificationIntent(intent)
    viewModel.syncFcmToken()

    enableEdgeToEdge()
    setContent {
      val isLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
      var lastKnownLoginState by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf<Boolean?>(null) }
      
      if (isLoggedIn != null) {
          lastKnownLoginState = isLoggedIn
      }
      
      TruceTheme { 
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { 
            if (lastKnownLoginState != null) {
                MainNavigation(isLoggedIn = lastKnownLoginState!!, mainViewModel = viewModel) 
            }
        } 
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    supabaseClient.handleDeeplinks(intent)
    handleNotificationIntent(intent)
  }

  private fun handleNotificationIntent(intent: Intent?) {
    val navTab = intent?.getStringExtra(NotificationHelper.EXTRA_NAV_TAB)
    if (navTab.equals("inbox", ignoreCase = true)) {
      viewModel.setTargetTab(MainTab.Inbox)
    }
  }
}
