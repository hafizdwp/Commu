package dycode.com.commu.feature.newmes

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dycode.com.commu.R
import dycode.com.commuchatapp.data.model.FriendlistModel
import kotlinx.android.synthetic.main.item_newmes.view.*
import java.util.*

/**
 * Created by Asus on 11/22/2017.
 */
class NewmesAdapter(val activity: NewmesActivity,
                    val glide: RequestManager) : RecyclerView.Adapter<NewmesAdapter.ViewHolder>() {

    var items = ArrayList<FriendlistModel>()
    var itemsFiltered = ArrayList<FriendlistModel>()

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
//        holder?.bind(activity, items[position])
        holder?.bind(activity, itemsFiltered[position], glide)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent?.context).inflate(R.layout.item_newmes, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
//        return items.size
        return itemsFiltered.size
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(activity: NewmesActivity, model: FriendlistModel, glide: RequestManager) {
            with(itemView){
                item_newmes_tv_name.text = model.fullname
                item_newmes_tv_username.text = "@${model.username}"
                glide.load(model.photo)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(item_newmes_iv)
                //Bikin.glide(context, model.photo, item_newmes_iv)

                setOnClickListener{
                    activity.newRoom(model)
                }
            }
        }
    }

    fun addAllItems(item: ArrayList<FriendlistModel>){
        items.addAll(item)
        itemsFiltered.clear()
        itemsFiltered.addAll(items)
        sortItem()
        notifyDataSetChanged()
    }

    fun clearItems(){
        items.clear()
        notifyDataSetChanged()
    }

    private fun sortItem(){
        Collections.sort(itemsFiltered) { obj1, obj2 ->
            val fullname1 = obj1.fullname.toLowerCase()
            val fullname2 = obj2.fullname.toLowerCase()
            return@sort fullname1.compareTo(fullname2)
        }
    }

    fun sortByInput(queryName: String){
        val listFilter = items.filterTo(ArrayList()) { it.fullname.toLowerCase().contains(queryName) }
        itemsFiltered.clear()
        itemsFiltered.addAll(listFilter)
        sortItem()
        notifyDataSetChanged()
    }
}