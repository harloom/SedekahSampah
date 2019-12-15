package com.overdrive.sedekahsampah.ui.home.post.komentar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.Komentar
import com.overdrive.sedekahsampah.models.Post
import kotlinx.android.synthetic.main.bottom_post_komentar_fragment.*

class KomentarBottomFragment: BottomSheetDialogFragment(), KomentarAdapter.Interaction,
    View.OnClickListener {
    override fun onClick(p0: View?) {

    }

    override fun onItemSelected(position: Int, item: Komentar) {

    }

    companion object{
        const val POST_KOMENTAR = "PostKomentar";
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_post_komentar_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        post = arguments?.getParcelable<Post>(POST_KOMENTAR)
        post?.let {
            initUi(it)
        }
    }

    private fun initUi(it: Post) {
        mAdapter = KomentarAdapter(this@KomentarBottomFragment)
        subcribeList()
        balasanLayout.setEndIconOnClickListener(this@KomentarBottomFragment)
        subcribeFrom()
        subcribeButtonSend()

        subcribeStatus()
    }


    private  var post: Post? =null
    private lateinit var mAdapter: KomentarAdapter



    private fun subcribeButtonSend() {
//        balasanLayout.isEndIconVisible = false
//        viewModel.etKomentar.observe(this@KomentarFragmentBottomSheet, Observer {
//            balasanLayout.isEndIconVisible = it
//        })
    }
    private fun subcribeFrom(){
//        etKomentar.afterTextChanged {
//            jobChangeText?.cancel()
//            jobChangeText = CoroutineScope(Main).launch {
//                delay(300)
//                viewModel.fromMassageChange(it)
//            }
//        }
    }
    private fun subcribeList() {
        rcv_commentar.apply {
            adapter = mAdapter
        }

//        viewModel.records.observe(this@KomentarFragmentBottomSheet, Observer {
//            mAdapter.submitList(it)
//        })
    }

    private fun subcribeStatus() {
//        viewModel.initialLoad.observe(this@KomentarFragmentBottomSheet, Observer {
//            if (it == NetworkState.LOADING) {
//                // Show loading
//
//            } else {
//                stopAnimation()
//                if (it.status == NetworkState.Status.SUCCESS_EMPTY) {
//                    // Show empty state for initial load
//                }
//            }
//        })
    }

//    private fun goToMoreThread(
//        item:Post,
//        position: Int
//    ){
//        val newFragment = ComentarInfo(item,position,onMoredCallback)
//
//        // The device is using a large layout, so show the fragment as a dialog
//        if (fragmentManager != null) {
//            newFragment.show(childFragmentManager, "morePost")
//        }
//
//
//    }

//
//    private fun stopAnimation() {
//        val s = shimmer_animation
//        CoroutineScope(Main).launch {
//            s.stopShimmerAnimation()
//            s.visibility = View.GONE
//            delay(500)
//        }
//
//    }
//
//    private fun startAnimation() {
//        CoroutineScope(Main).launch {
//            shimmer_animation.visibility = View.VISIBLE
//            shimmer_animation.startShimmerAnimation()
//            delay(500)
//        }
//
//    }
}