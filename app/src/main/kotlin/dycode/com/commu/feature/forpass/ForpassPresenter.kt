package dycode.com.commu.feature.forpass

import android.util.Log
import dycode.com.commu.utils.NetworkError
import dycode.com.commu.feature.base.BasePresenter
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.ApiResponse
import dycode.com.commuchatapp.data.model.ForpassModel
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Asus on 11/16/2017.
 */
class ForpassPresenter(val dataManager: DataManager): BasePresenter<ForpassView>() {

    val TAG = "ForpassPresenter"
    var subForpass: Subscription? = null

    fun forpass(email: String){
        if(!isViewAttached) return

        mvpView?.showLoading(true)
        subForpass = dataManager.forpass(ForpassModel(email))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    response: ApiResponse<String> ->
                    Log.i(TAG, response.toString())
                    mvpView?.showLoading(false)
                    mvpView?.onForpassSuccess(response.data)
                },{
                    error: Throwable ->
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

}