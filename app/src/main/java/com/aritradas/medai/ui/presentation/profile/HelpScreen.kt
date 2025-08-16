package com.aritradas.medai.ui.presentation.profile

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.aritradas.medai.R
import com.aritradas.medai.ui.presentation.profile.components.SettingsCard
import com.aritradas.medai.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text("Help") },
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
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {

            SettingsCard(
                isFirstItem = true,
                itemName = stringResource(R.string.send_feedback),
                onClick = {
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = context.resources.getString(R.string.mailTo).toUri()
                    context.startActivity(openURL)
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            SettingsCard(
                itemName = stringResource(R.string.terms_conditions),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Constants.TERMS_AND_CONDITIONS.toUri()
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            SettingsCard(
                isLastItem = true,
                itemName = stringResource(R.string.privacy_policy),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Constants.PRIVACY_POLICY.toUri()
                    context.startActivity(intent)
                }
            )
        }
    }
}