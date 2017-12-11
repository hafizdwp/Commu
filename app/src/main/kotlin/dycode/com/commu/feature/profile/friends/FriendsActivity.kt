package dycode.com.commu.feature.profile.friends

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.bumptech.glide.Glide
import dycode.com.commu.utils.DividerItemDecoration
import dycode.com.commu.R
import dycode.com.commu.utils.TinyDB
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.FriendlistModel
import kotlinx.android.synthetic.main.activity_friends.*

/**
 * Created by Asus on 11/17/2017.
 */
class FriendsActivity : BaseActivity(), FriendsView {

    var mPresenter: FriendsPresenter? = null
    var mAdapter: FriendsAdapter? = null

    companion object {
        fun start(context: Context){
            context.startActivity(Intent(context, FriendsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        baseSetupToolbar(true, true, "Friends")

        tinyDB = TinyDB(this)
        mPresenter = FriendsPresenter(DataManager(baseGetBearerApi(this)))
        mPresenter?.attachView(this)

        mAdapter = FriendsAdapter(Glide.with(this))
        friends_rv.layoutManager = LinearLayoutManager(this)
        friends_rv.setHasFixedSize(true)
        friends_rv.isClickable = true
        friends_rv.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        friends_rv.itemAnimator = DefaultItemAnimator()
        friends_rv.adapter = mAdapter

        //load friends
        mPresenter?.getFriendlist()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }

    override fun onGetFriendlistSuccess(friendlist: ArrayList<FriendlistModel>) {
        mAdapter?.addAllItem(friendlist)
        friends_tv_total.text = "Total :"+friendlist.size.toString()
    }

    override fun showLoading(isShow: Boolean) {
        baseShowLoading(isShow)
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        baseShowError(errorCode, errorMessage)
    }
}