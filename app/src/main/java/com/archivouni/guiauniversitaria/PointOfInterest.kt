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
    val data = this
            .let { poiArray ->
                0.until(poiArray.length()).map { index ->
                    poiArray.getJSONObject(index) } }
            .map { jObject ->
                PointOfInterest(jObject.toString()) }
            .toTypedArray()
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

        enum class Type {
            DEFAULT,
            BUILDING,
            ARTWORK
        }
    }

    val id: Int = getInt(TAG_ID)
    val type = when(getString(TAG_TYPE)) {
        "building" -> Type.BUILDING
        "artwork" -> Type.ARTWORK
        else -> Type.DEFAULT
    }
    val name: String? = if(getString(TAG_NAME) != "null") getString(TAG_NAME) else null
    val description: String? = if(getString(TAG_DESCRIPTION) != "null") getString(TAG_DESCRIPTION) else null
    val acronym: String? = if(getString(TAG_ACRONYM) != "null") getString(TAG_ACRONYM) else null
    val latLng: LatLng? = LatLng(getDouble(TAG_LATITUDE), getDouble(TAG_LONGITUDE))
    // Convert JSONArray to array of strings
    val images = getJSONArray(TAG_IMAGES).let { imgArray ->
        if (imgArray.length() > 0)
            Array(imgArray.length()) { i ->
                imgArray[i].toString()
            }
        else
            null
    }
}