package com.adithyatv.launcher

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.adithyatv.launcher.data.AppItem
import com.adithyatv.launcher.data.AppRepository
import com.adithyatv.launcher.data.InputItem
import com.adithyatv.launcher.data.InputRepository
import com.adithyatv.launcher.data.PreferencesRepository
import com.adithyatv.launcher.data.ReleaseRepository
import com.adithyatv.launcher.ui.components.GoogleTvHeader
import com.adithyatv.launcher.ui.components.GoogleTvTab
import com.adithyatv.launcher.ui.screens.AllAppsScreen
import com.adithyatv.launcher.ui.screens.GoogleTvHomeScreen
import com.adithyatv.launcher.ui.screens.InputsScreen
import com.adithyatv.launcher.ui.screens.SearchScreen
import com.adithyatv.launcher.ui.screens.SettingsScreen
import com.adithyatv.launcher.ui.theme.AdithyaTvTheme
import com.adithyatv.launcher.ui.theme.DarkBackground

class MainActivity : ComponentActivity() {

    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var appRepository: AppRepository
    private lateinit var inputRepository: InputRepository
    private lateinit var releaseRepository: ReleaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesRepository = PreferencesRepository(this)
        appRepository = AppRepository(this, preferencesRepository)
        inputRepository = InputRepository(this)
        releaseRepository = ReleaseRepository()

        setContent {
            var currentTab by remember { mutableStateOf(GoogleTvTab.FOR_YOU) }
            var uiScale by remember { mutableFloatStateOf(preferencesRepository.getUiScale()) }

            var allApps by remember { mutableStateOf(appRepository.getInstalledApps()) }
            var favoriteApps by remember { mutableStateOf(appRepository.getFavoriteApps(allApps)) }
            val tvInputs by remember { mutableStateOf(inputRepository.getTvInputs()) }

            // Safe Back-key handling: Return to "For you" Home screen if on secondary tabs; prevent exit on Home
            BackHandler(enabled = (currentTab != GoogleTvTab.FOR_YOU)) {
                currentTab = GoogleTvTab.FOR_YOU
            }

            fun refreshApps() {
                val updatedAll = appRepository.getInstalledApps()
                allApps = updatedAll
                favoriteApps = appRepository.getFavoriteApps(updatedAll)
            }

            fun toggleFavorite(app: AppItem) {
                val isNowFav = preferencesRepository.toggleFavorite(app.packageName)
                refreshApps()
                val msg = if (isNowFav) "Added ${app.label} to Favorites" else "Removed ${app.label} from Favorites"
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }

            fun launchApp(app: AppItem) {
                val success = appRepository.launchApp(app)
                if (!success) {
                    Toast.makeText(this@MainActivity, "Could not open ${app.label}", Toast.LENGTH_SHORT).show()
                }
            }

            fun launchPackage(packageName: String) {
                val foundApp = allApps.firstOrNull { it.packageName == packageName }
                if (foundApp != null) {
                    launchApp(foundApp)
                } else {
                    val intent = packageManager.getLeanbackLaunchIntentForPackage(packageName)
                        ?: packageManager.getLaunchIntentForPackage(packageName)
                    if (intent != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@MainActivity, "App ($packageName) not found or not installed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            fun selectInput(input: InputItem) {
                val success = inputRepository.switchToInput(input)
                if (!success) {
                    Toast.makeText(this@MainActivity, "Cannot switch to ${input.displayTitle}", Toast.LENGTH_SHORT).show()
                }
            }

            fun openSystemSettings() {
                try {
                    val intent = Intent(Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Settings app not found", Toast.LENGTH_SHORT).show()
                }
            }

            val currentDensity = LocalDensity.current
            val customDensity = remember(currentDensity, uiScale) {
                Density(
                    density = currentDensity.density * uiScale,
                    fontScale = currentDensity.fontScale * uiScale
                )
            }

            CompositionLocalProvider(LocalDensity provides customDensity) {
                AdithyaTvTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkBackground)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Google TV Top Navigation Header
                            GoogleTvHeader(
                                currentTab = currentTab,
                                onTabSelected = { currentTab = it }
                            )

                            // Main Content Area based on Selected Tab
                            when (currentTab) {
                                GoogleTvTab.FOR_YOU -> GoogleTvHomeScreen(
                                    favoriteApps = favoriteApps,
                                    tvInputs = tvInputs,
                                    releaseRepository = releaseRepository,
                                    onLaunchApp = { launchApp(it) },
                                    onLaunchPackage = { launchPackage(it) },
                                    onToggleFavorite = { toggleFavorite(it) },
                                    onSelectInput = { selectInput(it) },
                                    onNavigateTab = { currentTab = it },
                                    onOpenSystemSettings = { openSystemSettings() }
                                )

                                GoogleTvTab.APPS -> AllAppsScreen(
                                    allApps = allApps,
                                    onLaunchApp = { launchApp(it) },
                                    onToggleFavorite = { toggleFavorite(it) }
                                )

                                GoogleTvTab.LIVE, GoogleTvTab.LIBRARY -> InputsScreen(
                                    tvInputs = tvInputs,
                                    onSelectInput = { selectInput(it) }
                                )

                                GoogleTvTab.SEARCH -> SearchScreen(
                                    allApps = allApps,
                                    onLaunchApp = { launchApp(it) },
                                    onToggleFavorite = { toggleFavorite(it) }
                                )

                                GoogleTvTab.SETTINGS -> SettingsScreen(
                                    currentUiScale = uiScale,
                                    onSetUiScale = {
                                        uiScale = it
                                        preferencesRepository.setUiScale(it)
                                    },
                                    onOpenSystemSettings = { openSystemSettings() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
