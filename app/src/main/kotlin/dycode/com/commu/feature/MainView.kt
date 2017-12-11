package dycode.com.commu.feature

import dycode.com.commu.feature.base.MvpView

/**
 * Created by Asus on 11/16/2017.
 */
interface MainView : MvpView {

    fun checkIfMyNodeExists();
    fun saveMyFCMToken();
    fun confirmLogout()
    fun onMyroomSuccess(roomCount: Int)
    fun onLogoutSuccess()
}