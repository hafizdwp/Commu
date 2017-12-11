package dycode.com.commu.feature.profile.edit

import android.util.Log
import dycode.com.commu.utils.NetworkError
import dycode.com.commu.feature.base.BasePresenter
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.ApiResponse
import dycode.com.commuchatapp.data.model.UserData
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Asus on 11/16/2017.
 */
class ProfileEditPresenter(val dataManager: DataManager) : BasePresenter<ProfileEditView>() {

    val TAG = "ProfileEditPresenter"
    var subUpdate: Subscription? = null

    fun update(field: String, value: String) {
        if (!isViewAttached) return

        val hashmap = HashMap<String, RequestBody>()
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), value)
        hashmap.put(field, requestBody)

        mvpView?.showLoading(true)
        subUpdate = dataManager.updateProfile(hashmap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: ApiResponse<UserData> ->
                    Log.i(TAG, response.toString())

                    val message = response.meta.message
                    mvpView?.showLoading(false)
                    mvpView?.onUpdateSuccess(message, field, value)

                },{ error: Throwable ->
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

    override fun detachView() {
        super.detachView()
        subUpdate?.unsubscribe()
    }

}