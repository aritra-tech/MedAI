package com.aritradas.medai.ui.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aritradas.medai.BuildConfig
import com.aritradas.medai.navigation.Screens
import com.aritradas.medai.ui.presentation.profile.components.SettingsCard
import com.aritradas.medai.ui.presentation.profile.components.SettingsItemGroup

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userData by viewModel.userData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 42.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(contentAlignment = Alignment.BottomEnd) {
                        userData?.profilePictureUrl?.let { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Profile picture",
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    userData?.username?.let { name ->
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SettingsItemGroup {
                SettingsCard(
                    itemName = "Settings",
                    iconVector = Icons.Outlined.Settings,
                    onClick = {
                        navController.navigate(Screens.Settings.route)
                    }
                )

                HorizontalDivider(
                    thickness = 1.dp,
                )

                SettingsCard(
                    itemName = "Help",
                    itemSubText = "Get help using MedAI",
                    iconVector = Icons.AutoMirrored.Outlined.Help,
                    onClick = {
                        navController.navigate(Screens.Help.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            SettingsItemGroup {
                SettingsCard(
                    itemName = "Send Love",
                    itemSubText = "Rate MedAI on the Play Store",
                    iconVector = Icons.Outlined.RateReview,
                    onClick = {

                    }
                )

                HorizontalDivider(
                    thickness = 1.dp,
                )

                SettingsCard(
                    itemName = "Invite Friends",
                    itemSubText = "Like MedAI? Share with friends!",
                    iconVector = Icons.Outlined.Share,
                    onClick = {

                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Version: ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                modifier = Modifier.padding(bottom = 10.dp),
                text = "Build with 💜 for peoples",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
