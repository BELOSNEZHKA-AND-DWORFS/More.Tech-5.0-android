package com.vtb.hackaton.ui.bottomSheet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtb.hackaton.domain.entity.OfficeFilter
import com.vtb.hackaton.domain.entity.OfficeSnippet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BottomSheetViewModel : ViewModel() {
    val officeSnippetListLiveData: MutableLiveData<MutableList<OfficeSnippet>> = MutableLiveData()
    val officeFiltersLiveData: MutableLiveData<MutableList<OfficeFilter>> = MutableLiveData()

    fun getNearbyOffices() = viewModelScope.launch(Dispatchers.Main) {
        officeSnippetListLiveData.postValue(
            mutableListOf(
                OfficeSnippet(isAvailable = true, workingTime = "12:00-21:00", distance = "10 km"),
                OfficeSnippet(isAvailable = true, workingTime = "10:00-22:00", distance = "2 km"),
                OfficeSnippet(isAvailable = true, workingTime = "12:00-21:00", distance = "500 m"),
                OfficeSnippet(isAvailable = true, workingTime = "10:00-22:00", distance = "5 m"),
                OfficeSnippet(isAvailable = true, workingTime = "12:00-21:00", distance = "15 km"),
                OfficeSnippet(isAvailable = true, workingTime = "12:00-21:00", distance = "11 km"),
                OfficeSnippet(isAvailable = true, workingTime = "10:00-22:00", distance = "1,4 km"),
                OfficeSnippet(isAvailable = true, workingTime = "12:00-21:00", distance = "300 m"),
                OfficeSnippet(isAvailable = true, workingTime = "10:00-22:00", distance = "900 m"),
                OfficeSnippet(isAvailable = true, workingTime = "12:00-21:00", distance = "20 km"),
            )
        )
    }

    fun getAvailableFilters() = viewModelScope.launch(Dispatchers.Main) {
        officeFiltersLiveData.postValue(
            mutableListOf(
                OfficeFilter(specific = "Ближайшие"),
                OfficeFilter(specific = "Работает сейчас"),
                OfficeFilter(specific = "Снять рубли"),
                OfficeFilter(specific = "Есть обслуживание"),
            )
        )
    }
}