package dycode.com.commu.feature.main

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dycode.com.commu.R
import dycode.com.commu.cons.Constant
import dycode.com.commu.data.dto.MessageDto
import dycode.com.commu.data.dto.MyroomDto
import dycode.com.commu.data.dto.UserDto
import dycode.com.commu.feature.chatroom.ChatroomActivity
import dycode.com.commuchatapp.data.model.MyroomModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Asus on 11/23/2017.
 */
class MainFragmentAdapter(options: FirebaseRecyclerOptions<MyroomDto>,
                          val glide: RequestManager)
    : FirebaseRecyclerAdapter<MyroomDto, MainFragmentAdapter.MyroomHolder>(options) {

    val TAG = "MainFragmentAdapter"
    val firebaseRoot = FirebaseDatabase.getInstance().reference
    val firebaseUsersRef = firebaseRoot.child(Constant.Node.USERS)
    val firebaseMsgRef = firebaseRoot.child(Constant.Node.MESSAGES)

    override fun onBindViewHolder(holder: MyroomHolder?, position: Int, model: MyroomDto?) {
        val idroom = getRef(position).getKey()
        val iduser = model?.iduser
        val lastMessageKey = model?.lastMessageKey
        val lastTimeStamp = model?.lastTimestamp
        val context = holder?.itemView?.context

        //get fullname, photo, and set click listener
        firebaseUsersRef.child(iduser).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                Log.i(TAG, dataSnapshot.toString())
                if (dataSnapshot != null) {
                    val user = dataSnapshot.getValue(UserDto::class.java)
                    val name = user?.fullname
                    val username = user?.username
                    val photo = user?.photo
                    holder?.tvName?.text = name

                    if (photo != null) {
                        if(photo == ""){
                            glide.load(R.drawable.profile_grey)
                                    .into(holder?.ivAvatar)
                        }else{
                            glide.load(photo)
                                    .into(holder?.ivAvatar)
                        }

                        //Bikin.glide(context, photo, holder?.ivAvatar)
                    }

                    //click listener
                    holder?.itemView?.setOnClickListener {
                        ChatroomActivity.start(
                                context!!,
                                iduser!!,
                                MyroomModel(idroom, username!!, photo, name))
                    }

                }
            }
        })

        //get last message
        firebaseMsgRef.child(idroom).child(lastMessageKey).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                Log.i(TAG, dataSnapshot.toString())
                if (dataSnapshot != null) {
                    val message = dataSnapshot.getValue(MessageDto::class.java)
                    val lastMessage = message?.text
                    holder?.tvLastchat?.text = lastMessage
                }
            }
        })

        //timestamp
        val date = SimpleDateFormat("HH:mm a").format(lastTimeStamp?.let { Date(it) })
        holder?.tvTimestamp?.text = date

        //unread message
        val unread = model?.unreadMessage
        if(unread == 0L)
            holder?.tvUnreadmsg?.visibility = TextView.INVISIBLE
        else{
            holder?.tvUnreadmsg?.visibility = TextView.VISIBLE
            holder?.tvUnreadmsg?.text = unread.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyroomHolder {
        return MyroomHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_main, parent, false))
    }

    class MyroomHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatar: ImageView = itemView.findViewById(R.id.item_main_iv)
        val tvName: TextView = itemView.findViewById(R.id.item_main_tv_name)
        val tvLastchat: TextView = itemView.findViewById(R.id.item_main_tv_chat)
        val tvUnreadmsg: TextView = itemView.findViewById(R.id.item_main_tv_count)
        val tvTimestamp: TextView = itemView.findViewById(R.id.item_main_tv_timestamp)
    }

}

