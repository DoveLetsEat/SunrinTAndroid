package kr.saintdev.sunrint.models.func

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt

/**
 * 어플리케이션 공통 함수 정의
 * ! 네이밍 규칙 잘 지키기 !
 * @date 07.20 14:03
 */

object ConnectionManager {
    fun isNetworkConnected(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return (connManager.getNetworkInfo(1).state == NetworkInfo.State.CONNECTED       // wifi connection
                || connManager.getNetworkInfo(0).state == NetworkInfo.State.CONNECTED)
    }

    fun isGPSConnected(context: Context): Boolean {
        /**
         * GPS 의 활성화 여부를 확인한다.
         */

        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled( LocationManager.GPS_PROVIDER )
    }
}

object Convert {
    /**
     * @param m 단위의 거리
     * @return 700 = 700m 으로, 1300m = 1.3km 으로.
     */

    fun convertIntToDistance(distance: Int): String {
        if (distance >= 1000)
            return (String.format("%.1f", distance.toDouble() / 1000)) + "km"
        else
            return "$distance m"
    }
}