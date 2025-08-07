package com.aritradas.medai.ui.presentation.profile

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aritradas.medai.BuildConfig
import com.aritradas.medai.R
import com.aritradas.medai.navigation.Screens
import com.aritradas.medai.ui.presentation.profile.components.SettingsCard
import com.aritradas.medai.ui.presentation.profile.components.SettingsItemGroup
import com.aritradas.medai.utils.Constants
import com.aritradas.medai.utils.UtilsKt.getInitials
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current
    val context = LocalContext.current
    val userData by viewModel.userData.collectAsState()
    var backPressedState by remember { mutableStateOf(false) }

    BackHandler {
        if (backPressedState) {
            activity?.finish()
        } else {
            backPressedState = true
            Toast.makeText(
                context,
                context.getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT
            ).show()

            scope.launch {
                delay(2.seconds)
                backPressedState = false
            }
        }
    }

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

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(color = MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = userData?.username?.let { getInitials(it) } ?: ""
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
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


            SettingsCard(
                isFirstItem = true,
                itemName = "Settings",
                iconVector = Icons.Outlined.Settings,
                onClick = {
                    navController.navigate(Screens.Settings.route)
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            SettingsCard(
                isLastItem = true,
                itemName = "Help",
                itemSubText = "Get help using MedAI",
                iconVector = Icons.AutoMirrored.Outlined.Help,
                onClick = {
                    navController.navigate(Screens.Help.route)
                }
            )


            Spacer(modifier = Modifier.height(30.dp))


            SettingsCard(
                isFirstItem = true,
                itemName = "Send Love",
                itemSubText = "Rate MedAI on the Play Store",
                iconVector = Icons.Outlined.RateReview,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Constants.PLAY_STORE_URL.toUri()
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            SettingsCard(
                isLastItem = true,
                itemName = "Invite Friends",
                itemSubText = "Like MedAI? Share with friends!",
                iconVector = Icons.Outlined.Share,
                onClick = {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, Constants.INVITE)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            )


            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Version: ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                modifier = Modifier.padding(bottom = 10.dp),
                text = "Build with 💜 for people",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
