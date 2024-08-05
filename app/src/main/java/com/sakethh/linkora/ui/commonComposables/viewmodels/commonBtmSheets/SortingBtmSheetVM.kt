package com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.R
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM.Settings.dataStore
import kotlinx.coroutines.launch


class SortingBtmSheetVM : ViewModel() {
    data class SortingBtmSheet(
        val sortingName: Int,
        val onClick: () -> Unit,
        val sortingType: SettingsScreenVM.SortingPreferences,
    )

    val sortingBottomSheetData: (context: Context) -> List<SortingBtmSheet> = {
        listOf(
            SortingBtmSheet(sortingName = R.string.newest_to_oldest, onClick = {
                SettingsScreenVM.Settings.selectedSortingType.value =
                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD.name
                viewModelScope.launch {
                    SettingsScreenVM.Settings.changeSortingPreferenceValue(
                        preferenceKey = stringPreferencesKey(
                            SettingsScreenVM.SettingsPreferences.SORTING_PREFERENCE.name
                        ),
                        dataStore = it.dataStore,
                        newValue = SettingsScreenVM.SortingPreferences.NEW_TO_OLD
                    )
                }
            }, sortingType = SettingsScreenVM.SortingPreferences.NEW_TO_OLD),
            SortingBtmSheet(sortingName = R.string.oldest_to_newest, onClick = {
                SettingsScreenVM.Settings.selectedSortingType.value =
                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW.name
                viewModelScope.launch {
                    SettingsScreenVM.Settings.changeSortingPreferenceValue(
                        preferenceKey = stringPreferencesKey(
                            SettingsScreenVM.SettingsPreferences.SORTING_PREFERENCE.name
                        ),
                        dataStore = it.dataStore,
                        newValue = SettingsScreenVM.SortingPreferences.OLD_TO_NEW
                    )
                }
            }, sortingType = SettingsScreenVM.SortingPreferences.OLD_TO_NEW),
            SortingBtmSheet(sortingName = R.string.a_to_z_sequence, onClick = {
                SettingsScreenVM.Settings.selectedSortingType.value =
                    SettingsScreenVM.SortingPreferences.A_TO_Z.name
                viewModelScope.launch {
                    SettingsScreenVM.Settings.changeSortingPreferenceValue(
                        preferenceKey = stringPreferencesKey(
                            SettingsScreenVM.SettingsPreferences.SORTING_PREFERENCE.name
                        ),
                        dataStore = it.dataStore,
                        newValue = SettingsScreenVM.SortingPreferences.A_TO_Z
                    )
                }
            }, sortingType = SettingsScreenVM.SortingPreferences.A_TO_Z),
            SortingBtmSheet(
                sortingType = SettingsScreenVM.SortingPreferences.Z_TO_A,
                sortingName = R.string.z_to_a_sequence,
                onClick = {
                    SettingsScreenVM.Settings.selectedSortingType.value =
                        SettingsScreenVM.SortingPreferences.Z_TO_A.name
                    viewModelScope.launch {
                        SettingsScreenVM.Settings.changeSortingPreferenceValue(
                            preferenceKey = stringPreferencesKey(
                                SettingsScreenVM.SettingsPreferences.SORTING_PREFERENCE.name
                            ),
                            dataStore = it.dataStore,
                            newValue = SettingsScreenVM.SortingPreferences.Z_TO_A
                        )
                    }
                }),
        )
    }
}