package dycode.com.commu.feature.chatroom

import com.google.firebase.database.DataSnapshot
import dycode.com.commu.feature.base.MvpView

/**
 * Created by Asus on 11/24/2017.
 */
interface ChatroomView: MvpView {
    fun checkIfUserExists()
    fun resetMyTimestamp()
//    fun updateAdapter(dataSnapshot: DataSnapshot?)
}