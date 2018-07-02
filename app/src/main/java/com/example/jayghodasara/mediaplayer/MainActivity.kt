package com.example.jayghodasara.mediaplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            mediaPlayer!!.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }


    companion object {
        lateinit var videofileUri: Uri

    }

    lateinit var surfaceHolder: SurfaceHolder

    var mediaPlayer: MediaPlayer? = null
    var position: Int = 0
    var seekHandler: Handler = Handler()
    lateinit var broadcastReceiver: BroadcastReceiver
    lateinit var t: Thread
    lateinit var startintent: Intent
    lateinit var resumeintent: Intent
    lateinit var pauseintent: Intent
    lateinit var br: broadcastreceiver

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent!!.action == "activity_intent") {
                    position = intent.getIntExtra("curr_position", 0)

                    Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_LONG).show()
                }

            }


        }


        select.setOnClickListener(View.OnClickListener {

            var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("audio/*")
            startActivityForResult(intent, 1)


        })

        play.setOnClickListener(View.OnClickListener {
            position = 0
            var pref = this.getSharedPreferences("Position", android.content.Context.MODE_PRIVATE)
            pref.edit().putInt("posi", position).apply()
            startintent = Intent(this, MediaIntentService::class.java)

            startintent.putExtra("action", "START")

            startService(startintent)


        })


        pause.setOnClickListener(View.OnClickListener {

            if (MediaIntentService.mediaplayer2.isPlaying) {
                position = MediaIntentService.mediaplayer2.currentPosition
                stopService(startintent)
                pauseintent = Intent(this, MediaIntentService::class.java)
                pauseintent.putExtra("action", "PAUSE")
                startService(pauseintent)
//                mediaPlayer!!.pause()
            }


        })
        Resume.setOnClickListener(View.OnClickListener {


            if (!MediaIntentService.mediaplayer2.isPlaying) {
                stopService(startintent)
                var pref = this.getSharedPreferences("Position", android.content.Context.MODE_PRIVATE)
                pref.edit().putInt("posi", position).apply()
                startintent = Intent(this, MediaIntentService::class.java)
                //mediaPlayer!!.start()
                startintent.putExtra("action", "START")

                startService(startintent)

            }

        })

        seekbar.setOnSeekBarChangeListener(this)


    }


    var run: Runnable = Runnable {
        if (mediaPlayer != null) {
            seekupdation()
        }

    }


    fun seekupdation() {
        if (mediaPlayer!!.isPlaying) {
            seekbar.progress = mediaPlayer!!.currentPosition
            seekHandler.postDelayed(run, 1000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        videofileUri = data!!.data
        mediaPlayer = MediaPlayer.create(this, videofileUri)
        seekbar.max = mediaPlayer!!.duration

        path.setText(videofileUri.toString())

    }

    override fun onStop() {
        super.onStop()
        var pref = this.getSharedPreferences("Position", android.content.Context.MODE_PRIVATE)
        pref.edit().putInt("posi", position).apply()
        var int: Intent = Intent("action_start")
        LocalBroadcastManager.getInstance(this).sendBroadcast(int)

    }

    override fun onResume() {
        super.onResume()

    }


    override fun onDestroy() {
        super.onDestroy()

        Log.i("Destroyed", "in Destroy")

    }


}

