package dycode.com.commu.feature.search

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import dycode.com.commu.R
import dycode.com.commu.utils.TinyDB
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.SearchModel
import kotlinx.android.synthetic.main.activity_search.*

/**
 * Created by Asus on 11/17/2017.
 */
class SearchActivity : BaseActivity(), SearchPiew, SearchView.OnQueryTextListener {

    var mPresenter: SearchPresenter? = null
    var mAdapter: SearchAdapter? = null

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SearchActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        baseSetupToolbar(true, true, "Search User")

        tinyDB = TinyDB(this)
        mPresenter = SearchPresenter(DataManager(baseGetBearerApi(this)))
        mPresenter?.attachView(this)

        mAdapter = SearchAdapter(this, Glide.with(this))
        search_rv.layoutManager = LinearLayoutManager(this)
        search_rv.setHasFixedSize(true)
        search_rv.isClickable = true
        search_rv.itemAnimator = DefaultItemAnimator()
        search_rv.adapter = mAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val mSearchView = menu.findItem(R.id.search).actionView as SearchView
        mSearchView.isIconified = false
        mSearchView.isSubmitButtonEnabled = false
        mSearchView.setIconifiedByDefault(true)
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        mSearchView.queryHint = "Find new friends..."
        mSearchView.isSubmitButtonEnabled = true
        mSearchView.setOnQueryTextListener(this)

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        mPresenter?.search(query?.toLowerCase())
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    /* ===== MvpView Implementation ===== */

    override fun onSearchSuccess(arrSearchModel: ArrayList<SearchModel>) {
        mAdapter?.clearItem()
        mAdapter?.addAllItem(arrSearchModel)
    }

    override fun onAddFriendSuccess(name: String) {
        var firstMes = "<font color='#01579b'> $name </font>"
        var secondMes = "has been added to your Friends!"

        AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(Html.fromHtml("$firstMes $secondMes"))
                .setPositiveButton("OK", { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .show()
    }

    override fun showLoading(isShow: Boolean) {
        baseShowLoading(isShow)
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        baseShowError(errorCode, errorMessage)
    }

    /* ===== End of MvpView Implementation ===== */
}