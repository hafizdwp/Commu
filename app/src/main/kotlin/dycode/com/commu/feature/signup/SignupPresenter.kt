package dycode.com.commu.feature.signup

import android.util.Log
import dycode.com.commu.utils.NetworkError
import dycode.com.commu.feature.base.BasePresenter
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Asus on 11/14/2017.
 */
class SignupPresenter(val dataManager: DataManager) : BasePresenter<SignupView>() {

    val TAG = "SignupPresenter"
    var subSignup: Subscription? = null

    fun signup(email: String, username: String, fullname: String, password: String) {
        if(!isViewAttached) return

        mvpView?.showLoading(true)
        subSignup = dataManager.signup(SignupModel(email, username, fullname, password, password))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    response: ApiResponse<String> ->
                    Log.i(TAG, response.toString())
                    mvpView?.showLoading(false)
                    mvpView?.onSignupSuccess(response.data)
                },{
                    error: Throwable? ->
                    error?.printStackTrace()
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

    override fun detachView() {
        super.detachView()
        subSignup?.unsubscribe()
    }
}