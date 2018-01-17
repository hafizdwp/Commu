package dycode.com.commu.feature.chatroom

import android.util.Log
import dycode.com.commu.feature.base.BasePresenter
import dycode.com.commu.utils.NetworkError
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.Meta
import dycode.com.commuchatapp.data.model.PushnotifModel
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Asus on 11/24/2017.
 */
class ChatroomPresenter(val dataManager: DataManager) : BasePresenter<ChatroomView>() {

    val TAG = "ChatroomPresenter"
    var subscription: Subscription? = null

    fun pushnotif(mToken: String, mTitle: String, mMessage: String) {
        val model = PushnotifModel(mToken, mTitle, mMessage)
        subscription = dataManager.pushnotif(model)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: Meta? ->
                    Log.i(TAG, response.toString())
                },{ error ->
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

    override fun detachView() {
        super.detachView()
        subscription?.unsubscribe()
    }
}