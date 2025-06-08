package com.aritradas.medai.ui.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoDelete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aritradas.medai.navigation.Screens
import com.aritradas.medai.ui.presentation.profile.components.SettingsCard
import com.aritradas.medai.ui.presentation.profile.components.SettingsItemGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val onLogOutComplete by settingsViewModel.onLogOutComplete.observeAsState(false)
    val onDeleteAccountComplete by settingsViewModel.onDeleteAccountComplete.observeAsState(false)
    var openLogoutDialog by remember { mutableStateOf(false) }
    var openDeleteAccountDialog by remember { mutableStateOf(false) }

    if (onLogOutComplete || onDeleteAccountComplete) {
        navController.navigate(Screens.Login.route)
    }

    when {
        openLogoutDialog -> {
            AlertDialog(
                onDismissRequest = { openLogoutDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                    )
                },
                title = {
                    Text(
                        text = "Logout",
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to logout?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            settingsViewModel.logout()
                            openLogoutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            "Logout",
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openLogoutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            text = "Cancel",
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            )
        }

        openDeleteAccountDialog -> {
            AlertDialog(
                onDismissRequest = { openDeleteAccountDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.AutoDelete,
                        contentDescription = null
                    )
                },
                title = {
                    Text(
                        text = "Delete Account"
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete your account? This action is irreversible."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            settingsViewModel.deleteAccount()
                            openDeleteAccountDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            "Delete",
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDeleteAccountDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            text = "Cancel"
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            )
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text("Settings") },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
        ) {
//            Text(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                text = stringResource(id = R.string.security)
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            SettingsItemGroup {
//
//                SwitchCard(
//                    itemName = stringResource(R.string.biometric_unlock),
//                    itemSubText = stringResource(R.string.use_biometric_to_unlock_the_app),
//                    isChecked = biometricAuthState,
//                    onChecked = {
//                        settingsViewModel.showBiometricPrompt(context as MainActivity)
//                    }
//                )
//            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 18.dp, top = 18.dp),
                text = "Danger Zone",
            )

            Spacer(modifier = Modifier.height(14.dp))

            SettingsItemGroup {
                SettingsCard(
                    itemName = "Logout",
                    onClick = {
                        openLogoutDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SettingsItemGroup {
                SettingsCard(
                    itemName = "Delete Account",
                    onClick = {
                        openDeleteAccountDialog = true
                    }
                )
            }
        }
    }
}