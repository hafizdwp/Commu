package dycode.com.commu.feature.profile

import dycode.com.commu.feature.base.MvpView

/**
 * Created by Asus on 11/16/2017.
 */
interface ProfileView : MvpView{

    fun refreshContent()
    fun confirmChangePassword()
    fun onUpdatePhotoProfileSuccess(message: String, photoUrl: String)
}