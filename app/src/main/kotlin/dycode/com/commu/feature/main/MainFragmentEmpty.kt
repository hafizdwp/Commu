package dycode.com.commuchatapp.feature.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dycode.com.commu.R
import dycode.com.commu.feature.base.BaseFragment

/**
 * Created by Asus on 10/31/2017.
 */
class MainFragmentEmpty : BaseFragment() {

    var mView: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater?.inflate(R.layout.fragment_main_empty, container, false)
        return mView
    }
}