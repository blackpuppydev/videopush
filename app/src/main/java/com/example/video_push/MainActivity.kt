package com.example.video_push

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.example.video_push.databinding.ActivityMainBinding
import com.example.video_push.model.VideoFile
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var player: ExoPlayer
    var videos = ArrayList<VideoFile>()
    var countFile: Int = 0
    private var handler: Handler? = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //array video
        videos =
            VideoManager.getGsonFromJsonVideo(VideoManager.loadJSONFromAssets(this)!!)

        playMedia(videos)

        binding?.btnStart?.setOnClickListener {
            startPlayer()
        }

        binding?.btnStop?.setOnClickListener {
            stopPlayer()
        }

        binding?.btnResume?.setOnClickListener {
            resumePlayer()
        }

        binding?.btnPause?.setOnClickListener {
            pausePlayer()
        }

    }


    private fun playMedia(file: ArrayList<VideoFile>) {

        val mediaPath = file[countFile].file_path

        if (VideoManager.isFileInAssets(this, mediaPath)) {
            if (mediaPath.endsWith(".jpg") || mediaPath.endsWith(".png")) {

                val durationPic = file[countFile].duration * 1000

                binding?.apply {
                    videoView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    btnStart.visibility = View.GONE
                    btnPause.visibility = View.GONE
                    btnResume.visibility = View.GONE
                    btnStop.visibility = View.GONE
                }

                val bitmap = getBitmapFromAssets(mediaPath)
                binding?.imageView?.setImageBitmap(bitmap)

                handler!!.postDelayed({
                    countFile++

                    if (countFile >= file.size) {
                        countFile = 0
                    }

                    playMedia(file)
                }, durationPic.toLong())

            } else if (mediaPath.endsWith(".mp4")) {

                val durationVideo = file[countFile].duration * 1000

                binding?.apply {
                    videoView.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                    btnStart.visibility = View.VISIBLE
                    btnPause.visibility = View.VISIBLE
                    btnResume.visibility = View.VISIBLE
                    btnStop.visibility = View.VISIBLE
                }

                //set play
                player = ExoPlayer.Builder(this).build()
                binding?.videoView?.player = player

                val assetUri = Uri.parse("asset://${file[countFile].file_path}")
                val mediaItem = MediaItem.fromUri(assetUri)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()


                handler!!.postDelayed({
                    player.stop()

                    countFile++
                    if (countFile >= file.size) {
                        countFile = 0
                    }

                    playMedia(file)
                    player.release()
                }, durationVideo.toLong())

            }


        } else {

            countFile++

            if (countFile >= file.size) {
                countFile = 0
            }

            playMedia(file)
        }


    }

    private fun startPlayer() {
        player.seekTo(0)
        player.play()
    }

    private fun resumePlayer() {
        player.play()
    }

    private fun pausePlayer() {
        player.pause()
    }

    private fun stopPlayer() {
        player.stop()

        countFile++

        if (countFile >= videos.size) {
            countFile = 0
        }

        handler?.removeCallbacksAndMessages(null)
        playMedia(videos)
        player.release()


    }

    override fun onStop() {
        super.onStop()
        handler?.removeCallbacksAndMessages(null)
    }

    private fun getBitmapFromAssets(fileName: String): Bitmap? {
        return try {
            val assetManager = assets
            val name = fileName.replace("assets/", "")
            val inputStream = assetManager.open(name)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}