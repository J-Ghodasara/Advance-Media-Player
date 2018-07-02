package com.example.jayghodasara.mediaplayer

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.net.Inet4Address

class broadcastreceiver : BroadcastReceiver() {

    var NOTIFY_PLAY: String = "com.example.jayghodasara.mediaplayer.play"
    var NOTIFY_PAUSE: String = "com.example.jayghodasara.mediaplayer.pause"
    var NOTIFY_STOP: String = "com.example.jayghodasara.mediaplayer.stop"
    //var NOTIFY_SEEKING:String="com.example.jayghodasara.mediaplayer.seeking"
    lateinit var pauseintent: Intent
    lateinit var startintent: Intent
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent!!.action == NOTIFY_PLAY) {
            var posi: Int = intent.extras["posi"] as Int
            if (!MediaIntentService.mediaplayer2.isPlaying) {
                startintent = Intent(context, MediaIntentService::class.java)
                //mediaPlayer!!.start()
                var pref = context!!.getSharedPreferences("Position", android.content.Context.MODE_PRIVATE)
                pref.edit().putInt("posi", posi).apply()
                startintent.putExtra("action", "START")

                context!!.startService(startintent)
            } else {
                var i: Intent = Intent(context, MediaIntentService::class.java)
                i.putExtra("action", "")
                context!!.startService(i)
            }


        } else if (intent!!.action == NOTIFY_PAUSE) {
            var position = MediaIntentService.mediaplayer2.currentPosition
            var pref = context!!.getSharedPreferences("Position", android.content.Context.MODE_PRIVATE)
            pref.edit().putInt("posi", position).apply()
            pauseintent = Intent(context, MediaIntentService::class.java)
            pauseintent.putExtra("action", "PAUSE")
            context!!.startService(pauseintent)

        } else if (intent!!.action == NOTIFY_STOP) {
            var i: Intent = Intent(context, MediaIntentService::class.java)
            i.putExtra("action", "")
            context!!.startService(i)
            var int: Intent = Intent(context, MediaIntentService::class.java)
            context.stopService(int)

        }

    }

}