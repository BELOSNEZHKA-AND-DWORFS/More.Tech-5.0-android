package com.vtb.hackaton.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.vtb.hackaton.R
import com.vtb.hackaton.databinding.FragmentMapsBinding
import com.vtb.hackaton.ui.base.BaseMapFragment
import com.vtb.hackaton.ui.info.InfoFragment
import com.yandex.mapkit.Animation
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class MapsFragment : BaseMapFragment(), CameraListener {

    private var _binding: FragmentMapsBinding? = null
    private lateinit var map: Map
    private lateinit var userLocationLayer: UserLocationLayer
    private var followUserLocation = false
    private val viewModel by lazy {
        ViewModelProvider(this)[MapsViewModel::class.java]
    }
    private lateinit var editQueryTextWatcher: TextWatcher

    private var placemarksCollection: MapObjectCollection? = null
    private var routesCollection: MapObjectCollection? = null

    private val searchResultPlacemarkTapListener = MapObjectTapListener { mapObject, _ ->
        val selectedObject = (mapObject.userData as? GeoObject)
        SelectedObjectHolder.selectedObject = selectedObject
        InfoFragment().show(parentFragmentManager, null)
        true
    }

    private val checkLocationPermission = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            onMapReady()
        }
    }

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        map = binding.viewMap.mapWindow.map
        map.move(START_POSITION)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        userInterface()
        viewModel.setVisibleRegion(map.visibleRegion)
        binding.buttonSearch.setOnClickListener { viewModel.startSearch() }
        binding.buttonSearchATMs.setOnClickListener { binding.editQuery.setText("Банкомат ВТБ") }
        binding.buttonSearchOffices.setOnClickListener { binding.editQuery.setText("Отделение банка ВТБ") }
        editQueryTextWatcher = binding.editQuery.doAfterTextChanged { text ->
            if (text.toString() == viewModel.uiState.value.query) return@doAfterTextChanged
            viewModel.setQueryText(text.toString())
        }

        binding.editQuery.setOnEditorActionListener { _, _, _ ->
            viewModel.startSearch()
            true
        }

        viewModel.uiState
            .flowWithLifecycle(lifecycle)
            .onEach {

                val successSearchState = it.searchState as? SearchState.Success
                val searchItems = successSearchState?.items ?: emptyList()
                updateSearchResponsePlacemarks(searchItems)
                if (successSearchState?.zoomToItems == true) {
                    focusCamera(
                        searchItems.map { item -> item.point },
                        successSearchState.itemsBoundingBox
                    )
                }
                binding.editQuery.apply {
                    if (text.toString() != it.query) {
                        setText(it.query)
                    }
                }
            }
            .launchIn(lifecycleScope)

        viewModel.subscribeForSearch().flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun focusCamera(points: List<Point>, boundingBox: BoundingBox) {
        if (points.isEmpty()) return

        val position = if (points.size == 1) {
            map.cameraPosition.run {
                CameraPosition(points.first(), zoom, azimuth, tilt)
            }
        } else {
            map.cameraPosition(Geometry.fromBoundingBox(boundingBox))
        }

        map.move(position, Animation(Animation.Type.SMOOTH, 0.5f), null)
    }

    private fun updateSearchResponsePlacemarks(items: List<SearchResponseItem>) {
        map.mapObjects.clear()

        val imageProvider = ImageProvider.fromResource(requireContext(), R.drawable.ic_vtb_logo)

        items.forEach {
            map.mapObjects.addPlacemark(
                it.point,
                imageProvider,
                IconStyle().apply { scale = 0.5f }
            ).apply {
                addTapListener(searchResultPlacemarkTapListener)
                userData = it.geoObject
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onMapReady()
        } else {
            checkLocationPermission.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        placemarksCollection = map.mapObjects.addCollection()
        routesCollection = map.mapObjects.addCollection()
        binding.viewMap.onStart()
    }

    override fun onStop() {
        binding.viewMap.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun userInterface() {
        val mapLogoAlignment = Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM)
        map.logo.setAlignment(mapLogoAlignment)
        binding.userLocationFab.setOnClickListener {
            moveCameraToUserPosition()
            followUserLocation = true
        }
    }

    private fun onMapReady() {
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(binding.viewMap.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)
        map.addCameraListener(this)
        moveCameraToUserPosition()
    }

    private fun moveCameraToUserPosition() {
        if (userLocationLayer.cameraPosition() != null) {
            val target = userLocationLayer.cameraPosition()!!.target
            map.move(
                CameraPosition(target, 16f, 0f, 0f), Animation(Animation.Type.SMOOTH, 1f), null
            )
        } else {
            map.move(CameraPosition(POINT, 16f, 0f, 0f))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCameraPositionChanged(
        map: Map, cPos: CameraPosition, cUpd: CameraUpdateReason, finish: Boolean
    ) {
        if (finish && followUserLocation) {
            setAnchor()
        } else if (!followUserLocation) {
            noAnchor()
        }
    }

    private fun setAnchor() {
        userLocationLayer.setAnchor(
            PointF(
                (binding.viewMap.width * 0.5).toFloat(), (binding.viewMap.height * 0.5).toFloat()
            ),
            PointF(
                (binding.viewMap.width * 0.5).toFloat(), (binding.viewMap.height * 0.83).toFloat()
            )
        )

        binding.userLocationFab.setImageResource(R.drawable.ic_my_location_black_24dp)

        followUserLocation = false
    }

    private fun noAnchor() {
        userLocationLayer.resetAnchor()
        binding.userLocationFab.setImageResource(R.drawable.ic_location_searching_black_24dp)
    }

    companion object {
        private val POINT = Point(55.751280, 37.629720)
        private val START_POSITION = CameraPosition(Point(59.941282, 30.308046), 13.0f, 0f, 0f)
    }

    object SelectedObjectHolder {
        var selectedObject: GeoObject? = null
    }
}