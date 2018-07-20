package kr.saintdev.sunrint.views.activitys

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kr.saintdev.sunrint.R
import kr.saintdev.sunrint.models.manager.gps.GPSManager
import kr.saintdev.sunrint.models.manager.gps.GPSUpdateListener
import android.graphics.Color.parseColor
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kr.saintdev.sunrint.models.APIUrl
import kr.saintdev.sunrint.models.data.ParkingMetaData
import kr.saintdev.sunrint.models.func.ConnectionManager
import kr.saintdev.sunrint.models.func.Convert
import kr.saintdev.sunrint.models.func.PermissionCheck
import kr.saintdev.sunrint.models.func.PermissionGrant
import kr.saintdev.sunrint.models.manager.gps.GPSObject
import kr.saintdev.sunrint.models.manager.gps.openGPSSettings
import kr.saintdev.sunrint.models.sync.BackgroundTask
import kr.saintdev.sunrint.models.sync.BackgroundTaskListener
import kr.saintdev.sunrint.models.sync.http.HttpRequester
import kr.saintdev.sunrint.models.sync.http.HttpResponse
import kr.saintdev.sunrint.views.dialog.MoreInfoDialog
import org.json.JSONArray


class MainActivity : SuperActivity() {
    private lateinit var gpsManager: GPSManager
    private lateinit var mapFragment: SupportMapFragment
    private var mMap: GoogleMap? = null
    private val markerData = arrayListOf<ParkingMetaData>()     // 주차장 관련 정보 저장

    private val NEAR_PARKING_AREA_REQUEST = 0x0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 네트워크 연결 상태 확인
        if(ConnectionManager.isNetworkConnected(this)) {
            if(PermissionCheck.isGPSEnabled(this)) {
                startTrackingService()
            } else {
                openMessageDialog("GPS 가 꺼져있습니다.", "위치 추적을 위해 GPS 를 활성화 해 주세요.", DialogInterface.OnDismissListener {
                    openGPSSettings(this)
                })
            }
        } else {
            // 네트워크 연결 안됨.
            openMessageDialog("네트워크 연결 없슴!", "네트워크 연결을 확인하고 다시 시도하세요.", DialogInterface.OnDismissListener {
                finish()
            })
        }

        main_filter.adapter = ArrayAdapter.createFromResource(this, R.array.filter_option_names, android.R.layout.simple_spinner_dropdown_item)
        main_filter.onItemSelectedListener = OnFilterChangeListener()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 0x0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용 완료.
                startTrackingService()
            }
        }
    }

    /**
     * GPS 업데이트 핸들러
     */
    inner class GPSUpdateHandler : GPSUpdateListener {
        override fun onStart() {
            // GPS 정보 트랙킹 시작
        }

        override fun onUpdated() {
            // 초기화 가능
        }

        override fun onFirstUpdate() {
            // GPS Init success

        }

        override fun onStartFailed(ex: Exception) {
            // GPS init failed
            openMessageDialog("오류가 발생했습니다.", "GPS 정보를 추적 할 수 없습니다.")
        }
    }

    /**
     * Map 정보를 가져오는 핸들러
     */
    inner class MapResponseHandler : OnMapReadyCallback {
        override fun onMapReady(map: GoogleMap?) {
            val lastLocation = gpsManager.getLastLocation()
            closeProgressDialog()

            if(map != null) {
                mMap = map

                // Listener
                mMap!!.setOnMarkerClickListener(OnMarkerClickListener())
                if (lastLocation != null) {
                    // 카메라를 이동 시킨다.
                    map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lastLocation.latitude, lastLocation.longitude)))
                    map.animateCamera(CameraUpdateFactory.zoomTo(14F))

                    // 내 위치 근처 주차장을 찾는다.
                    searchNearParkingArea(lastLocation.latitude, lastLocation.longitude, "all")
                } else {
                    openMessageDialog("Error!", "위치 정보를 받아올 수 없습니다.")
                }
            }
        }
    }

    /**
     * 주차장 검색 완료 핸들러
     */
    inner class NearParkingSearchListener : BackgroundTaskListener<HttpResponse> {
        override fun onSuccess(worker: BackgroundTask<HttpResponse>) {
            val response = worker.getResult()
            closeProgressDialog()

            if(response != null && response.isParseSuccess()) {
                val array = response.getDataUnit("data") as JSONArray

                // 근처 주차장에 대한 정보를 가져온다.
                for(i in 0 until array.length()) {
                    val parkingMeta = array.getJSONObject(i)

                    val metaData = ParkingMetaData(
                            parkingMeta.getString("id"),
                            parkingMeta.getString("name"),
                            parkingMeta.getString("address"),
                            parkingMeta.getBoolean("isOpen"),
                            parkingMeta.getInt("distance"),
                            GPSObject(parkingMeta.getDouble("latitude"),parkingMeta.getDouble("longtitude") )
                    )

                    markerData.add(metaData)

                    // 마커를 그린다.
                    createMarker(metaData, !metaData.isOpened, MarkerType.PARKING)
                }
            } else {
                onFailed(Exception("Parse failed."))
            }

            // 내 마커나 그린다.
            val location = gpsManager.getLastLocation()
            if(location != null) {
                drawMyMarker(location.latitude, location.longitude)
            }
        }

        override fun onFailed(ex: Exception?) {
            closeProgressDialog()
            openMessageDialog("An error occurred.", "근처 주차장을 탐색 할 수 없습니다.${ex?.message}")
        }
    }

    /**
     * 마커 클릭 리스너
     */
    inner class OnMarkerClickListener : GoogleMap.OnMarkerClickListener {
        override fun onMarkerClick(marker: Marker): Boolean {
            val id = marker.tag as? String

            if(id != null) {
                // MoreInfoDialog 표시
                val parkingData = markerData.filter { it.id == id }
                if(parkingData.isNotEmpty()) {
                    val dialog = MoreInfoDialog(this@MainActivity, parkingData[0])
                    dialog.showDialog()
                } else {
                    Toast.makeText(this@MainActivity, "주차장을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            return false
        }
    }

    /**
     * Spinner click listener
     */
    inner class OnFilterChangeListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        var firstIgonore = true
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if(firstIgonore) {
                firstIgonore = false
                return
            }

            val location = gpsManager.getLastLocation()
            val array = R.array.filter_option_values.strArray()

            if(location != null) {
                searchNearParkingArea(location.latitude, location.longitude, array[position])
            }
        }
    }

    fun startTrackingService() {
        // GPS 권한을 확인한다.
        if(PermissionCheck.isGPSPermissionGranted(this)) {
            // GPS 정보 수신기를 시작한다.
            this.gpsManager = GPSManager(this, GPSUpdateHandler())
            this.gpsManager.startListening()

            // Map listener 등록
            this.mapFragment = supportFragmentManager.findFragmentById(R.id.main_map) as SupportMapFragment
            this.mapFragment.getMapAsync(MapResponseHandler())
            openProgressDialog("지도 로드 중...")
        } else {
            PermissionGrant.requestGPSPermission(this, 0x0)
        }
    }

    fun createMarker(metaData: ParkingMetaData, alpha: Boolean, type: MarkerType) {
        val marker = MarkerOptions()

        marker.position(LatLng(metaData.position.latitude, metaData.position.longitude))
                .title(metaData.parkingName)
                .snippet(metaData.parkingAddress)
                .icon(
                        BitmapDescriptorFactory.defaultMarker(
                                when(type) {
                                    MarkerType.PARKING -> BitmapDescriptorFactory.HUE_BLUE
                                    MarkerType.USER -> BitmapDescriptorFactory.HUE_RED
                                }
                        )
                )

        if(alpha) {
            marker.alpha(0.3F)
        }
        val make = mMap?.addMarker(marker)
        make?.tag = metaData.id
    }

    /**
     * 근처 주차장을 탐색합니다.
     */
    fun searchNearParkingArea(latitude: Double, longtitude: Double, option: String) {
        mMap?.clear()

        val args = hashMapOf<String, Any>(
                "latitude" to latitude.toString(),
                "longtitude" to longtitude.toString(),
                "cost" to option
        )

        val requester = HttpRequester(NEAR_PARKING_AREA_REQUEST, APIUrl.NEAR_PARKING_AREA, args, NearParkingSearchListener())
        requester.execute()

        openProgressDialog("주차장 탐색 중...")
    }

    fun drawMyMarker(latitude: Double, longitude: Double) {
        val marker = MarkerOptions()

        marker.position(LatLng(latitude, longitude))
                .title("내 위치")
                .snippet("いまココ！")
                .icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        mMap?.addMarker(marker)
    }

    override fun onStop() {
        super.onStop()

        if(::gpsManager.isInitialized){
            this.gpsManager.stopListening()
        }
    }
}

enum class MarkerType {
    PARKING, USER
}