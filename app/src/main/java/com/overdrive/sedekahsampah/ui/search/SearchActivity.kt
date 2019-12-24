package com.overdrive.sedekahsampah.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.utils.change
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private var job : Job? =null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        imageView3.setOnClickListener {
            finish()
        }

        edit_search.change { text->
            job?.cancel()
            job = CoroutineScope(Main).launch {
                delay(300);
                querySearch(text)
            }

        }

    }

    private fun querySearch(text: String) {

    }
}
