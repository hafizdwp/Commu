package dycode.com.commu.feature.profile

import android.util.Log
import dycode.com.commu.utils.NetworkError
import dycode.com.commu.feature.base.BasePresenter
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.ApiResponse
import dycode.com.commuchatapp.data.model.UserData
import okhttp3.MultipartBody
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Asus on 11/16/2017.
 */
class ProfilePresenter(val dataManager: DataManager) : BasePresenter<ProfileView>() {

    val TAG = "ProfilePresenter"
    var subPhoto: Subscription? = null

    fun updatePhotoProfile(body: MultipartBody.Part) {
        if(!isViewAttached) return

        mvpView?.showLoading(true)
        subPhoto = dataManager.updatePhotoProfile(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: ApiResponse<UserData> ->
                    Log.i(TAG, response.toString())
                    mvpView?.showLoading(false)
                    val message = response.meta.message
                    val photoUrl = response.data.userdata.photo
                    mvpView?.onUpdatePhotoProfileSuccess(message, photoUrl?:"")

                }, { error: Throwable? ->
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

    override fun detachView() {
        super.detachView()
        subPhoto?.unsubscribe()
    }
}