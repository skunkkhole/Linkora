package com.sakethh.linkora.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.LinksTable
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeScreenVM : ViewModel() {
    val currentPhaseOfTheDay = mutableStateOf("")

    private val _linksData = MutableStateFlow(emptyList<LinksTable>())
    val recentlySavedLinksData = _linksData.asStateFlow()

    private val _impLinksData = MutableStateFlow(emptyList<ImportantLinks>())
    val recentlySavedImpLinksData = _impLinksData.asStateFlow()

    private val _historyLinksData = MutableStateFlow(emptyList<RecentlyVisited>())
    val historyLinksData = _historyLinksData.asStateFlow()

    companion object {
        val tempImpLinkData = ImportantLinks(
            title = "",
            webURL = "",
            baseURL = "",
            imgURL = "",
            infoForSaving = ""
        )
    }

    init {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..3 -> {
                currentPhaseOfTheDay.value = "Didn't slept?"
            }

            in 4..11 -> {
                currentPhaseOfTheDay.value = "Good Morning"
            }

            in 12..15 -> {
                currentPhaseOfTheDay.value = "Good Afternoon"
            }

            in 16..22 -> {
                currentPhaseOfTheDay.value = "Good Evening"
            }

            in 23 downTo 0 -> {
                currentPhaseOfTheDay.value = "Good Night?"
            }

            else -> {
                currentPhaseOfTheDay.value = "Hey, hi\uD83D\uDC4B"
            }
        }

        viewModelScope.launch {
            CustomLocalDBDaoFunctionsDecl.localDB.crudDao().getLatestImportantLinks().collect {
                _impLinksData.emit(it.reversed())
            }
        }

        viewModelScope.launch {
            CustomLocalDBDaoFunctionsDecl.localDB.crudDao().getLatestSavedLinks().collect {
                _linksData.emit(it.reversed())
            }
        }
        changeHistoryRetrievedData(
            sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                SettingsScreenVM.Settings.selectedSortingType.value
            )
        )
    }

    fun changeHistoryRetrievedData(sortingPreferences: SettingsScreenVM.SortingPreferences) {
        when (sortingPreferences) {
            SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                viewModelScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.historyLinksSorting().sortByAToZ()
                        .collect {
                            _historyLinksData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.historyLinksSorting().sortByZToA()
                        .collect {
                            _historyLinksData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.historyLinksSorting()
                        .sortByLatestToOldest()
                        .collect {
                            _historyLinksData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.historyLinksSorting()
                        .sortByOldestToLatest()
                        .collect {
                            _historyLinksData.emit(it)
                        }
                }
            }
        }
    }
}