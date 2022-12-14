package com.huawei.hmssitekitsample

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.*
import com.huawei.hmssitekitsample.Utils.apiKey
import com.huawei.hmssitekitsample.Utils.parseDouble
import com.huawei.hmssitekitsample.Utils.parseInt
import java.util.*
import kotlin.collections.ArrayList

class QuerySuggestionActivity : AppCompatActivity(), View.OnClickListener {
    // Declare a SearchService object.
    private var searchService: SearchService? = null
    private var locationTypeSpinner: CheckboxSpinner? = null
    private var countryListSpinner: CheckboxCountriesSpinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_suggestion)

        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, apiKey)
        findViewById<View>(R.id.search_query_suggestion_button).setOnClickListener(this)
        val poiTypes: MutableList<LocationType> = ArrayList()
        poiTypes.add(LocationType.GEOCODE)
        poiTypes.add(LocationType.ADDRESS)
        poiTypes.add(LocationType.ESTABLISHMENT)
        poiTypes.add(LocationType.REGIONS)
        poiTypes.add(LocationType.CITIES)
        val poiTypesInput = findViewById<TextView>(R.id.query_suggestion_poi_type_input)
        poiTypesInput.isEnabled = false
        locationTypeSpinner = CheckboxSpinner(findViewById(R.id.switch_query_suggestion_poitype), poiTypesInput, poiTypes)

        val countryList: MutableList<String> = ArrayList()
        countryList.add("en")
        countryList.add("fr")
        countryList.add("cn")
        countryList.add("de")
        countryList.add("ko")

        val countryListInput = findViewById<TextView>(R.id.query_suggestion_country_list_input)
        countryListInput.isEnabled = false

        countryListSpinner = CheckboxCountriesSpinner(findViewById(R.id.switch_query_suggestion_countrylist), countryListInput, countryList)
    }

    // Create a search result listener.
    private val searchResultListener: SearchResultListener<QuerySuggestionResponse?> = object : SearchResultListener<QuerySuggestionResponse?> {
        // Return search results upon a successful search.
        override fun onSearchResult(results: QuerySuggestionResponse?) {
            (findViewById<View>(R.id.query_suggestion_result_status) as TextView).text = "success"
            val resultView = findViewById<TextView>(R.id.query_suggestion_result_text)
            val stringBuilder = StringBuilder()
            if (results != null) {
                val sites = results.getSites()
                if (sites != null && sites.size > 0) {
                    var count = 1
                    for (site in sites) {
                        val addressDetail = site.getAddress()
                        val location = site.getLocation()
                        val poi = site.getPoi()
                        val viewport = site.getViewport()
                        stringBuilder.append(String.format(
                            "[%s] siteId: '%s', name: %s, formatAddress: %s, country: %s, countryCode: %s, location: %s, distance: %s, poiTypes: %s, viewport: %s, ",
                            "" + count++, site.getSiteId(), site.getName(), site.getFormatAddress(),
                            if (addressDetail == null) "" else addressDetail.getCountry(),
                            if (addressDetail == null) "" else addressDetail.getCountryCode(),
                            if (location == null) "" else location.getLat().toString() + "," + location.getLng(), site.getDistance(),
                            if (poi == null) "" else Arrays.toString(poi.getPoiTypes()),
                            if (viewport == null) "" else "northeast{lat=" + viewport.getNortheast().getLat().toString() + ", lng=" + viewport.getNortheast().getLng().toString() + "},"
                                    + "southwest{lat=" + viewport.getSouthwest().getLat().toString() + ", lng=" + viewport.getSouthwest().getLng().toString() + "}"))
                        if (poi != null) {
                            val g = Gson()
                            val jsonString = g.toJson(poi.getChildrenNodes())
                            stringBuilder.append(String.format("childrenNode: %s \n\n", jsonString))
                        }
                    }
                } else {
                    stringBuilder.append("0 results")
                }
            }
            resultView.text = stringBuilder
        }

        // Return the result code and description upon a search exception.
        override fun onSearchError(status: SearchStatus) {
            (findViewById<View>(R.id.query_suggestion_result_text) as TextView).text = ""
            (findViewById<View>(R.id.query_suggestion_result_status) as TextView).text = "failed ${status.getErrorCode()}  ${status.getErrorMessage()}"
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.search_query_suggestion_button) {
            querySuggestion()
        }
    }

    private fun querySuggestion() {
        // Create a request body.
        val request = QuerySuggestionRequest()
        val query = (findViewById<View>(R.id.query_suggestion_query_input) as TextView).text.toString()
        val radius = (findViewById<View>(R.id.query_suggestion_radius_input) as TextView).text.toString()
        val language = (findViewById<View>(R.id.query_suggestion_language_input) as TextView).text.toString()
        val locationLatitude = (findViewById<View>(R.id.query_suggestion_location_lat_input) as TextView).text.toString()
        val locationLongitude = (findViewById<View>(R.id.query_suggestion_location_lng_input) as TextView).text.toString()
        val countryCode = (findViewById<View>(R.id.query_suggestion_country_code_input) as TextView).text.toString()
        val northeastLatText = (findViewById<View>(R.id.query_suggestion_bounds_northeast_lat_input) as TextView).text.toString()
        val northeastLngText = (findViewById<View>(R.id.query_suggestion_bounds_northeast_lng_input) as TextView).text.toString()
        val southwestLatText = (findViewById<View>(R.id.query_suggestion_bounds_southwest_lat_input) as TextView).text.toString()
        val southwestLngText = (findViewById<View>(R.id.query_suggestion_bounds_southwest_lng_input) as TextView).text.toString()
        if (!TextUtils.isEmpty(language)) {
            request.setLanguage(language)
        }
        if (!TextUtils.isEmpty(query)) {
            request.setQuery(query)
        }
        if (!TextUtils.isEmpty(countryCode)) {
            request.setCountryCode(countryCode)
        }
        val countryList: List<String> = getCountryList()
        if (countryList.isNotEmpty()) {
            request.setCountries(countryList)
        }
        val radiusValue: Int? = parseInt(radius)
        if (radiusValue != null) {
            request.setRadius(radiusValue)
        }
        val northeastLat = parseDouble(northeastLatText)
        val northeastLng = parseDouble(northeastLngText)
        val southwestLat = parseDouble(southwestLatText)
        val southwestLng = parseDouble(southwestLngText)
        if (northeastLat != null && northeastLng != null && southwestLat != null && southwestLng != null) {
            val northeast = Coordinate(northeastLat, northeastLng)
            val southwest = Coordinate(southwestLat, southwestLng)
            val bounds = CoordinateBounds(northeast, southwest)
            request.setBounds(bounds)
        }
        val lat = parseDouble(locationLatitude)
        val lng = parseDouble(locationLongitude)
        if (lat != null && lng != null) {
            request.setLocation(Coordinate(lat, lng))
        }
        val locationTypes = locationTypes
        request.setPoiTypes(locationTypes)
        request.isChildren = findViewById<Switch>(R.id.switch_query_suggestion_children).isChecked
        request.strictBounds = findViewById<Switch>(R.id.switch_query_suggestion_strict_bounds).isChecked

        // Call the place search suggestion API.
        searchService?.querySuggestion(request, searchResultListener)
    }

    private val locationTypes: List<LocationType?>
        get() = if ((findViewById<View>(R.id.switch_query_suggestion_poitype) as Switch).isChecked) {
            locationTypeSpinner!!.selectedLocationTypes
        } else {
            ArrayList()
        }

    private fun getCountryList(): List<String> {
        return if (findViewById<Switch>(R.id.switch_query_suggestion_countrylist).isChecked) {
            countryListSpinner!!.selectedCountryList
        } else {
            ArrayList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchService = null
    }
}