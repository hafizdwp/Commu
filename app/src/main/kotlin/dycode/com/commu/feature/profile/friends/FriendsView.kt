package dycode.com.commu.feature.profile.friends

import dycode.com.commu.feature.base.MvpView
import dycode.com.commuchatapp.data.model.FriendlistModel

/**
 * Created by Asus on 11/17/2017.
 */
interface FriendsView : MvpView {

    fun onGetFriendlistSuccess(friendlist: ArrayList<FriendlistModel>)
}