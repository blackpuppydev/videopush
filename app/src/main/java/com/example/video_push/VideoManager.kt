package com.example.video_push

import android.content.Context
import com.example.video_push.model.VideoFile
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.IOException

class VideoManager {

    companion object {

        //get json from asset
        fun loadJSONFromAssets(context: Context, fileName: String = "content.json"): String? {
            return try {
                val inputStream = context.assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, Charsets.UTF_8)
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }
        }

        //convert to class
        fun getGsonFromJsonVideo(json:String):ArrayList<VideoFile>{
            val jsonListType = object : TypeToken<ArrayList<VideoFile>>() {}.type
            return Gson().fromJson(json, jsonListType)
        }

    }


}