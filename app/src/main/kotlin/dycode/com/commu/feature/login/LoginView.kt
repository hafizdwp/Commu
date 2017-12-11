package dycode.com.commu.feature.login

import dycode.com.commu.feature.base.MvpView
import dycode.com.commuchatapp.data.model.LoginData
import dycode.com.commuchatapp.data.model.UserData

/**
 * Created by Asus on 11/14/2017.
 */
interface LoginView : MvpView {
    fun onLoginSuccess(token: String, refreshToken: String)
    fun onGetMyProfileSuccess(userData: UserData)
    fun gotoMain()
}