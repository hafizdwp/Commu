package dycode.com.commu.feature.search

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dycode.com.commu.utils.Bikin
import dycode.com.commu.R
import dycode.com.commuchatapp.data.model.SearchModel
import dycode.com.commuchatapp.feature.search.popup.SearchPopupDialog
import kotlinx.android.synthetic.main.item_search.view.*
import java.util.*

/**
 * Created by Asus on 11/20/2017.
 */
class SearchAdapter(val activity: SearchActivity,
                    val glide: RequestManager) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var items = ArrayList<SearchModel>()

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(activity, items[position], glide)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view: View = LayoutInflater.from(parent?.context).inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(activity: SearchActivity, searchModel: SearchModel, glide: RequestManager) {
            with(itemView) {
                item_search_tv_name.text = searchModel.fullname ?: "unnamed"
                item_search_tv_username.text = "@${searchModel.username}"
                if (searchModel.photo != null)
                    glide.load(searchModel.photo)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(item_search_iv)
                //Bikin.glide(this.context, searchModel.photo, item_search_iv)
                else
                    glide.load(R.drawable.dummy_profile)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(item_search_iv)
                //Bikin.glide(this.context, R.drawable.dummy_profile, item_search_iv)

                //on click listener
                setOnClickListener {
                    startSearchPopupDialog(
                            activity,
                            context,
                            searchModel.fullname,
                            searchModel.username
                    )
                }
            }
        }

        private fun startSearchPopupDialog(activity: SearchActivity, context: Context, fullname: String?, username: String) {
            fullname?.let { SearchPopupDialog(activity, context).show(it, username) }
        }
    }

    fun addAllItem(item: ArrayList<SearchModel>) {
        items.addAll(item)
        sortItem()
        notifyDataSetChanged()
    }

    fun clearItem() {
        items.clear()
        notifyDataSetChanged()
    }

    private fun sortItem() {
        Collections.sort(items) { obj1: SearchModel, obj2: SearchModel ->
            val username1 = obj1.username
            val username2 = obj2.username

            return@sort username1.compareTo(username2)
//            val fullname1 = obj1.fullname
//            val fullname2 = obj2.fullname
//            return@sort fullname1!!.compareTo(fullname2!!)
        }
    }
}