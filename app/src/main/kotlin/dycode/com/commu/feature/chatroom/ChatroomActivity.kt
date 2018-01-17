package dycode.com.commu.feature.chatroom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import dycode.com.commu.R
import dycode.com.commu.cons.Constant
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commu.data.dto.MessageDto
import dycode.com.commu.utils.TinyDB
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.MyroomModel
import kotlinx.android.synthetic.main.activity_chatroom.*
import kotlinx.android.synthetic.main.base_toolbar.*

/**
 * Created by Asus on 11/17/2017.
 */
class ChatroomActivity : BaseActivity(), ChatroomView, View.OnClickListener{

    companion object {
        val EXTRA_IDROOM = "idroom"
        val EXTRA_OTHER_USERID = "userid"
        val EXTRA_OTHER_PHOTO = "photo"
        val EXTRA_OTHER_FULLNAME = "fullname"
        val EXTRA_OTHER_USERNAME = "username"
        fun start(context: Context, otherUserId: String, myroomModel: MyroomModel) {
            val intent = Intent(context, ChatroomActivity::class.java)
            intent.putExtra(EXTRA_IDROOM, myroomModel.idroom)
            intent.putExtra(EXTRA_OTHER_USERID, otherUserId)
            intent.putExtra(EXTRA_OTHER_PHOTO, myroomModel.photo)
            intent.putExtra(EXTRA_OTHER_FULLNAME, myroomModel.fullname)
            intent.putExtra(EXTRA_OTHER_USERNAME, myroomModel.username)
            context.startActivity(intent)
        }
    }

    val TAG = "ChatroomActivity"

    val firebaseRoot = FirebaseDatabase.getInstance().reference
    val firebaseUsersRef = firebaseRoot.child(Constant.Node.USERS)
    val firebaseMsgRef = firebaseRoot.child(Constant.Node.MESSAGES)
    val firebaseRoomRef = firebaseRoot.child(Constant.Node.CHATROOMS)
    val firebaseTokensRef = firebaseRoot.child(Constant.Node.FCMTOKENS)

    var adapter: ChatroomAdapter? = null
    lateinit var linearLayoutManager: LinearLayoutManager

    var mPresenter: ChatroomPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        base_toolbar_title.setAllCaps(false)
        baseSetupToolbar(true, true, intent.extras[EXTRA_OTHER_FULLNAME].toString())

        tinyDB = TinyDB(this)
        mPresenter = ChatroomPresenter(DataManager(baseGetBearerApi(this)))
        mPresenter?.attachView(this)

        //set other users node to firebase
        checkIfUserExists()

        //reset timestamp
        resetMyTimestamp()

        //setup recyclerview adapter from firebase ui
        linearLayoutManager = LinearLayoutManager(this)
        chatroom_rv.layoutManager = linearLayoutManager
        chatroom_rv.itemAnimator = DefaultItemAnimator()

        val idroom = intent.extras[EXTRA_IDROOM].toString()
        val query = firebaseMsgRef.child(idroom)
        val options = FirebaseRecyclerOptions.Builder<MessageDto>()
                .setQuery(query, MessageDto::class.java)
                .build()
        adapter = ChatroomAdapter(options, tinyDB, Glide.with(this))
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val messageCount = adapter?.itemCount!!
                chatroom_rv.scrollToPosition(messageCount-1)
//                val lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
//                if (lastVisibleItem == -1 ||
//                        (positionStart >= (messageCount?.minus(1)!!) &&
//                                lastVisibleItem == (positionStart - 1))) {
//                    chatroom_rv.scrollToPosition(positionStart)
//                }
            }
        })
        chatroom_rv.adapter = adapter

        chatroom_btn_send.setOnClickListener(this)
        chatroom_image_chooser.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.chatroom_btn_send -> {
                if(chatroom_et_msg.text.toString() == "") return
                val idroom = intent.extras[EXTRA_IDROOM].toString()
                val myUserId = tinyDB?.getString(Constant.Pref.USER_ID)
                val otherUserId = intent.extras[EXTRA_OTHER_USERID].toString()
                val text = chatroom_et_msg.text.toString()
                val map = HashMap<String, Any>()
                map.put("text", text)
                map.put("iduser", myUserId!!)
                map.put("timestamp", ServerValue.TIMESTAMP)

                val randomKey = firebaseMsgRef.child(idroom).push().key

                //send text to message node
                firebaseMsgRef.child(idroom).child(randomKey).ref.setValue(map).addOnCompleteListener {
                    chatroom_et_msg.setText("")
                }

                //send or update the room node
                firebaseRoomRef.child(myUserId).child(idroom).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            val map = HashMap<String, Any>()
                            map.put("lastMessageKey", randomKey)
                            map.put("lastTimestamp", ServerValue.TIMESTAMP)
                            map.put("iduser", otherUserId)
                            map.put("unreadMessage", 0)
                            firebaseRoomRef.child(myUserId).child(idroom).ref.setValue(map).addOnCompleteListener {
                                Log.i(TAG, "myroom updated")
                            }

                            val otherMap = HashMap<String, Any>()
                            otherMap.put("lastMessageKey", randomKey)
                            otherMap.put("lastTimestamp", ServerValue.TIMESTAMP)
                            otherMap.put("iduser", myUserId)
                            otherMap.put("unreadMessage", 1)
                            firebaseRoomRef.child(otherUserId).child(idroom).ref.setValue(otherMap).addOnCompleteListener {
                                Log.i(TAG, "other user room updated")
                            }
                        } else {

                            val map = HashMap<String, Any>()
                            map.put("lastMessageKey", randomKey)
                            map.put("lastTimestamp", ServerValue.TIMESTAMP)
                            firebaseRoomRef.child(myUserId).child(idroom).updateChildren(map).addOnCompleteListener {
                                Log.i(TAG, "myroom updated")
                            }

                            //load unread message count first
                            firebaseRoomRef.child(otherUserId).child(idroom).child("unreadMessage").addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }
                                override fun onDataChange(p0: DataSnapshot?) {
                                    val unread = p0?.value as Long + 1
                                    val otherMap = HashMap<String, Any>()
                                    otherMap.put("lastMessageKey", randomKey)
                                    otherMap.put("lastTimestamp", ServerValue.TIMESTAMP)
                                    otherMap.put("unreadMessage", unread)
                                    firebaseRoomRef.child(otherUserId).child(idroom).updateChildren(otherMap).addOnCompleteListener {
                                        Log.i(TAG, "other user room updated")
                                    }
                                }
                            })
                        }
                    }
                })

                //send notification :)
                firebaseTokensRef.child(otherUserId).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(dataSnapshot.exists()){
                            val token = (dataSnapshot.value as Map<*,*>)["token"].toString()

                            val mToken = token
                            val mTitle = tinyDB?.getString(Constant.Pref.FULLNAME)!!
                            val mMessage = text

                            mPresenter?.pushnotif(mToken, mTitle, mMessage)

                        }
                    }
                })
            }
            R.id.chatroom_image_chooser -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
    }

    override fun onPause() {
        super.onPause()
        adapter?.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        resetMyTimestamp()
        super.onBackPressed()
    }

    /* ===== MvpView implementation ===== */

    override fun checkIfUserExists() {
        val otheruserid = intent.extras[EXTRA_OTHER_USERID].toString()
        firebaseUsersRef.child(otheruserid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    val intent = intent.extras
                    val map = HashMap<String, Any>()
                    map.put("fullname", intent[EXTRA_OTHER_FULLNAME])
                    map.put("photo", intent[EXTRA_OTHER_PHOTO])
                    map.put("username", intent[EXTRA_OTHER_USERNAME])

                    firebaseUsersRef.child(otheruserid).ref.setValue(map)
                }
            }

            override fun onCancelled(p0: DatabaseError?) {
                Log.e(TAG, p0.toString())
            }
        })
    }

    override fun resetMyTimestamp() {
        val idroom = intent.extras[EXTRA_IDROOM].toString()
        val myUserId = tinyDB?.getString(Constant.Pref.USER_ID)
        val map = HashMap<String, Any>()
        map.put("unreadMessage", 0)
        firebaseRoomRef.child(myUserId).child(idroom).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists())
                    firebaseRoomRef.child(myUserId).child(idroom).updateChildren(map)
            }
        })
    }

    override fun showLoading(isShow: Boolean) {
        baseShowLoading(isShow)
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        baseShowError(errorCode, errorMessage)
    }
    /* ===== End of MvpView implementation ===== */

}