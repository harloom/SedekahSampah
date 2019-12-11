package com.overdrive.sedekahsampah.service

import android.app.*
import android.app.Service.START_NOT_STICKY
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.overdrive.sedekahsampah.MainActivity
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.Post

class UploadService : Service() {
    private val CHANNEL_ID = "${application.packageName} upload_service"
    private val GROUP_UPLOAD = "${application.packageName} group upload"
    companion object{

        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, UploadService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)

        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, UploadService::class.java)
            context.stopService(stopIntent)
        }

    }


    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val input = intent?.getParcelableExtra<Post>("inputExtra")
        taskUpload(input);




        createNotificationChannel()
        val notification = displayNotificationProgress(0)
        startForeground(1, notification)
        //stopSelf();

        return  START_NOT_STICKY
    }



    override fun onBind(p0: Intent?): IBinder? {
      return null
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Upload Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }

    }


    private fun displayNotificationProgress( progression: Int): Notification? {

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setContentTitle("Uploading ")
            .setContentText("File to Server..")
            .setProgress(100, progression, false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(GROUP_UPLOAD)
            .build()
    }


    private fun taskUpload(input: Post?) {
       val  mStorageRef = FirebaseStorage.getInstance().reference


    }

}