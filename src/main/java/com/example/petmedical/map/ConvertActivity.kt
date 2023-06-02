package com.example.petmedical.map

import android.Manifest
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import net.daum.mf.map.api.MapView.CurrentLocationEventListener
import android.view.ViewGroup
import android.os.Bundle
import android.content.pm.PackageManager
import net.daum.mf.map.api.MapPoint
import androidx.core.app.ActivityCompat
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.petmedical.MainActivity
import com.example.petmedical.R
import com.example.petmedical.databinding.ActivityMaptestBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConvertActivity : AppCompatActivity(), View.OnClickListener, CurrentLocationEventListener,
    MapView.MapViewEventListener {
    private val binding by lazy { ActivityMaptestBinding.inflate(layoutInflater) }
    private var mapView: MapView? = null
    private var mapViewContainer: ViewGroup? = null
    private val BASE_URL = "https://dapi.kakao.com/"
    private val API_KEY = "KakaoAK 58d18bfcce74238262ec0874598d74e1"
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    private val gomain = View.OnClickListener {

        finish()
    }

    var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
//    private var userLocation: MapPoint? = null // 현재 위치 정보를 저장할 변수


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maptest)
        setContentView(binding.root)

        // 지도 띄우기
        mapView = MapView(this)
        mapViewContainer = findViewById<View>(R.id.map) as ViewGroup
        mapViewContainer!!.addView(mapView)
        mapView!!.setCurrentLocationEventListener(this)


        if (!checkLocationServicesStatus()) {
            // GPS 활성화
            showDialogForLocationServiceSetting()
        } else {
            // 런타임 퍼미션 처리
            checkRunTimePermission()

            // 현 위치 잡기
            mapView!!.currentLocationTrackingMode =
                MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        }
        // KakaoAPI 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val kakaoAPI = retrofit.create(KakaoAPI::class.java)

//        // 사용자 위치 가져오기
//        val userLocation = MapPoint.mapPointWithGeoCoord(currentLatitude, currentLongitude)
//        val latitude = userLocation.mapPointGeoCoord.longitude.toDouble()
//        val longitude = userLocation.mapPointGeoCoord.latitude.toDouble()
//
//        Log.i(TAG, "유저위치 x = $currentLatitude")
//        Log.i(TAG, "유저위치 y = $currentLongitude")

        // 검색 요청 보내기
        val call = kakaoAPI.getSearchKeyword(
            API_KEY,
            "동물병원",
            currentLongitude,
            currentLatitude,
            3000,
            "distance"
        )
        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    // 검색 결과 처리
                    if (result != null) {
                        val places = result.documents
                        // 장소 리스트를 사용하여 지도에 마커 표시 등의 작업 수행
                        for (place in places) {
                            val marker = MapPOIItem() // Kakao 지도 SDK의 마커 객체 생성

                            marker.itemName = place.place_name // 마커에 장소명 설정
                            marker.mapPoint = MapPoint.mapPointWithGeoCoord(
                                place.y.toDouble(),
                                place.x.toDouble()
                            ) // 마커의 좌표 설정
                            marker.markerType = MapPOIItem.MarkerType.BluePin // 마커 이미지 타입 설정

                            mapView!!.addPOIItem(marker) // 마커를 지도에 추가
                        }
                    }
                } else {
                    // 검색 실패 처리
                    Log.e(TAG, "검색 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 검색 실패 처리
                Log.e(TAG, "검색 실패: ${t.message}")
            }
        })

        binding.mainbutton.setOnClickListener(gomain)
        Log.i("ConvertActivity", "onCreate")

    }

    override fun onCurrentLocationUpdate(
        mapView: MapView,
        currentLocation: MapPoint,
        accuracyInMeters: Float
    ) {
        val mapPointGeo = currentLocation.mapPointGeoCoord
        currentLatitude = mapPointGeo.latitude
        currentLongitude = mapPointGeo.longitude
        Log.i("ConvertActivity", "사용자 현재 위치 좌표: (${mapPointGeo.latitude}, ${mapPointGeo.longitude}")
        Log.e(
            LOG_TAG, String.format(
                "MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)",
                mapPointGeo.latitude,
                mapPointGeo.longitude,
                accuracyInMeters
            )
        )

        val userLocation = MapPoint.mapPointWithGeoCoord(currentLatitude, currentLongitude)
        val latitude = userLocation.mapPointGeoCoord.latitude.toDouble()
        val longitude = userLocation.mapPointGeoCoord.longitude.toDouble()

        Log.i(TAG, "유저위치 x = $latitude")
        Log.i(TAG, "유저위치 y = $longitude")

        mapView.setMapCenterPoint(userLocation, true) // 지도 중심점 이동

        // KakaoAPI 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val kakaoAPI = retrofit.create(KakaoAPI::class.java)

        // 검색 요청 보내기
        val call =
            kakaoAPI.getSearchKeyword(API_KEY, "동물병원", longitude, latitude, 3000, "distance")
        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    // 검색 결과 처리
                    if (result != null) {
                        val places = result.documents
                        // 장소 리스트를 사용하여 지도에 마커 표시 등의 작업 수행
                        for (place in places) {
                            val marker = MapPOIItem() // Kakao 지도 SDK의 마커 객체 생성

                            marker.itemName = place.place_name // 마커에 장소명 설정
                            marker.mapPoint = MapPoint.mapPointWithGeoCoord(
                                place.y.toDouble(),
                                place.x.toDouble()
                            ) // 마커의 좌표 설정
                            marker.markerType = MapPOIItem.MarkerType.BluePin // 마커 이미지 타입 설정

                            mapView.addPOIItem(marker) // 마커를 지도에 추가
                        }
                    }
                } else {
                    // 검색 실패 처리
                    Log.e(TAG, "검색 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 검색 실패 처리
                Log.e(TAG, "검색 실패: ${t.message}")
            }
        })
    }


    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String>,
        grandResults: IntArray
    ) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults)
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true

            // 모든 퍼미션을 허용했는지 체크한다.
            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {
                Log.d(LOG_TAG, "start")
                //위치 값을 가져올 수 있음
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료한다.2 가지 경우가 있다
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                ) {
                    Toast.makeText(
                        this@ConvertActivity,
                        "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@ConvertActivity,
                        "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun checkRunTimePermission() {
        // 런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크한다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this@ConvertActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식한다.)
            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요하다. 2가지 경우(3-1, 4-1)가 있다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@ConvertActivity,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있다.
                Toast.makeText(
                    this@ConvertActivity,
                    "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Toast.LENGTH_LONG
                )
                    .show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionsResult에서 수신된다.
                ActivityCompat.requestPermissions(
                    this@ConvertActivity, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionsResult에서 수신된다.
                ActivityCompat.requestPermissions(
                    this@ConvertActivity, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    // 여기부터는 GPS 활성화를 위한 메소드들
    private fun showDialogForLocationServiceSetting() {
        val builder = AlertDialog.Builder(this@ConvertActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
    앱을 사용하기 위해서는 위치 서비스가 필요합니다.
    위치 설정을 수정하시겠습니까?
    """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton("취소") { dialog, id -> dialog.cancel() }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->                 //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(LOG_TAG, "onActivityResult : GPS 활성화 되있음")
                        checkRunTimePermission()
                        return
                    }
                }
        }
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onCurrentLocationDeviceHeadingUpdate(mapView: MapView, v: Float) {}
    override fun onCurrentLocationUpdateFailed(mapView: MapView) {}
    override fun onCurrentLocationUpdateCancelled(mapView: MapView) {}
    override fun onMapViewInitialized(mapView: MapView) {}
    override fun onMapViewCenterPointMoved(mapView: MapView, mapPoint: MapPoint) {}
    override fun onMapViewZoomLevelChanged(mapView: MapView, i: Int) {}
    override fun onMapViewSingleTapped(mapView: MapView, mapPoint: MapPoint) {}
    override fun onMapViewDoubleTapped(mapView: MapView, mapPoint: MapPoint) {}
    override fun onMapViewLongPressed(mapView: MapView, mapPoint: MapPoint) {}
    override fun onMapViewDragStarted(mapView: MapView, mapPoint: MapPoint) {}
    override fun onMapViewDragEnded(mapView: MapView, mapPoint: MapPoint) {}
    override fun onMapViewMoveFinished(mapView: MapView, mapPoint: MapPoint) {}

    companion object {
        private const val LOG_TAG = "ConvertActivity"
        private const val GPS_ENABLE_REQUEST_CODE = 2001
        private const val PERMISSIONS_REQUEST_CODE = 100
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}