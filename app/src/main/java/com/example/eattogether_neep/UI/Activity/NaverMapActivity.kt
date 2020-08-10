package com.example.eattogether_neep.UI.Activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.eattogether_neep.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.NaverMapSdk.NaverCloudPlatformClient
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class NaverMapActivity : Activity(), OnMapReadyCallback {
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_naver)

        NaverMapSdk.getInstance(this).client = NaverCloudPlatformClient("28x6oj93e7")

        /* MapFragment
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_view, it).commit()
            }*/

        //Naver Map MapView

        var mapView: MapView? = MapView(this)

        val mapViewContainer =
            findViewById<View>(R.id.map_view) as ViewGroup
        mapViewContainer.addView(mapView)
        //mapView.onCreate(savedInstanceState)

        mapView?.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633),true)
    }

    override fun onMapReady(naverMap : NaverMap) {
        val marker = Marker()
        marker.position = LatLng(37.5670135, 126.9783740)
        marker.map = naverMap
    }

    private fun getLocation(){

    }
    /* MapView
    override fun onStart() {
         super.onStart()
         mapView.onStart()
     }

     override fun onResume() {
         super.onResume()
         mapView.onResume()
     }

     override fun onPause() {
         super.onPause()
         mapView.onPause()
     }

     override fun onSaveInstanceState(outState: Bundle) {
         super.onSaveInstanceState(outState)
         mapView.onSaveInstanceState(outState)
     }

     override fun onStop() {
         super.onStop()
         mapView.onStop()
     }

     override fun onDestroy() {
         super.onDestroy()
         mapView.onDestroy()
     }

     override fun onLowMemory() {
         super.onLowMemory()
         mapView.onLowMemory()
     }*/
}

