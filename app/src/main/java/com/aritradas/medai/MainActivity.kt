package com.aritradas.medai

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.aritradas.medai.navigation.Navigation
import com.aritradas.medai.ui.presentation.splash.SplashViewModel
import com.aritradas.medai.ui.theme.MedAITheme
import com.aritradas.medai.utils.AppBioMetricManager
import com.aritradas.medai.utils.Constants.UPDATE_REQUEST_CODE
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var appBioMetricManager: AppBioMetricManager

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        splashViewModel = ViewModelProvider(this)[SplashViewModel::class.java]

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.isLoading.value
        }

        enableEdgeToEdge()
        setContent {
            LaunchedEffect(Unit) {
                checkForAppUpdate()
            }

            MedAITheme {
                Navigation(splashViewModel = splashViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkForStuckUpdate()
    }

    private fun checkForAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val updateAvailability = appUpdateInfo.updateAvailability()

            if (updateAvailability == UpdateAvailability.UPDATE_AVAILABLE) {
                val isImmediateUpdateAllowed =
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

                if (isImmediateUpdateAllowed) {
                    Timber.d("Starting immediate update")
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            UPDATE_REQUEST_CODE
                        )
                    } catch (e: Exception) {
                        Timber.e(e, "Error starting immediate update")
                    }
                } else {
                    Timber.d("Immediate update not allowed")
                }
            } else {
                Timber.d("No update available")
            }
        }.addOnFailureListener { exception ->
            Timber.e(exception, "Error checking for update")
        }
    }

    private fun checkForStuckUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                Timber.d("Resuming stuck immediate update")
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        UPDATE_REQUEST_CODE
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Error resuming update")
                }
            }
        }
    }
}
