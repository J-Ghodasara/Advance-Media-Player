package com.example.jayghodasara.mediaplayer

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.support.v4.app.NotificationCompat
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.RemoteViews
import kotlinx.android.synthetic.main.activity_main.*


class MediaIntentService: Service(), MediaPlayer.OnCompletionListener {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationchannel:NotificationChannel
    lateinit var builder:Notification.Builder
    private val channelid= "com.example.jayghodasara.mediaplayer"
    private val desc="test"
    lateinit var notification:Notification

    var NOTIFY_PLAY:String= "com.example.jayghodasara.mediaplayer.play"
    var NOTIFY_PAUSE:String= "com.example.jayghodasara.mediaplayer.pause"
    var NOTIFY_STOP:String= "com.example.jayghodasara.mediaplayer.stop"
    var NOTIFY_SEEKING:String="com.example.jayghodasara.mediaplayer.seeking"

    companion object {
        var mediaplayer2:MediaPlayer= MediaPlayer()
    }


    lateinit var videofileUri: Uri
    var seekHandler: Handler = Handler()
     var position: Int?=null
    lateinit var context:Context

    override fun onBind(intent: Intent?): IBinder? {

        return null


    }

    override fun onCreate() {
        Log.i("OncreateService","in Oncreate")


    }


    fun shownotification(count:Int){
        notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("value", count)

        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT )
        var remoteView:RemoteViews= RemoteViews(this.packageName,R.layout.custom_notification)


        notification=NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(count.toString())
        .setOngoing(true)

                .setCustomContentView(remoteView)
        .setContentText(desc).setContentIntent(pendingIntent).build()


//        notification.flags= Notification.FLAG_AUTO_CANCEL

        setListeners(remoteView,this)

//        notificationchannel= NotificationChannel(channelid,desc, NotificationManager.IMPORTANCE_HIGH)
//        notificationchannel.enableLights(true)
//        notificationchannel.lightColor =  Color.BLUE
//        notificationchannel.enableVibration(false)
//        notificationManager.createNotificationChannel(notificationchannel)
//
//        builder= Notification.Builder(this,channelid)
//                .setContentTitle(count.toString())
//                .setContentText(desc)
//                .setSmallIcon(R.drawable.notification_icon_background)
//                .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.notification_icon_background))
//                .setContentIntent(pendingIntent)

//        notificationManager.notify(123,builder.build())

        notificationManager.notify(123,notification)
        startForeground(123,notification)


    }

    var run: Runnable = Runnable {
        if(mediaplayer2 != null){
            seekupdation()
        }

    }



    fun seekupdation() {
        if (mediaplayer2.isPlaying) {
            shownotification(mediaplayer2.currentPosition)
          //  seekbar.progress = mediaPlayer!!.currentPosition
            seekHandler.postDelayed(run, 1000)
        }
    }

    fun setListeners(view:RemoteViews,context: Context){

        var play:Intent= Intent(NOTIFY_PLAY)
        var pause:Intent=Intent(NOTIFY_PAUSE)
        var stop:Intent=Intent(NOTIFY_STOP)
       // var seeking:Intent=Intent(NOTIFY_SEEKING)
        play.putExtra("posi", mediaplayer2.currentPosition)
      //  seeking.putExtra("posi", mediaplayer2.currentPosition)

        var pending_play=PendingIntent.getBroadcast(context,0,play,PendingIntent.FLAG_UPDATE_CURRENT)
        var pending_pause=PendingIntent.getBroadcast(context,0,pause,PendingIntent.FLAG_UPDATE_CURRENT)
        var pending_stop=PendingIntent.getBroadcast(context,0,stop,PendingIntent.FLAG_UPDATE_CURRENT)
        view.setOnClickPendingIntent(R.id.play,pending_play)
        view.setOnClickPendingIntent(R.id.pause,pending_pause)
        view.setOnClickPendingIntent(R.id.stop,pending_stop)

    }

    var runforpause: Runnable = Runnable {

            seekupdationforpause()

    }



    fun seekupdationforpause() {

            shownotification(mediaplayer2.currentPosition)
            //  seekbar.progress = mediaPlayer!!.currentPosition
            seekHandler.postDelayed(runforpause, 1000)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i("onstartCommand","in StartCommand")
       // videofileUri = intent!!.extras["videoUri"] as Uri
        Log.i("videoURI",MainActivity.videofileUri.toString())
       // position=intent.extras["position"] as Int
        var intent_value=intent!!.extras["action"]

        when(intent_value){

            "START" ->{
                position=this.getSharedPreferences("Position",android.content.Context.MODE_PRIVATE).getInt("posi",0)
                mediaplayer2= MediaPlayer.create(this,MainActivity.videofileUri)
                //  mediaplayer2.seekTo(position!!)
                mediaplayer2.seekTo(position!!)
                Log.i("in start","started")
                mediaplayer2.start()
                mediaplayer2.setOnCompletionListener(this)
               seekupdation()
            }

            "PAUSE"->{
                if (mediaplayer2.isPlaying) {
                    position = mediaplayer2.currentPosition
                    Log.i("in pause","paused")
                    seekupdationforpause()
                    mediaplayer2.pause()
                    stopSelf()


                }

            }

            else ->
            {
                mediaplayer2.stop()
                var pref=this.getSharedPreferences("Position",android.content.Context.MODE_PRIVATE)
                pref.edit().putInt("posi",0).apply()
                stopSelf()
            }


//            "RESUME"->{
//                if (!mediaplayer2.isPlaying) {
//                    position=this.getSharedPreferences("Position",android.content.Context.MODE_PRIVATE).getInt("posi")
//                    mediaplayer2.seekTo(position!!)
//                    mediaplayer2.start()
//                }
//            }


        }




        var runnable= Runnable {

            broadcastservice(mediaplayer2.currentPosition)
        }
        seekHandler.postDelayed(runnable,1000)
        return START_STICKY
    }

    override fun onCompletion(mp: MediaPlayer?) {
        stopSelf()
    }

    fun broadcastservice(position:Int){
        var intent:Intent= Intent("activity_intent")
        intent.putExtra("curr_position",position)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}