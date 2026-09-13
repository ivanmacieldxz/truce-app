package com.ivanmacieldxz.truce.ui.preferences

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivanmacieldxz.truce.ui.components.TruceButton
import com.ivanmacieldxz.truce.ui.components.TruceOutlinedButton
import com.ivanmacieldxz.truce.ui.components.TruceTextField

@Composable
fun PreferencesScreen(
    viewModel: PreferencesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEditUsernameDialog by remember { mutableStateOf(false) }
    var editUsernameInput by remember { mutableStateOf("") }

    var showEditEmailDialog by remember { mutableStateOf(false) }
    var editEmailInput by remember { mutableStateOf("") }

    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (uiState.isLoadingProfile && uiState.userProfile == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val profile = uiState.userProfile

                    Text(
                        text = "Mi Cuenta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Username item
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Nombre de usuario",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (profile != null) "@${profile.username}" else "Cargando...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        editUsernameInput = profile?.username ?: ""
                                        showEditUsernameDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar nombre de usuario",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )

                            // Email item
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Correo electrónico",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = profile?.email ?: "Cargando...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        editEmailInput = profile?.email ?: ""
                                        showEditEmailDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar correo electrónico",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TruceOutlinedButton(
                        text = "Cerrar sesión",
                        onClick = { viewModel.logout() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showDeleteAccountDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !uiState.isDeleting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        if (uiState.isDeleting) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.height(24.dp)
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = "Eliminar cuenta",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Editar Username
    if (showEditUsernameDialog) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isUpdating) showEditUsernameDialog = false },
            title = { Text("Cambiar nombre de usuario") },
            text = {
                Column {
                    Text(
                        text = "Ingresa tu nuevo nombre de usuario (mínimo 5 caracteres, minúsculas, números, puntos o guiones bajos).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TruceTextField(
                        value = editUsernameInput,
                        onValueChange = { editUsernameInput = it },
                        label = "Nombre de usuario"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateUsername(editUsernameInput) { success ->
                            if (success) showEditUsernameDialog = false
                        }
                    },
                    enabled = !uiState.isUpdating
                ) {
                    if (uiState.isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.height(16.dp))
                    } else {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditUsernameDialog = false },
                    enabled = !uiState.isUpdating
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog: Editar Email
    if (showEditEmailDialog) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isUpdating) showEditEmailDialog = false },
            title = { Text("Cambiar correo electrónico") },
            text = {
                Column {
                    Text(
                        text = "Ingresa tu nuevo correo electrónico. Se actualizará en tu perfil y proveedor de inicio de sesión.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TruceTextField(
                        value = editEmailInput,
                        onValueChange = { editEmailInput = it },
                        label = "Correo electrónico"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateEmail(editEmailInput) { success ->
                            if (success) showEditEmailDialog = false
                        }
                    },
                    enabled = !uiState.isUpdating
                ) {
                    if (uiState.isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.height(16.dp))
                    } else {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditEmailDialog = false },
                    enabled = !uiState.isUpdating
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog: Eliminar Cuenta
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isDeleting) showDeleteAccountDialog = false },
            title = { Text("Eliminar cuenta definitivamente") },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar tu cuenta? Todos tus datos, estadísticas y relaciones de amistad se borrarán permanentemente. Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        viewModel.deleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Eliminar definitivamente")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
