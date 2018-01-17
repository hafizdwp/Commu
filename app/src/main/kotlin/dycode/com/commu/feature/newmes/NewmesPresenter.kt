package dycode.com.commu.feature.newmes

import android.util.Log
import dycode.com.commu.cons.Constant
import dycode.com.commu.utils.NetworkError
import dycode.com.commu.utils.RetrofitExceptionUtil
import dycode.com.commu.feature.base.BasePresenter
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException

/**
 * Created by Asus on 11/22/2017.
 */
class NewmesPresenter(val dataManager: DataManager,
                      val activity: NewmesActivity) : BasePresenter<NewmesView>() {

    val TAG = "NewmesPresenter"
    val myUserId = activity.tinyDB?.getString(Constant.Pref.USER_ID)
    var subFriendlist: Subscription? = null
    var subNewRoom: Subscription? = null

    fun getFriendlist() {
        if (!isViewAttached) return

        mvpView?.showLoading(true)
        subFriendlist = dataManager.getFriendlist()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: ApiResponse<List<FriendlistData>> ->
                    Log.i(TAG, response.toString())

                    val friendlistModel = ArrayList<FriendlistModel>()
                    val list = response.data
                    for (i in list.indices) {
                        with(list[i]) {
                            val obj = FriendlistModel(
                                    username,
                                    email,
                                    this.userdata.fullname,
                                    this.userdata.photo)
                            friendlistModel.add(obj)
                        }
                    }
                    mvpView?.showLoading(false)
                    mvpView?.showLabel(true)
                    mvpView?.onGetFriendlistSuccess(friendlistModel)

                }, { error: Throwable? ->
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showLabel(false)
//                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

    fun newRoom(model: NewroomModel, friendlistModel: FriendlistModel) {
        if (!isViewAttached) return

        mvpView?.showLoading(true)
        subNewRoom = dataManager.createNewRoom(model)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    response: ApiResponse<NewroomData> ->
                    Log.i(TAG, response.toString())

                    var otherUserId = ""
                    with(response.data.user){
                        otherUserId =
                                if(this[0].idUser == myUserId) this[1].idUser
                                else this[0].idUser
                    }

                    mvpView?.showLoading(false)
                    mvpView?.onNewRoomSuccess(response.data.idroom, otherUserId, friendlistModel)

                }, {
                    e: Throwable ->
                    val error: RetrofitExceptionUtil = e as RetrofitExceptionUtil
                    try {
                        val response: NewroomResponse = error.getErrorBodyAs(NewroomResponse::class.java)
                        Log.i(TAG, response.toString())

                        var otherUserId = ""
                        with(response.data.user){
                            otherUserId =
                                    if(this[0].idUser == myUserId) this[1].idUser
                                    else this[0].idUser
                        }

                        mvpView?.showLoading(false)
                        mvpView?.onNewRoomSuccess(response.data.idroom, otherUserId, friendlistModel)
                    } catch (e: IOException) {
                        Log.e(TAG, e.message)
                        mvpView?.showLoading(false)
                        e.printStackTrace()
                    }
//                    Log.e(TAG, error.toString())
//                    val networkError = NetworkError(error)
//                    mvpView?.showLoading(false)
//                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })

    }

    override fun detachView() {
        super.detachView()
        subFriendlist?.unsubscribe()
        subNewRoom?.unsubscribe()
    }
}