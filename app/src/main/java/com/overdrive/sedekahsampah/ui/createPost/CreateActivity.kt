package com.overdrive.sedekahsampah.ui.createPost

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.Post
import com.overdrive.sedekahsampah.service.UploadService

import kotlinx.android.synthetic.main.activity_create.*
import net.alhazmy13.gota.Gota
import net.alhazmy13.gota.GotaResponse
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource

class CreateActivity : AppCompatActivity(), Gota.OnRequestPermissionsBack {
    override fun onRequestBack(requestId: Int, gotaResponse: GotaResponse) {
        if(gotaResponse.isAllGranted){
            easyImage.openCameraForImage(this@CreateActivity)
        }else{
            permission()
        }
    }

    private lateinit var easyImage: EasyImage
    private lateinit var adapter: ImageGridAdapter
    private var imageGrid : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        easyImage = EasyImage.Builder(this@CreateActivity).setCopyImagesToPublicGalleryFolder(true)
            .setFolderName(getString(R.string.app_name)).allowMultiple(false).build()

        girdViewInit()

        action_camera.setOnClickListener {
            loadImageFromCammera()
        }


        action_library.setOnClickListener {
           com.github.dhaval2404.imagepicker.ImagePicker.
               with(this@CreateActivity).
               galleryOnly().
               compress(1024).start(101)
        }

    }





    private fun girdViewInit(){
         adapter = ImageGridAdapter(context = this@CreateActivity,list =imageGrid )
        grid_view.adapter = adapter
        grid_view.horizontalSpacing = 15
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_create,menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_post->{
                println("Kirim Ke Service")
                if(cekForm()){
                    UploadService.startService(this@CreateActivity,getValue(),imageGrid)
                    onBackPressed()
                }else{
                    Toast.makeText(this@CreateActivity,"Periksa Kembali Form", Toast.LENGTH_LONG).show()
                }



                return  true
            }else-> return  true

        }


    }
    private fun getValue(): Post {
        val mPost = Post("",FirebaseAuth.getInstance().currentUser!!.uid,post_titile.text.toString(),
            post_content.text.toString(), Timestamp.now())

        return mPost
    }

    private fun cekForm() :  Boolean{
     return post_titile.text.toString().isNotBlank() && post_content.text.toString().isNotBlank()
    }


    private fun loadImageFromCammera(){

        permission()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if ( requestCode == 101 &&  resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data

            //You can get File object from intent
            val file = com.github.dhaval2404.imagepicker.ImagePicker.getFile(data)

            //You can also get File Path from intent
            val filePath = com.github.dhaval2404.imagepicker.ImagePicker.getFilePath(data)
            println(filePath)
            imageGrid.add(filePath.toString())
            adapter.notifyDataSetChanged()


        } else if (resultCode == com.github.dhaval2404.imagepicker.ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, com.github.dhaval2404.imagepicker.ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            easyImage.handleActivityResult(requestCode,resultCode,data,this@CreateActivity,object  :
                DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    val filePath  = imageFiles[0].file.path
                    imageGrid.add(filePath.toString())
                    adapter.notifyDataSetChanged()
                }
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
      onBackPressed()
        return true
    }

    private fun permission(){

        Gota.Builder(this@CreateActivity).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,

            Manifest.permission.WRITE_EXTERNAL_STORAGE,

            Manifest.permission.CAMERA)

            .setListener(this@CreateActivity).check()

    }
}
