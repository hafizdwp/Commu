package dycode.com.commu.feature.search

import dycode.com.commu.feature.base.MvpView
import dycode.com.commuchatapp.data.model.SearchModel

/**
 * Created by Asus on 11/20/2017.
 */
interface SearchPiew : MvpView {

    fun onSearchSuccess(arrSearchModel: ArrayList<SearchModel>)
    fun onAddFriendSuccess(name: String)
}