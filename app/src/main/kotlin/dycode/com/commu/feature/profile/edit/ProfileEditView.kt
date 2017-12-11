package dycode.com.commu.feature.profile.edit

import dycode.com.commu.feature.base.MvpView

/**
 * Created by Asus on 11/16/2017.
 */
interface ProfileEditView : MvpView {

    fun confirmUpdate()
    fun executeUpdate(field: String, value: String)
    fun onUpdateSuccess(message: String, field: String, value: String)
}