package dycode.com.commu.feature

import android.util.Log
import dycode.com.commu.utils.NetworkError
import dycode.com.commu.feature.base.BasePresenter
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.ApiResponse
import dycode.com.commuchatapp.data.model.MyroomData
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Asus on 11/16/2017.
 */
class MainPresenter(val dataManager: DataManager) : BasePresenter<MainView>() {

    val TAG = "MainPresenter"
    var subMyroom: Subscription? = null
    var subLogout: Subscription? = null

    fun myroom(){
        if(!isViewAttached) return

        mvpView?.showLoading(true)
        subMyroom = dataManager.getMyRoom()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: ApiResponse<List<MyroomData>> ->
                    Log.i(TAG, response.toString())
                    val roomData = response.data
                    mvpView?.showLoading(false)
                    mvpView?.onMyroomSuccess(
                            if(roomData.isNotEmpty()) roomData.size
                            else 0
                    )

                },{ error: Throwable ->
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

    fun logout() {
        if(!isViewAttached) return

        mvpView?.showLoading(true)
        subLogout = dataManager.logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    Log.i(TAG, response.toString())
                    mvpView?.showLoading(false)
                    mvpView?.onLogoutSuccess()

                },{ error ->
                    Log.e(TAG, error.toString())
                    val networkError = NetworkError(error)
                    mvpView?.showLoading(false)
                    mvpView?.showError(networkError.appErrorCode, networkError.appErrorMessage)
                })
    }

    override fun detachView() {
        super.detachView()
        subMyroom?.unsubscribe()
        subLogout?.unsubscribe()
    }

}