package com.ivanmacieldxz.truce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ivanmacieldxz.truce.theme.TruceTheme

import dagger.hilt.android.AndroidEntryPoint

import androidx.activity.viewModels
import androidx.compose.runtime.getValue
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
    supabaseClient.handleDeeplinks(intent)

    enableEdgeToEdge()
    setContent {
      val isLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
      
      TruceTheme { 
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { 
            if (isLoggedIn != null) {
                MainNavigation(isLoggedIn = isLoggedIn!!) 
            }
        } 
      }
    }
  }
}
