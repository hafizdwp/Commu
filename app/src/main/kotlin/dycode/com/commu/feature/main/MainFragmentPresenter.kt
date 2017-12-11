package dycode.com.commu.feature.main

import android.annotation.SuppressLint
import android.util.Log
import dycode.com.commu.utils.NetworkError
import dycode.com.commu.feature.base.BasePresenter
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.ApiResponse
import dycode.com.commuchatapp.data.model.MyroomData
import dycode.com.commuchatapp.data.model.UserData
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Asus on 11/23/2017.
 */
class MainFragmentPresenter(val dataManager: DataManager) : BasePresenter<MainFragmentView>() {

    val TAG = "MainFragmentPresenter"
    var subMyroom: Subscription? = null
    var subMyroomUserdata: Subscription? = null

    @SuppressLint("LongLogTag")
    fun myroom(myUserId: String?) {
        if (!isViewAttached) return

        mvpView?.showLoading(true)
        dataManager.getMyRoom()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: ApiResponse<List<MyroomData>> ->
                    Log.i(TAG + " myroom()", response.toString())

                    val listIdRoom = ArrayList<String>()
                    val listOtherUsername = ArrayList<String>()
                    val roomData = response.data
                    if (roomData.isNotEmpty()) {
                        for (i in roomData.indices) {
                            with(roomData[i]) {

                                val username1 = this.user[0].username
                                val username2 = this.user[1].username
                                val id1 = this.user[0].idUser
                                val id2 = this.user[1].idUser

                                listIdRoom.add(idroom)
                                listOtherUsername.add(if (myUserId == id1) username2 else username1)
                            }
                        }

                        myroomUserdata(listIdRoom, listOtherUsername)

                    } else {
                        return@subscribe
                    }

                }, { error: Throwable ->
                    Log.e(TAG + " myroom()", error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

    @SuppressLint("LongLogTag")
    private fun myroomUserdata(listIdRoom: ArrayList<String>, listOtherUsername: ArrayList<String>) {
        for(i in listIdRoom.indices){
            val idroom = listIdRoom[i]
            val username = listOtherUsername[i]
            subMyroomUserdata = dataManager.getFriendData(username)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        response: ApiResponse<UserData> ->
                        Log.i(TAG+" myroomUserdata()", response.toString())

                        with(response.data.userdata){
//                            mvpView?.onMyroomUserdataSuccess(idroom, username, photo, fullname)
                        }

                        if(i == (listIdRoom.size -1)){
                            mvpView?.showLoading(false)
                        }

                    },{ error: Throwable ->
                        Log.e(TAG+" myroomUserdata()", error.toString())
                        val networkError = NetworkError(error)
                        mvpView?.showLoading(false)
                        mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                    })
        }
    }

    override fun detachView() {
        super.detachView()
        subMyroom?.unsubscribe()
        subMyroomUserdata?.unsubscribe()
    }
}