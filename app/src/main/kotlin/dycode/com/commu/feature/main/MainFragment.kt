package dycode.com.commuchatapp.feature.main

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dycode.com.commu.R
import dycode.com.commu.utils.TinyDB
import dycode.com.commu.cons.Constant
import dycode.com.commu.data.dto.MyroomDto
import dycode.com.commu.feature.base.BaseFragment
import dycode.com.commu.feature.main.MainFragmentAdapter
//import dycode.com.commu.feature.main.MainFragmentAdapter
import dycode.com.commu.feature.main.MainFragmentView
import kotlinx.android.synthetic.main.fragment_main.*
import okhttp3.*
import java.io.IOException

/**
 * Created by Asus on 10/31/2017.
 */
class MainFragment : BaseFragment(), MainFragmentView {

    val firebaseRoot = FirebaseDatabase.getInstance().reference
    val firebaseRoomRef = firebaseRoot.child(Constant.Node.CHATROOMS)
    var adapter: MainFragmentAdapter? = null
    var myUserId: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mView = inflater?.inflate(R.layout.fragment_main, container, false)
        return mView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("onViewCreated()")

        tinyDB = TinyDB(context)
        myUserId = tinyDB?.getString(Constant.Pref.USER_ID)

        val linear = LinearLayoutManager(context)
        linear.reverseLayout = true
        fragment_main_rv.layoutManager = linear
        fragment_main_rv.itemAnimator = DefaultItemAnimator()

        println(myUserId)

        val query = firebaseRoomRef.child(myUserId).orderByChild("lastTimeStamp")
        val options = FirebaseRecyclerOptions.Builder<MyroomDto>()
                .setQuery(query, MyroomDto::class.java)
                .build()
        adapter = MainFragmentAdapter(options, Glide.with(this))

        fragment_main_rv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.stopListening()
    }

    /* ===== MvpView implementation ===== */

    override fun showLoading(isShow: Boolean) {
        baseShowLoading(isShow)
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        baseShowError(errorCode, errorMessage)
    }

    /* ===== End of MvpView implementation ===== */
}






