package com.huawei.hmssitekitsample

import android.text.TextUtils
import android.util.Log
import com.huawei.hms.site.api.model.LocationType
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.regex.Pattern

object Utils {
    private const val TAG = "Utils"
    fun isNumber(string: String?): Boolean {
        val p = Pattern.compile("[0-9]*")
        val m = p.matcher(string)
        return m.matches()
    }

    @JvmStatic
    fun parseDouble(string: String): Double? {
        var doubleValue: Double? = null
        doubleValue = try {
            string.toDouble()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            null
        }
        return doubleValue
    }

    @JvmStatic
    fun parseInt(string: String): Int? {
        var intValue: Int? = null
        if (TextUtils.isEmpty(string)) {
            return intValue
        }
        intValue = try {
            string.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            null
        }
        return intValue
    }

    fun parseLocationType(originValue: String?): LocationType? {
        var locationType: LocationType? = null
        locationType = try {
            LocationType.valueOf(originValue!!)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
        return locationType
    }// get apiKey from AppGallery Connect

    // need encodeURI the apiKey

    /**
     * It is recommended to save the apiKey to the server to avoid being obtained by hackers.
     * Please get the api_key from the app you created in appgallery
     * Need to encode api_key before use
     */
    @JvmStatic
    val apiKey: String?
        get() {
            // get apiKey from AppGallery Connect
            val apiKey = "your api key"

            // need encodeURI the apiKey
            return try {
                URLEncoder.encode(apiKey, "utf-8")
            } catch (e: UnsupportedEncodingException) {
                Log.e(TAG, "encode apikey error")
                null
            }
        }
}