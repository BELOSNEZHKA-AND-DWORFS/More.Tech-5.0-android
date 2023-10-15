package com.vtb.hackaton

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.vtb.hackaton.adapters.OfficeFiltersAdapter
import com.vtb.hackaton.adapters.OfficeListAdapter
import com.vtb.hackaton.databinding.ActivityMainBinding
import com.vtb.hackaton.domain.entity.OfficeSnippet
import com.vtb.hackaton.ui.bottomSheet.BottomSheetViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: BottomSheetViewModel
    private lateinit var officeListAdapter: OfficeListAdapter
    private lateinit var officeFiltersAdapter: OfficeFiltersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_settings,
                R.id.navigation_profile,
                R.id.navigation_maps,
                R.id.navigation_followed
            )
        )
        val bottomSheetLayout: LinearLayout = binding.persistentBottomSheet
        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, state: Int) { print(state) }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        viewModel =
            ViewModelProvider(this).get(BottomSheetViewModel::class.java)
        officeListAdapter =
            OfficeListAdapter { officeSnippet ->
                onClickSnippet(officeSnippet)
            }
        officeFiltersAdapter = OfficeFiltersAdapter()
        setupRecyclerView()
        setupObserver()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = officeListAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        }
        binding.filtersRecyclerView.apply {
            adapter = officeFiltersAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false);
        }
    }

    private fun setupObserver() {
        viewModel.officeSnippetListLiveData.observe(this) { response ->
            officeListAdapter.listOfItems = response
            officeListAdapter.notifyDataSetChanged()
        }
        viewModel.officeFiltersLiveData.observe(this) { response ->
            officeFiltersAdapter.listOfItems = response
            officeFiltersAdapter.notifyDataSetChanged()
        }
    }

    private fun onClickSnippet(snippet: OfficeSnippet) {}

    override fun onResume() {
        super.onResume()
        viewModel.getAvailableFilters()
        viewModel.getNearbyOffices()
    }
}