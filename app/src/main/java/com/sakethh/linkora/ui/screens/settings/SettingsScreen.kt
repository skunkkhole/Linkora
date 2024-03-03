package com.sakethh.linkora.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.SettingsInputSvideo
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.settings.composables.SettingsSectionComposable
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.SettingsSections

@OptIn(
    ExperimentalMaterial3Api::class
)
@PreviewLightDark
@Composable
fun SettingsScreen(navController: NavController = rememberNavController()) {
    val settingsScreenVM: SettingsScreenVM = viewModel()
    val topAppBarScrollState = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LinkoraTheme {
        Scaffold(topBar = {
            Column {
                LargeTopAppBar(scrollBehavior = topAppBarScrollState, title = {
                    Text(
                        text = "Settings",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = if (topAppBarScrollState.state.collapsedFraction > 0.6f) 24.sp else 32.sp
                    )
                })
            }
        }) { it ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .nestedScroll(topAppBarScrollState.nestedScrollConnection)
            ) {
                item(key = "settingsCard") {
                    Card(
                        border = BorderStroke(
                            1.dp, contentColorFor(MaterialTheme.colorScheme.surface)
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surface)
                        ),
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .animateContentSize()
                    ) {
                        Row {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null
                                )
                            }
                            Text(
                                text = "Linkora",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(top = 15.dp, bottom = 15.dp)
                                    .alignByBaseline()
                            )
                            Text(
                                text = SettingsScreenVM.APP_VERSION_NAME,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }
                }
                item(key = "themeRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.THEME
                            navController.navigate(NavigationRoutes.SPECIFIC_SETTINGS_SECTION_SCREEN.name)
                        },
                        sectionTitle = "Theme",
                        sectionIcon = Icons.Default.ColorLens
                    )
                }
                item(key = "generalRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.GENERAL
                            navController.navigate(NavigationRoutes.SPECIFIC_SETTINGS_SECTION_SCREEN.name)
                        },
                        sectionTitle = "General",
                        sectionIcon = Icons.Default.SettingsInputSvideo
                    )
                }
                item(key = "dataRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.DATA
                            navController.navigate(NavigationRoutes.SPECIFIC_SETTINGS_SECTION_SCREEN.name)
                        },
                        sectionTitle = "Data",
                        sectionIcon = Icons.Default.Storage
                    )
                }
                item(key = "privacyRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.PRIVACY
                            navController.navigate(NavigationRoutes.SPECIFIC_SETTINGS_SECTION_SCREEN.name)
                        },
                        sectionTitle = "Privacy",
                        sectionIcon = Icons.Default.PrivacyTip
                    )
                }
                item(key = "aboutRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.ABOUT
                            navController.navigate(NavigationRoutes.SPECIFIC_SETTINGS_SECTION_SCREEN.name)
                        },
                        sectionTitle = "About",
                        sectionIcon = Icons.Default.Info
                    )
                }
                item(key = "acknowledgmentsRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.ACKNOWLEDGMENT
                            navController.navigate(NavigationRoutes.SPECIFIC_SETTINGS_SECTION_SCREEN.name)
                        },
                        sectionTitle = "Acknowledgments",
                        sectionIcon = Icons.Default.Group
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    BackHandler {
        if (SettingsScreenVM.Settings.isHomeScreenEnabled.value) {
            navController.navigate(NavigationRoutes.HOME_SCREEN.name) {
                popUpTo(0)
            }
        } else {
            navController.navigate(NavigationRoutes.COLLECTIONS_SCREEN.name) {
                popUpTo(0)
            }
        }
    }
}
}