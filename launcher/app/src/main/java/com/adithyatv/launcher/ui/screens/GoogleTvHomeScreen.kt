package com.adithyatv.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adithyatv.launcher.data.AppItem
import com.adithyatv.launcher.data.InputItem
import com.adithyatv.launcher.data.MediaReleaseItem
import com.adithyatv.launcher.data.ReleaseRepository
import com.adithyatv.launcher.ui.components.DpadFocusableCard
import com.adithyatv.launcher.ui.components.GoogleTvAppPill
import com.adithyatv.launcher.ui.components.GoogleTvTab
import com.adithyatv.launcher.ui.components.HeroSpotlightBanner
import com.adithyatv.launcher.ui.components.InputCard
import com.adithyatv.launcher.ui.components.MediaReleaseCard
import com.adithyatv.launcher.ui.theme.DarkBackground
import com.adithyatv.launcher.ui.theme.FocusBorderCyan
import com.adithyatv.launcher.ui.theme.TextMuted
import com.adithyatv.launcher.ui.theme.TextPrimary
import com.adithyatv.launcher.ui.theme.TextSecondary

@Composable
fun GoogleTvHomeScreen(
    favoriteApps: List<AppItem>,
    tvInputs: List<InputItem>,
    releaseRepository: ReleaseRepository,
    onLaunchApp: (AppItem) -> Unit,
    onLaunchPackage: (String) -> Unit,
    onToggleFavorite: (AppItem) -> Unit,
    onSelectInput: (InputItem) -> Unit,
    onNavigateTab: (GoogleTvTab) -> Unit,
    onOpenSystemSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val spotlightReleases = remember { releaseRepository.getSpotlightReleases() }
    var activeSpotlight by remember { mutableStateOf(spotlightReleases.first()) }

    val netflixReleases = remember { releaseRepository.getReleasesBySource("Netflix") }
    val primeReleases = remember { releaseRepository.getReleasesBySource("Prime Video") }
    val hotstarReleases = remember { releaseRepository.getReleasesBySource("Disney+ Hotstar") }
    val jioReleases = remember { releaseRepository.getReleasesBySource("JioCinema") }
    val airtelReleases = remember { releaseRepository.getReleasesBySource("Airtel Xstream") }
    val sonyLivReleases = remember { releaseRepository.getReleasesBySource("Sony LIV") }
    val youtubeReleases = remember { releaseRepository.getReleasesBySource("YouTube") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(DarkBackground)
    ) {
        // 1. Google TV Dynamic Spotlight Hero Banner
        HeroSpotlightBanner(
            release = activeSpotlight,
            onWatchClick = { release ->
                onLaunchPackage(release.appPackage)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Google TV "Your Apps" Row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your apps",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Press Center to open • Long-press to edit",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) {
                items(favoriteApps, key = { it.packageName }) { app ->
                    GoogleTvAppPill(
                        app = app,
                        onClick = { onLaunchApp(app) },
                        onLongClick = { onToggleFavorite(app) }
                    )
                }

                // "All Apps" pill at the end
                item {
                    DpadFocusableCard(
                        onClick = { onNavigateTab(GoogleTvTab.APPS) },
                        shape = RoundedCornerShape(18.dp),
                        focusedBorderWidth = 2.dp,
                        focusedBorderColor = Color.White,
                        contentDescriptionText = "View All Apps"
                    ) { isFocused ->
                        Column(
                            modifier = Modifier
                                .size(72.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = if (isFocused) Color.White else TextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "All Apps",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isFocused) Color.White else TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 3. Top Picks & Trending Row (Across All Favorite Platforms)
        MediaSectionRow(
            title = "Top Picks & Trending Releases",
            releases = spotlightReleases,
            onFocused = { activeSpotlight = it },
            onSelect = { onLaunchPackage(it.appPackage) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Recent Releases on Netflix
        if (netflixReleases.isNotEmpty()) {
            MediaSectionRow(
                title = "Recent Releases on Netflix",
                subtitle = "New seasons, movies & reality shows",
                releases = netflixReleases,
                onFocused = { activeSpotlight = it },
                onSelect = { onLaunchPackage(it.appPackage) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 5. Trending on Prime Video
        if (primeReleases.isNotEmpty()) {
            MediaSectionRow(
                title = "Trending on Prime Video",
                subtitle = "Original series, drama & blockbusters",
                releases = primeReleases,
                onFocused = { activeSpotlight = it },
                onSelect = { onLaunchPackage(it.appPackage) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 6. New on Disney+ Hotstar
        if (hotstarReleases.isNotEmpty()) {
            MediaSectionRow(
                title = "New on Disney+ Hotstar",
                subtitle = "Blockbuster premieres, IMAX Enhanced & series",
                releases = hotstarReleases,
                onFocused = { activeSpotlight = it },
                onSelect = { onLaunchPackage(it.appPackage) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 7. Trending on JioCinema
        if (jioReleases.isNotEmpty()) {
            MediaSectionRow(
                title = "Trending on JioCinema",
                subtitle = "Original thrillers, live cricket & reality series",
                releases = jioReleases,
                onFocused = { activeSpotlight = it },
                onSelect = { onLaunchPackage(it.appPackage) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 8. Popular on Airtel Xstream & Sony LIV
        val regionalAndLive = airtelReleases + sonyLivReleases
        if (regionalAndLive.isNotEmpty()) {
            MediaSectionRow(
                title = "Airtel Xstream & Sony LIV",
                subtitle = "Live sports, regional cinema & hit originals",
                releases = regionalAndLive,
                onFocused = { activeSpotlight = it },
                onSelect = { onLaunchPackage(it.appPackage) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 9. Trending on YouTube
        if (youtubeReleases.isNotEmpty()) {
            MediaSectionRow(
                title = "Featured on YouTube",
                subtitle = "Cinematic 4K nature, documentaries & tech",
                releases = youtubeReleases,
                onFocused = { activeSpotlight = it },
                onSelect = { onLaunchPackage(it.appPackage) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 9. TV Inputs Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
        ) {
            Text(
                text = "TV Video Inputs & Sources",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(tvInputs, key = { it.id }) { input ->
                    InputCard(
                        input = input,
                        onClick = { onSelectInput(input) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 10. Native System Settings Shortcut Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
        ) {
            DpadFocusableCard(
                onClick = onOpenSystemSettings,
                shape = RoundedCornerShape(12.dp),
                focusedBorderColor = FocusBorderCyan,
                modifier = Modifier.height(72.dp)
            ) { isFocused ->
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = if (isFocused) FocusBorderCyan else TextSecondary
                    )
                    Text(
                        text = "Open Native Android TV System Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isFocused) TextPrimary else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun MediaSectionRow(
    title: String,
    releases: List<MediaReleaseItem>,
    onFocused: (MediaReleaseItem) -> Unit,
    onSelect: (MediaReleaseItem) -> Unit,
    subtitle: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = TextPrimary
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
        ) {
            items(releases, key = { it.id }) { release ->
                MediaReleaseCard(
                    release = release,
                    onClick = { onSelect(release) },
                    onFocused = onFocused
                )
            }
        }
    }
}
