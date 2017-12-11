package dycode.com.commu.feature.signup

import dycode.com.commu.feature.base.MvpView

/**
 * Created by Asus on 11/14/2017.
 */
interface SignupView : MvpView {
    fun onSignupSuccess(message: String)
}