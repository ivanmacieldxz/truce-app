package com.ivanmacieldxz.truce.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivanmacieldxz.truce.ui.components.TruceButton
import com.ivanmacieldxz.truce.ui.components.TruceTextField

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        if (uiState.signupStep == SignupStep.CONFIRMATION && !uiState.isLoginMode) {
            ConfirmationScreen(uiState = uiState, onEvent = viewModel::onEvent)
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TRUCE",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (uiState.isLoginMode) "Log in to reclaim your time" else "Join us to disconnect",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(48.dp))

                AnimatedContent(
                    targetState = uiState.isLoginMode,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                    label = "auth_mode_transition"
                ) { isLogin ->
                    if (isLogin) {
                        LoginSection(uiState = uiState, onEvent = viewModel::onEvent)
                    } else {
                        SignupSection(uiState = uiState, onEvent = viewModel::onEvent)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { viewModel.onEvent(AuthEvent.ToggleMode) }) {
                    Text(
                        text = if (uiState.isLoginMode) "Don't have an account? Sign Up" else "Already have an account? Log In",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        
        // Debug Error Section at the bottom
        if (uiState.debugError != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Debug Info: ${uiState.debugError}",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun LoginSection(uiState: AuthUiState, onEvent: (AuthEvent) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TruceTextField(
            value = uiState.email,
            onValueChange = { onEvent(AuthEvent.EmailChanged(it)) },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TruceTextField(
            value = uiState.password,
            onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
            label = "Password",
            visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { onEvent(AuthEvent.TogglePasswordVisibility) }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            }
        )

        ErrorAndSuccessMessages(uiState = uiState)

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            TruceButton(
                text = "Log In",
                onClick = { onEvent(AuthEvent.Submit) }
            )
        }
    }
}

@Composable
fun SignupSection(uiState: AuthUiState, onEvent: (AuthEvent) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when (uiState.signupStep) {
            SignupStep.NAME_EMAIL -> {
                TruceTextField(
                    value = uiState.fullName,
                    onValueChange = { onEvent(AuthEvent.FullNameChanged(it)) },
                    label = "Full Name"
                )
                Spacer(modifier = Modifier.height(16.dp))
                TruceTextField(
                    value = uiState.email,
                    onValueChange = { onEvent(AuthEvent.EmailChanged(it)) },
                    label = "Email",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
            SignupStep.USERNAME -> {
                TruceTextField(
                    value = uiState.username,
                    onValueChange = { onEvent(AuthEvent.UsernameChanged(it)) },
                    label = "Username"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Solo minúsculas, números, puntos y guiones bajos (min. 5 caracteres)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
            SignupStep.PASSWORD -> {
                TruceTextField(
                    value = uiState.password,
                    onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
                    label = "Password",
                    visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { onEvent(AuthEvent.TogglePasswordVisibility) }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                PasswordRequirements(uiState = uiState)
            }
            else -> {}
        }

        ErrorAndSuccessMessages(uiState = uiState)
        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (uiState.signupStep != SignupStep.NAME_EMAIL) {
                    TextButton(onClick = { onEvent(AuthEvent.PreviousStep) }) {
                        Text("Back")
                    }
                } else {
                    Spacer(modifier = Modifier.width(64.dp))
                }

                if (uiState.signupStep == SignupStep.PASSWORD) {
                    TruceButton(
                        text = "Sign Up",
                        onClick = { onEvent(AuthEvent.Submit) },
                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                    )
                } else {
                    TruceButton(
                        text = "Next",
                        onClick = { onEvent(AuthEvent.NextStep) },
                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(64.dp)) // To balance the layout
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        StepIndicators(currentStep = uiState.signupStep)
    }
}

@Composable
fun PasswordRequirements(uiState: AuthUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "La contraseña debe tener:", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        RequirementItem(text = "Mínimo 8 caracteres", isMet = uiState.hasPasswordMinLength)
        RequirementItem(text = "Una letra mayúscula", isMet = uiState.hasPasswordUppercase)
        RequirementItem(text = "Una letra minúscula", isMet = uiState.hasPasswordLowercase)
        RequirementItem(text = "Un número", isMet = uiState.hasPasswordDigit)
    }
}

@Composable
fun RequirementItem(text: String, isMet: Boolean) {
    val color = if (isMet) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun StepIndicators(currentStep: SignupStep) {
    val steps = listOf(SignupStep.NAME_EMAIL, SignupStep.USERNAME, SignupStep.PASSWORD)
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        steps.forEach { step ->
            val isCurrent = step == currentStep
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            )
        }
    }
}

@Composable
fun ErrorAndSuccessMessages(uiState: AuthUiState) {
    if (uiState.successMessage != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = uiState.successMessage,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }

    if (uiState.error != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = uiState.error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ConfirmationScreen(uiState: AuthUiState, onEvent: (AuthEvent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Success",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Revisá tu bandeja de entrada",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Te enviamos un correo a ${uiState.email} para confirmar tu cuenta. Hacé clic en el enlace del correo para continuar.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        ErrorAndSuccessMessages(uiState = uiState)
        
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            TruceButton(
                text = "Ya verifiqué mi correo",
                onClick = { onEvent(AuthEvent.ManualLoginCheck) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { onEvent(AuthEvent.ToggleMode) }) {
                Text(
                    text = "Volver al inicio de sesión",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
