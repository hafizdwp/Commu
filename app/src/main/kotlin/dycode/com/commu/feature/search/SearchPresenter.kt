package dycode.com.commu.feature.search

import android.util.Log
import dycode.com.commu.utils.NetworkError
import dycode.com.commu.feature.base.BasePresenter
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.ApiResponse
import dycode.com.commuchatapp.data.model.FriendModel
import dycode.com.commuchatapp.data.model.SearchData
import dycode.com.commuchatapp.data.model.SearchModel
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Asus on 11/20/2017.
 */
class SearchPresenter(val dataManager: DataManager) : BasePresenter<SearchPiew>() {

    val TAG = "SearchPresenter"
    var subSearch: Subscription? = null
    var subFriend: Subscription? = null

    fun search(query: String?) {
        if (!isViewAttached) return
        if (query == null) return

        mvpView?.showLoading(true)
        subSearch = dataManager.search(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: ApiResponse<List<SearchData>> ->
                    Log.i(TAG, response.toString())

                    val arrSearchModel = ArrayList<SearchModel>()
                    val list = response.data
                    for (i in list.indices) {
                        with(list[i]) {
                            val obj = SearchModel(
                                    username,
                                    email,
                                    this.data.fullname,
                                    this.data.photo
                            )
                            arrSearchModel.add(obj)
                        }
                    }
                    mvpView?.showLoading(false)
                    mvpView?.onSearchSuccess(arrSearchModel)

                }, { error: Throwable ->
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

    fun addFriend(user: String?, name: String) {
        if (user != null) {

            mvpView?.showLoading(true)
            subFriend = dataManager.addFriend(FriendModel(user))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response: ApiResponse<String> ->
                        Log.i(TAG, response.toString())
                        mvpView?.showLoading(false)
                        mvpView?.onAddFriendSuccess(name)

                    }, { error: Throwable ->
                        Log.e(TAG, error.toString())
                        val networkError = NetworkError(error)
                        mvpView?.showLoading(false)
                        mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                    })
        }
    }

    override fun detachView() {
        super.detachView()
        subSearch?.unsubscribe()
        subFriend?.unsubscribe()
    }
}