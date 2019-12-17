package com.overdrive.sedekahsampah.ui.home.post.komentar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.overdrive.sedekahsampah.R
import com.overdrive.sedekahsampah.models.Komentar
import com.overdrive.sedekahsampah.models.Post
import com.overdrive.sedekahsampah.utils.change

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


    private lateinit var formKomentar : TextInputEditText;
    private lateinit var buttonSend : TextInputLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_post_komentar_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        post = arguments?.getParcelable<Post>(POST_KOMENTAR)
        formKomentar = view.findViewById(R.id.etKomentar)
        buttonSend = view.findViewById(R.id.balasanLayout)
        post?.let {
            if(FirebaseAuth.getInstance().currentUser!!.isAnonymous){
                buttonSend.visibility = View.VISIBLE
            }

            initUi(it)
        }
    }

    private fun initUi(_post: Post) {
        mAdapter = KomentarAdapter(this@KomentarBottomFragment)
        subcribeList()
        balasanLayout.setEndIconOnClickListener(this@KomentarBottomFragment)
        subcribeFrom()
        subcribeStatus()
        buttonSend.setEndIconOnClickListener {
            formKomentar.setText("")
            buttonSend.isEndIconVisible = true
            FirebaseFirestore.getInstance()
                .collection("post/${post!!.id}/komentar").add(
                    Komentar(
                        idPost =_post.id,
                        body =  formKomentar.text.toString()
                        , timeStamp =Timestamp.now(),
                        uid = FirebaseAuth.getInstance().currentUser!!.uid))
                .addOnSuccessListener {
                    Toast.makeText(context,"Komentar is Succesfull",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context,"Komentar is Fail : ${it.message}",Toast.LENGTH_SHORT).show()
                }
        }
    }


    private  var post: Post? =null
    private lateinit var mAdapter: KomentarAdapter



    private fun subcribeFrom(){
     formKomentar.change {
        if(it.isBlank()){
            buttonSend.isEndIconVisible =true
            return@change
        }

         buttonSend.isEndIconVisible =false
      }
    }
    private fun subcribeList() {
        rcv_commentar.apply {
            adapter = mAdapter
        }

        FirebaseFirestore.getInstance()
            .collection("post/${post!!.id}/komentar")
            .addSnapshotListener { snapshot, exception ->
                if(exception !=null){
                    return@addSnapshotListener
                }
                val listKomentar = snapshot?.toObjects(Komentar::class.java)
                if (listKomentar != null) {
                    mAdapter.submitList(listKomentar)
                }
                //not paging

            }

    }

    private fun subcribeStatus() {

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