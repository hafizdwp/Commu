package dycode.com.commu.feature.base

/**
 * Created by Asus on 9/19/2017.
 */
interface MvpView {
    fun showLoading(isShow: Boolean)
    fun showError(errorCode: Int, errorMessage: String)
}