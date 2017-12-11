package dycode.com.commu.feature.login

import android.util.Log
import dycode.com.commu.utils.NetworkError
import dycode.com.commu.cons.Constant
import dycode.com.commu.feature.base.BasePresenter
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.ApiResponse
import dycode.com.commuchatapp.data.model.LoginData
import dycode.com.commuchatapp.data.model.UserData
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Asus on 11/14/2017.
 */
class LoginPresenter(val dataManager: DataManager) : BasePresenter<LoginView>() {

    val TAG = "LoginPresenter"
    var subLogin: Subscription? = null
    var subProfile: Subscription? = null

    fun login(username: String, password: String) {
        if (!isViewAttached) return

        mvpView?.showLoading(true)
        subLogin = dataManager.login(username, password, Constant.GRANT_TYPE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: ApiResponse<LoginData> ->
                    Log.i(TAG, response.toString())
                    val loginData = response.data
                    mvpView?.showLoading(false)
                    mvpView?.onLoginSuccess(loginData.accessToken, loginData.refreshToken)
                }, { error ->
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

    fun getMyProfile() {
        if (!isViewAttached) return

        mvpView?.showLoading(true)
        subProfile = dataManager.getProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: ApiResponse<UserData> ->
                    Log.i(TAG, response.toString())
                    mvpView?.showLoading(false)
                    mvpView?.onGetMyProfileSuccess(response.data)
                }, { error ->
                    mvpView?.showLoading(false)
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }


    override fun detachView() {
        super.detachView()
        subLogin?.unsubscribe()
        subProfile?.unsubscribe()
    }

}

//    var subRefreshToken: Subscription? = null
//    fun refreshToken(model: RefreshTokenModel){
//        mvpView?.showLoading(true)
//        subRefreshToken = dataManager.refreshToken(model.grant_type, model.refresh_token)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe({
//                    response: ApiResponse<LoginData> ->
//                    Log.i(TAG, response.toString())
//                    mvpView?.showLoading(false)
//                    mvpView?.onRefreshTokenSuccess(response.data)
//                },{
//                    error: Throwable ->
//                    Log.e(TAG, error.toString())
//                    val networkError = NetworkError(error)
//                    mvpView?.showLoading(false)
//                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
//                })
//    }
