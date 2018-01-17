package dycode.com.commu.feature.profile.friends

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dycode.com.commu.utils.Bikin
import dycode.com.commu.R
import dycode.com.commuchatapp.data.model.FriendlistModel
import kotlinx.android.synthetic.main.item_friends.view.*
import java.util.*

/**
 * Created by Asus on 11/17/2017.
 */
class FriendsAdapter(val glide: RequestManager) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    var items = ArrayList<FriendlistModel>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent?.context).inflate(R.layout.item_friends, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(items[position], glide)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){
        fun bind(friendlistModel: FriendlistModel, glide: RequestManager){
            with(itemView){
                item_friends_username.text = "@${friendlistModel.username}"
                item_friends_fullname.text = friendlistModel.fullname

                if(friendlistModel.photo != null){
                    glide.load(friendlistModel.photo)
                            .into(item_friends_iv)
                }else{
                    glide.load(R.drawable.profile_grey)
                            .into(item_friends_iv)
                }

                //Bikin.glide(this.context, friendlistModel.photo, item_friends_iv)
            }
        }
    }

    fun addAllItem(item: ArrayList<FriendlistModel>){
        items.addAll(item)
        sortItem()
        notifyDataSetChanged()
    }

    fun clearItem(){
        items.clear()
        notifyDataSetChanged()
    }

    private fun sortItem(){
        Collections.sort(items) { obj1, obj2 ->
            val username1 = obj1.username
            val username2 = obj2.username
            return@sort username1.compareTo(username2)
        }
    }
}