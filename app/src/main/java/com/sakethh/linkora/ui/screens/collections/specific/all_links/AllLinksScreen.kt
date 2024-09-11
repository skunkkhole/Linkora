package com.sakethh.linkora.ui.screens.collections.specific.all_links

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.data.local.links.LinkType
import com.sakethh.linkora.ui.bottomSheets.menu.MenuBtmSheetParam
import com.sakethh.linkora.ui.bottomSheets.menu.MenuBtmSheetUI
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.RenameDialogBox
import com.sakethh.linkora.ui.commonComposables.RenameDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.link_views.LinkUIComponentParam
import com.sakethh.linkora.ui.commonComposables.link_views.components.GridViewLinkUIComponent
import com.sakethh.linkora.ui.commonComposables.link_views.components.ListViewLinkUIComponent
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetVM
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.CustomWebTab
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenUIEvent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.home.HomeScreenVM
import com.sakethh.linkora.ui.screens.link_view.LinkView
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun AllLinksScreen(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setNavigationBarColor(
        MaterialTheme.colorScheme.surfaceColorAtElevation(
            BottomAppBarDefaults.ContainerElevation
        )
    )
    val allLinksScreenVM: AllLinksScreenVM = hiltViewModel()

    val savedLinks = allLinksScreenVM.savedLinks.collectAsStateWithLifecycle(emptyList())
    val impLinks = allLinksScreenVM.importantLinks.collectAsStateWithLifecycle(emptyList())
    val historyLinks = allLinksScreenVM.historyLinks.collectAsStateWithLifecycle(emptyList())
    val archivedLinks = allLinksScreenVM.archivedLinks.collectAsStateWithLifecycle(emptyList())
    val regularFoldersLinks =
        allLinksScreenVM.regularFoldersLinks.collectAsStateWithLifecycle(emptyList())

    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldRenameDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val selectedElementID = rememberSaveable {
        mutableLongStateOf(0)
    }
    val selectedURLTitle = rememberSaveable {
        mutableStateOf("")
    }
    val selectedNote = rememberSaveable {
        mutableStateOf("")
    }
    val optionsBtmSheetVM: OptionsBtmSheetVM = hiltViewModel()
    val selectedLinkType = rememberSaveable {
        mutableStateOf("")
    }
    val noLinksSelectedState = allLinksScreenVM.linkTypes.map { it.isChecked.value }.all { !it }
    val customWebTab = hiltViewModel<CustomWebTab>()
    val context = LocalContext.current
    fun modifiedLinkUIComponentParam(
        linksTable: LinksTable,
        linkType: LinkType
    ): LinkUIComponentParam {
        return LinkUIComponentParam(
            onLongClick = {},
            isSelectionModeEnabled = mutableStateOf(false),
            isItemSelected = mutableStateOf(
                false
            ),
            title = linksTable.title,
            webBaseURL = linksTable.baseURL,
            imgURL = linksTable.imgURL,
            onMoreIconClick = {
                selectedLinkType.value = linkType.name
                SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                    OptionsBtmSheetType.LINK
                selectedElementID.longValue = linksTable.id
                HomeScreenVM.tempImpLinkData.baseURL = linksTable.baseURL
                HomeScreenVM.tempImpLinkData.imgURL = linksTable.imgURL
                HomeScreenVM.tempImpLinkData.webURL = linksTable.webURL
                HomeScreenVM.tempImpLinkData.title = linksTable.title
                HomeScreenVM.tempImpLinkData.infoForSaving =
                    linksTable.infoForSaving
                shouldOptionsBtmModalSheetBeVisible.value = true
                selectedWebURL.value = linksTable.webURL
                selectedNote.value = linksTable.infoForSaving
                selectedURLTitle.value = linksTable.title
                coroutineScope.launch {
                    awaitAll(async {
                        optionsBtmSheetVM.updateImportantCardData(
                            url = selectedWebURL.value
                        )
                    }, async {
                        optionsBtmSheetVM.updateArchiveLinkCardData(
                            url = selectedWebURL.value
                        )
                    }
                    )
                }
            },
            onLinkClick = {
                customWebTab.openInWeb(
                    recentlyVisitedData = RecentlyVisited(
                        title = linksTable.title,
                        webURL = linksTable.webURL,
                        baseURL = linksTable.baseURL,
                        imgURL = linksTable.imgURL,
                        infoForSaving = linksTable.infoForSaving
                    ), context = context, uriHandler = uriHandler,
                    forceOpenInExternalBrowser = false
                )
            },
            webURL = linksTable.webURL,
            onForceOpenInExternalBrowserClicked = {
                customWebTab.openInWeb(
                    recentlyVisitedData = RecentlyVisited(
                        title = linksTable.title,
                        webURL = linksTable.webURL,
                        baseURL = linksTable.baseURL,
                        imgURL = linksTable.imgURL,
                        infoForSaving = linksTable.infoForSaving
                    ), context = context, uriHandler = uriHandler,
                    forceOpenInExternalBrowser = true
                )
            })
    }
    SpecificScreenScaffold(topAppBarText = "All Links", navController = navController, actions = {
        if (
            allLinksScreenVM.linkTypes
                .map { it.isChecked.value }.count { it } == 1
        ) {
            IconButton(modifier = Modifier.pulsateEffect(),
                onClick = { /*shouldSortingBottomSheetAppear.value = true*/ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Sort, contentDescription = null
                )
            }
        }
        IconButton(onClick = {
            navController.navigate(NavigationRoutes.LINK_VIEW_SETTINGS.name)
        }) {
            Icon(Icons.AutoMirrored.Filled.ViewQuilt, null)
        }
    }, bottomBar = {
        LinksSelectionChips(allLinksScreenVM)
    }) { paddingValues, topAppBarScrollBehaviour ->
        val commonModifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection)
            .animateContentSize()
        when (SettingsPreference.currentlySelectedLinkView.value) {
            LinkView.REGULAR_LIST_VIEW.name, LinkView.TITLE_ONLY_LIST_VIEW.name -> {
                LazyColumn(
                    modifier = commonModifier
                ) {
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Saved Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(savedLinks.value) {
                            ListViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.SAVED_LINK
                                ),
                                forTitleOnlyView = SettingsPreference.currentlySelectedLinkView.value == LinkView.TITLE_ONLY_LIST_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Important Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(impLinks.value.map {
                            LinksTable(
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "Graduation",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            ListViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.IMP_LINK
                                ),
                                forTitleOnlyView = SettingsPreference.currentlySelectedLinkView.value == LinkView.TITLE_ONLY_LIST_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "History Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(historyLinks.value.map {
                            LinksTable(
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "Illmatic",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            ListViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.HISTORY_LINK
                                ),
                                forTitleOnlyView = SettingsPreference.currentlySelectedLinkView.value == LinkView.TITLE_ONLY_LIST_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Archived Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(archivedLinks.value.map {
                            LinksTable(
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "It Was Written",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            ListViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.ARCHIVE_LINK
                                ),
                                forTitleOnlyView = SettingsPreference.currentlySelectedLinkView.value == LinkView.TITLE_ONLY_LIST_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Folders Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(regularFoldersLinks.value) {
                            if (!it.isLinkedWithArchivedFolder)
                                ListViewLinkUIComponent(
                                    linkUIComponentParam = modifiedLinkUIComponentParam(
                                        linksTable = it,
                                        linkType = LinkType.FOLDER_LINK
                                    ),
                                    forTitleOnlyView = SettingsPreference.currentlySelectedLinkView.value == LinkView.TITLE_ONLY_LIST_VIEW.name
                                )
                        }
                    }
                }
            }

            LinkView.GRID_VIEW.name -> {
                LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), modifier = commonModifier) {

                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Saved Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(savedLinks.value) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.SAVED_LINK
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Important Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(impLinks.value.map {
                            LinksTable(
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.IMP_LINK
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "History Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(historyLinks.value.map {
                            LinksTable(
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.HISTORY_LINK
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Archived Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(archivedLinks.value.map {
                            LinksTable(
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.ARCHIVE_LINK
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Folders Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(regularFoldersLinks.value) {
                            if (!it.isLinkedWithArchivedFolder)
                                GridViewLinkUIComponent(
                                    linkUIComponentParam = modifiedLinkUIComponentParam(
                                        linksTable = it,
                                        linkType = LinkType.FOLDER_LINK
                                    ),
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                                )
                        }
                    }
                }
            }

            LinkView.STAGGERED_VIEW.name -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(150.dp), modifier = commonModifier
                ) {

                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Saved Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(savedLinks.value) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.SAVED_LINK
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Important Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(impLinks.value.map {
                            LinksTable(
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.IMP_LINK
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "History Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(historyLinks.value.map {
                            LinksTable(
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.HISTORY_LINK
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Archived Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(archivedLinks.value.map {
                            LinksTable(
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.ARCHIVE_LINK
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == "Folders Links" }!!.isChecked.value || noLinksSelectedState) {
                        items(regularFoldersLinks.value) {
                            if (!it.isLinkedWithArchivedFolder)
                                GridViewLinkUIComponent(
                                    linkUIComponentParam = modifiedLinkUIComponentParam(
                                        linksTable = it,
                                        linkType = LinkType.FOLDER_LINK
                                    ),
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                                )
                        }
                    }
                }
            }
        }
        MenuBtmSheetUI(
            MenuBtmSheetParam(
                btmModalSheetState = btmModalSheetState,
                shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
                btmSheetFor = SpecificCollectionsScreenVM.selectedBtmSheetType.value,
                onRenameClick = {
                    coroutineScope.launch {
                        btmModalSheetState.hide()
                    }
                    shouldRenameDialogBoxAppear.value = true
                },
                onDeleteCardClick = {
                    shouldDeleteDialogBoxAppear.value = true
                },
                onArchiveClick = {
                    allLinksScreenVM.onUIEvent(
                        SpecificCollectionsScreenUIEvent.ArchiveAnExistingLink(
                            ArchivedLinks(
                                id = selectedElementID.longValue,
                                title = HomeScreenVM.tempImpLinkData.title,
                                webURL = HomeScreenVM.tempImpLinkData.webURL,
                                baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                                imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                                infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                            ), context, LinkType.valueOf(selectedLinkType.value)
                        )
                    )
                },
                noteForSaving = selectedNote.value,
                onNoteDeleteCardClick = {
                    allLinksScreenVM.onUIEvent(
                        SpecificCollectionsScreenUIEvent.DeleteAnExistingNote(
                            selectedElementID.longValue, LinkType.valueOf(selectedLinkType.value)
                        )
                    )
                },
                folderName = selectedURLTitle.value,
                linkTitle = selectedURLTitle.value,
                imgLink = HomeScreenVM.tempImpLinkData.imgURL,
                onRefreshClick = {
                    allLinksScreenVM.onUIEvent(
                        SpecificCollectionsScreenUIEvent.OnLinkRefresh(
                            selectedElementID.longValue, LinkType.valueOf(selectedLinkType.value)
                        )
                    )
                }, onImportantLinkClick = {
                    allLinksScreenVM.onUIEvent(
                        SpecificCollectionsScreenUIEvent.AddExistingLinkToImportantLink(
                            ImportantLinks(
                                title = HomeScreenVM.tempImpLinkData.title,
                                webURL = HomeScreenVM.tempImpLinkData.webURL,
                                baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                                imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                                infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                            )
                        )
                    )
                }
            )
        )
    }
    DeleteDialogBox(
        DeleteDialogBoxParam(
            folderName = selectedURLTitle,
            shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
            deleteDialogBoxType = if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) DataDialogBoxType.LINK else DataDialogBoxType.FOLDER,
            onDeleteClick = {
                allLinksScreenVM.onUIEvent(
                    SpecificCollectionsScreenUIEvent.DeleteAnExistingLink(
                        selectedElementID.longValue, LinkType.valueOf(selectedLinkType.value)
                    )
                )
            })
    )
    RenameDialogBox(
        RenameDialogBoxParam(
            shouldDialogBoxAppear = shouldRenameDialogBoxAppear,
            existingFolderName = selectedURLTitle.value,
            renameDialogBoxFor = SpecificCollectionsScreenVM.selectedBtmSheetType.value,
            onNoteChangeClick = { newNote: String ->
                allLinksScreenVM.onUIEvent(
                    SpecificCollectionsScreenUIEvent.UpdateLinkNote(
                        linkId = selectedElementID.longValue,
                        newNote = newNote,
                        linkType = LinkType.valueOf(selectedLinkType.value)
                    )
                )
            },
            onTitleChangeClick = { newTitle: String ->
                allLinksScreenVM.onUIEvent(
                    SpecificCollectionsScreenUIEvent.UpdateLinkTitle(
                        linkId = selectedElementID.longValue,
                        newTitle = newTitle,
                        linkType = LinkType.valueOf(selectedLinkType.value)
                    )
                )
            }, existingTitle = selectedURLTitle.value, existingNote = selectedNote.value
        )
    )
    }

@Composable
private fun LinksSelectionChips(allLinksScreenVM: AllLinksScreenVM) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(BottomAppBarDefaults.ContainerElevation))
    ) {
        Text(
            "Filter based on",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(start = 15.dp, top = 15.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(15.dp))
            allLinksScreenVM.linkTypes.forEach {
                FilterChip(modifier = Modifier.animateContentSize(), onClick = {
                    it.isChecked.value = !it.isChecked.value
                }, selected = it.isChecked.value, label = {
                    Text(it.linkType, style = MaterialTheme.typography.titleSmall)
                })
                Spacer(Modifier.width(10.dp))
            }
        }
    }
}