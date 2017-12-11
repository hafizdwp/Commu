package dycode.com.commu.feature.newmes

import dycode.com.commu.feature.base.MvpView
import dycode.com.commuchatapp.data.model.FriendlistModel
import dycode.com.commuchatapp.data.model.NewroomData

/**
 * Created by Asus on 11/22/2017.
 */
interface NewmesView : MvpView {

    fun newRoom(friendlistModel: FriendlistModel)
    fun onNewRoomSuccess(idroom: String, otherUserId: String, friendlistModel: FriendlistModel)
    fun onGetFriendlistSuccess(arrFriendlistModel: ArrayList<FriendlistModel>)
}