package dycode.com.commu.feature.profile.friends

import android.util.Log
import dycode.com.commu.utils.NetworkError
import dycode.com.commu.feature.base.BasePresenter
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.ApiResponse
import dycode.com.commuchatapp.data.model.FriendlistData
import dycode.com.commuchatapp.data.model.FriendlistModel
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Asus on 11/17/2017.
 */
class FriendsPresenter(val dataManager: DataManager) : BasePresenter<FriendsView>() {

    val TAG = "FriendsPresenter"
    var subFriendlist: Subscription? = null

    fun getFriendlist() {
        if (!isViewAttached) return

        mvpView?.showLoading(true)
        subFriendlist = dataManager.getFriendlist()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    response: ApiResponse<List<FriendlistData>> ->
                    Log.i(TAG, response.toString())

                    val friendlistModel = ArrayList<FriendlistModel>()
                    val list = response.data
                    for(i in list.indices){
                        with(list[i]){
                            val obj = FriendlistModel(
                                    username,
                                    email,
                                    this.userdata.fullname,
                                    this.userdata.photo)
                            friendlistModel.add(obj)
                        }
                    }
                    mvpView?.showLoading(false)
                    mvpView?.onGetFriendlistSuccess(friendlistModel)

                }, { error: Throwable? ->
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

    override fun detachView() {
        super.detachView()
        subFriendlist?.unsubscribe()
    }
}