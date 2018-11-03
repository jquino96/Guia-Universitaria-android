package com.archivouni.guiauniversitaria

import android.content.res.AssetManager
import android.graphics.Rect
import android.util.SparseArray
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.TileProvider.NO_TILE
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


class GoogleMapsTileProvider(private val mAssets: AssetManager) : TileProvider{

    companion object {
        private const val TILE_WIDTH = 256
        private const val TILE_HEIGHT = 256
        private const val BUFFER_SIZE = 16 * 1024
        private val TILE_ZOOMS = SparseArray<Rect>().apply {
            put(15, Rect(10371, 14678, 10373, 14679))
            put(16, Rect(20742, 29356, 20746, 29359))
            put(17, Rect(41485, 58713, 41492, 58718))
            put(18, Rect(82971, 117428, 82984, 117437))
        }
    }


    override fun getTile(x: Int, y: Int, zoom: Int): Tile? {
        System.out.println(y)
        val image = readTileImage(x, y, zoom) as ByteArray
        return if (checkTileExists(x, y, zoom)) Tile(TILE_WIDTH, TILE_HEIGHT, image) else NO_TILE
    }

    private fun readTileImage(x: Int, y: Int, zoom: Int): ByteArray? {
        var inStream: InputStream? = null
        var buffer: ByteArrayOutputStream? = null

        try {
            inStream = mAssets.open(getTileFilename(x, y, zoom))
            buffer = ByteArrayOutputStream()

            var nRead: Int
            val data = ByteArray(BUFFER_SIZE)

            nRead = inStream.read(data, 0, BUFFER_SIZE)
            while (nRead != -1) {
                buffer.write(data, 0, nRead)
                nRead = inStream.read(data,0, BUFFER_SIZE)
            }
            buffer.flush()

            return buffer.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        } finally {
            if (inStream != null) try {
                inStream.close()
            } catch (ignored: Exception) {}

            if (buffer != null) try {
                buffer.close()
            } catch (ignored: Exception) {}


        }
    }

    private fun getTileFilename(x: Int, y: Int, zoom: Int): String {
        return "map_tiles/$zoom/$x/$y.png"
    }
    private fun checkTileExists(x: Int, y: Int, zoom: Int): Boolean {
        val minZoom = 16
        val maxZoom = 18
        val b = TILE_ZOOMS.get(zoom)
        return if (b == null && !(zoom < minZoom || zoom > maxZoom)) false else (b.left <= x && x <= b.right && b.top <= y && y <= b.bottom)
    }
}