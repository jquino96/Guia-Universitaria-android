package com.archivouni.guiauniversitaria

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject

/**
 * Reads string as JSONArray and stores in data an array of PointOfInterest objects
 * Input: JSONArray as string
 * Members:
 *      data: array of PointOfInterest objects
 */
class Response(json: String): JSONArray(json) {
    val data = this.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } }
            .map { PointOfInterest(it.toString()) }.toTypedArray()
}

/**
 * Creates PointOfInterest object from a JSONObject string
 * Input: JSONObject as string
 * Members:
 *      id: ID of POI
 *      name: name of POI
 *      description: information about the POI
 *      acronym: acronym of POI
 *      latLng: coordinates of POI
 *      images: array of image paths as strings
 *      type: type of POI(DEFAULT, BUILDING, or ARTWORK)
 */
class PointOfInterest(json: String): JSONObject(json) {
    companion object {
        // keys for POI objects as they appear in JSON
        private const val TAG_DESCRIPTION = "DESCRIPTION"
        private const val TAG_ACRONYM = "ACRONYM"
        private const val TAG_LONGITUDE = "LONGITUDE"
        private const val TAG_LATITUDE = "LATITUDE"
        private const val TAG_IMAGES = "IMAGES"
        private const val TAG_ID = "_id"
        private const val TAG_TYPE = "TYPE"
        private const val TAG_NAME = "NAME"

        enum class TYPE {
            DEFAULT,
            BUILDING,
            ARTWORK
        }
    }

    val id: Int = this.optInt(TAG_ID)
    val type = when(this.optString(TAG_TYPE)) {
        "building" -> TYPE.BUILDING
        "artwork" -> TYPE.ARTWORK
        else -> TYPE.DEFAULT
    }
    val name: String? = this.optString(TAG_NAME)
    val description: String? = this.optString(TAG_DESCRIPTION)
    val acronym: String? = this.optString(TAG_ACRONYM)
    val latLng: LatLng? = LatLng(this.optDouble(TAG_LATITUDE), this.optDouble(TAG_LONGITUDE))
    // Convert JSONArray to array of strings
    val images = this.optJSONArray(TAG_IMAGES).let {
        Array(it.length()) { i ->
            it[i].toString()
        }
    }
}