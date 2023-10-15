package com.vtb.hackaton.ui.base

import android.graphics.Color
import androidx.fragment.app.Fragment
import com.vtb.hackaton.R
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider

open class BaseMapFragment  : Fragment(), UserLocationObjectListener{
    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.pin.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.user_arrow))
        userLocationView.arrow.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.user_arrow))
        userLocationView.accuracyCircle.fillColor = Color.BLUE
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}

    override fun onObjectRemoved(p0: UserLocationView) {}


}