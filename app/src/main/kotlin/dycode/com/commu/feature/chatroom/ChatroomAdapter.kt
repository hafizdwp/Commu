package dycode.com.commu.feature.chatroom

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dycode.com.commu.R
import dycode.com.commu.cons.Constant
import dycode.com.commu.data.dto.MessageDto
import dycode.com.commu.data.dto.UserDto
import dycode.com.commu.utils.CircleTransform
import dycode.com.commu.utils.TinyDB
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Asus on 11/24/2017.
 */
class ChatroomAdapter(options: FirebaseRecyclerOptions<MessageDto>,
                      val tinyDB: TinyDB?,
                      val glide: RequestManager)
    : FirebaseRecyclerAdapter<MessageDto, ChatroomAdapter.ChatroomHolder>(options) {

    val TAG = "ChatroomAdapter"
    val firebaseRoot = FirebaseDatabase.getInstance().reference
    val firebaseUsersRef = firebaseRoot.child(Constant.Node.USERS)

    val myUserId = tinyDB?.getString(Constant.Pref.USER_ID)

    override fun onBindViewHolder(holder: ChatroomAdapter.ChatroomHolder?, position: Int, dto: MessageDto?) {
        Log.i(TAG, dto.toString())
        val iduser = dto?.iduser

        //text
        holder?.tvChat?.text = dto?.text

        //timestamp
        val date = SimpleDateFormat("HH:mm a").format(dto?.timestamp?.let { Date(it) })
        holder?.tvTimestamp?.text = date

        //photo
        firebaseUsersRef.child(iduser).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e(TAG, p0.toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i(TAG, dataSnapshot.toString())
                val obj: UserDto? = dataSnapshot.getValue(UserDto::class.java)
                val photo = obj?.photo

                holder?.ivAvatar?.visibility = ImageView.VISIBLE
                if (obj != null) {
//                    if (position == 0) {
                    if(photo == ""){
                        glide.load(R.drawable.profile_grey)
                                .transform(CircleTransform(holder?.itemView?.context))
                                .into(holder?.ivAvatar)
                    }else{
                        glide.load(photo)
                                .transform(CircleTransform(holder?.itemView?.context))
                                .into(holder?.ivAvatar)
                    }

//                    }
//                    if (position > 0) {
//                        glide.load(photo)
//                                .transform(CircleTransform(holder?.itemView?.context))
//                                .into(holder?.ivAvatar)
//                    }
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChatroomAdapter.ChatroomHolder {
        val view =
                if (viewType == 0) LayoutInflater.from(parent?.context).inflate(R.layout.item_chatroom, parent, false)
                else LayoutInflater.from(parent?.context).inflate(R.layout.item_chatroom_other, parent, false)
        return ChatroomHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val dto: MessageDto = getItem(position)
        return if (myUserId == dto.iduser) 0 else 1
    }

    class ChatroomHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvChat: TextView = itemView.findViewById(R.id.item_chatroom_tv)
        val tvTimestamp: TextView = itemView.findViewById(R.id.item_chatroom_timestamp)
        val ivAvatar: ImageView = itemView.findViewById(R.id.item_chatroom_iv)
    }
}