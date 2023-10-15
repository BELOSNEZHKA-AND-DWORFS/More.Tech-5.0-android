package com.vtb.hackaton.ui.maps

import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point

data class MapUiState(
    val query: String = "",
    val searchState: SearchState = SearchState.Off,
)

sealed interface SearchState {
    object Off : SearchState
    object Loading : SearchState
    object Error : SearchState
    data class Success(
        val items: List<SearchResponseItem>,
        val zoomToItems: Boolean,
        val itemsBoundingBox: BoundingBox,
    ) : SearchState
}

data class SearchResponseItem(
    val point: Point,
    val geoObject: GeoObject?,
)

fun SearchState.toTextStatus(): String {
    return when (this) {
        SearchState.Error -> "Error"
        SearchState.Loading -> "Loading"
        SearchState.Off -> "Off"
        is SearchState.Success -> "Success"
    }
}
