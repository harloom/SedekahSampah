package com.overdrive.sedekahsampah.ui.createPost

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.GridView
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.uriImage
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlinx.android.synthetic.main.activity_create.*
import java.io.File

class CreateActivity : AppCompatActivity() {

    private lateinit var adapter: ImageGridAdapter
    private var imageGrid : MutableList<uriImage> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        girdViewInit()

        action_camera.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .compress(1024)//User can only capture image using Camera
                .start{
                        resultCode, data ->
                    if (resultCode == Activity.RESULT_OK) {
                        //Image Uri will not be null for RESULT_OK
                        val fileUri = data?.data
                        //You can get File object from intent
                        val file = ImagePicker.getFile(data)
                        //You can also get File Path from intent
                        val filePath = ImagePicker.getFilePath(data)
                        imageGrid.add(uriImage(url = filePath.toString()))
                        adapter.notifyDataSetChanged()
                    } else if (resultCode == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }
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
                return  true
            }else-> return  true

        }


    }
}
