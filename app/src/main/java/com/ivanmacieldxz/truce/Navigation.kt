package com.ivanmacieldxz.truce

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ivanmacieldxz.truce.ui.auth.AuthScreen

import com.ivanmacieldxz.truce.ui.main.MainScreen

@Composable
fun MainNavigation(isLoggedIn: Boolean) {
    val startDestination = if (isLoggedIn) Main else Auth
    val backStack = rememberNavBackStack(startDestination)
    
    // We update the backstack root if the login state changes
    if (isLoggedIn && backStack.lastOrNull() == Auth) {
        backStack.clear()
        backStack.add(Main)
    } else if (!isLoggedIn && backStack.lastOrNull() == Main) {
        backStack.clear()
        backStack.add(Auth)
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
        entryProvider {
            entry<Auth> {
                AuthScreen()
            }
            entry<Main> {
                MainScreen()
            }
        },
    )
}
