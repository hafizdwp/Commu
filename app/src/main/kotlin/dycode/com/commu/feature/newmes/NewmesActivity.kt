package dycode.com.commu.feature.newmes

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import dycode.com.commu.R
import dycode.com.commu.cons.Constant
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commu.feature.chatroom.ChatroomActivity
import dycode.com.commu.utils.TinyDB
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.*
import kotlinx.android.synthetic.main.activity_newmes.*

/**
 * Created by Asus on 11/22/2017.
 */
class NewmesActivity : BaseActivity(), NewmesView, SearchView.OnQueryTextListener {

    var mPresenter: NewmesPresenter? = null
    var mAdapter: NewmesAdapter? = null

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, NewmesActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newmes)

        baseSetupToolbar(true, true, "New Message")

        tinyDB = TinyDB(this)
        mPresenter = NewmesPresenter(DataManager(baseGetBearerApi(this)), this)
        mPresenter?.attachView(this)

        mAdapter = NewmesAdapter(this, Glide.with(this))
        newmes_rv.layoutManager = LinearLayoutManager(this)
        newmes_rv.setHasFixedSize(true)
        newmes_rv.isClickable = true
        newmes_rv.itemAnimator = DefaultItemAnimator()
        newmes_rv.adapter = mAdapter

        //label kalau belum punya friends
        newmes_label.visibility = View.INVISIBLE

        mPresenter?.getFriendlist()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val mSearchView = menu?.findItem(R.id.search)?.actionView as SearchView
        mSearchView.isIconified = false
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        mSearchView.queryHint = "Chat someone..."
        mSearchView.isSubmitButtonEnabled = true
        mSearchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            mAdapter?.sortByInput(newText.toLowerCase())
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    /* ===== MvpView implementation ===== */

    var presenter: NewmesPresenter? = null

    override fun newRoom(friendlistModel: FriendlistModel) {
        val token = baseGetToken()
        val api = Client.getBearerRetrofitNewmes(this, token).create(Api::class.java)
        presenter = NewmesPresenter(DataManager(api), this)
        presenter?.attachView(this)
        presenter?.newRoom(NewroomModel(Constant.Roomtype.PRIVATE, friendlistModel.username), friendlistModel)
    }

    override fun onNewRoomSuccess(idroom: String, otherUserId: String, friendlistModel: FriendlistModel) {
        presenter?.detachView()
        tinyDB?.putInt(Constant.Pref.MYROOM, 1)
        ChatroomActivity.start(this, otherUserId, MyroomModel(
                idroom,
                friendlistModel.username,
                friendlistModel.photo,
                friendlistModel.fullname
        ))
    }

    override fun onGetFriendlistSuccess(arrFriendlistModel: ArrayList<FriendlistModel>) {
        mAdapter?.clearItems()
        mAdapter?.addAllItems(arrFriendlistModel)
    }

    override fun showLabel(isHaveFriend: Boolean) {
        if(isHaveFriend) newmes_label.visibility = View.GONE
        else newmes_label.visibility = View.VISIBLE
    }

    override fun showLoading(isShow: Boolean) {
        baseShowLoading(isShow)
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        baseShowError(errorCode, errorMessage)
    }

    /* ===== End of MvpView implementation ===== */
}