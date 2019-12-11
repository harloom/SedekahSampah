package com.overdrive.sedekahsampah.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.overdrive.sedekahsampah.MainActivity
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.ImageStorage
import com.overdrive.sedekahsampah.models.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.*

class UploadService : Service() {
    private val CHANNEL_ID = "${application.packageName} upload_service"
    private val GROUP_UPLOAD = "${application.packageName} group upload"
    companion object{

        fun startService(context: Context, post : Post ,image: List<String>?) {
            val startIntent = Intent(context, UploadService::class.java)
            startIntent.putExtra("inputExtra", post)
            startIntent.putStringArrayListExtra("inputImage",image as ArrayList<String>)
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
        val inputImage = intent?.getStringArrayListExtra("inputImage")
        taskUpload(input,inputImage);




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
        return NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setContentTitle("Uploading ")
            .setContentText("File to Server..")
            .setProgress(100, progression, false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(GROUP_UPLOAD)
            .build()
    }


    private fun taskUpload(
        input: Post?,
        inputImage: ArrayList<String>?
    ) {
        val  mStorageRef = FirebaseStorage.getInstance()
        val db = FirebaseFirestore.getInstance();
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpg")
            .build()


        val jobParrent = CoroutineScope(IO).launch {
            var progression = 0;
             val deferredList =   inputImage?.map {
              async (IO){
                  try {
                      val file = Uri.fromFile(File(it))
                      val riversRef = mStorageRef.reference.child("post/${UUID.randomUUID()}")
                      val uploadTask = riversRef.putFile(file,metadata)
                      val  size = uploadTask.snapshot.totalByteCount
                      val percent = 100.0 * inputImage.size
                      uploadTask.addOnProgressListener {
                          val progress = (percent * it.bytesTransferred) / size
                          progression += progress.toInt()
                          manager.notify(1,displayNotificationProgress(progression))
                      }.await()
                      val url = uploadTask.await().storage.downloadUrl.await().toString()
                      return@async url
                  }catch (e : Exception){
                      return@async "Error : ${e.message}"
                  }

              }

        }
            val result = deferredList?.awaitAll()
            try {
                val ref  = db.collection("post").add(input!!).await()
                val resImage = result?.map {uri->
                    async (IO){
                        try {
                            val q =   ref.collection("images").add(ImageStorage("",ref.id,uri,Timestamp.now())).await()
                            return@async true
                        }catch (e : Exception){
                            return@async  false
                        }
                    }
                }
               val a =  resImage?.awaitAll()

            }catch (e : Exception){
                print("debug : ${e.message}")
            }

        }
        jobParrent.invokeOnCompletion {
                stopSelf()
        }


    }

}