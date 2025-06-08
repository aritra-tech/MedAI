package com.aritradas.medai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.aritradas.medai.navigation.Navigation
import com.aritradas.medai.ui.presentation.login.GoogleAuthUiClient
import com.aritradas.medai.ui.theme.MedAITheme
import com.aritradas.medai.utils.AppBioMetricManager
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var appBioMetricManager: AppBioMetricManager

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            content = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedAITheme {
                Navigation(googleAuthUiClient)
            }
        }
    }
}
