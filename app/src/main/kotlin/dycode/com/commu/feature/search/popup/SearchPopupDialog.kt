package dycode.com.commuchatapp.feature.search.popup

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import dycode.com.commu.R
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commu.feature.search.SearchActivity

/**
 * Created by Asus on 10/9/2017.
 */
public class SearchPopupDialog(val activity: SearchActivity,
                               val context: Context) {

    private var dialog: AlertDialog? = null
    private var dialogBuilder: AlertDialog.Builder? = null
    var view: View? = null

    lateinit var tvPopupMessage: TextView
    lateinit var btnAddFriend: LinearLayout


    fun show(name: String, username: String?) {

        view = activity.layoutInflater.inflate(R.layout.dialog_popup, null)
        dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder?.setView(view)
        dialog = dialogBuilder?.create()
        dialog?.setCanceledOnTouchOutside(true)

        tvPopupMessage = view?.findViewById(R.id.popup_tv_message)!!
        var first = "Add"
        var second = "<font color='#01579b'> $name </font>"
        var last = "as a friend?"
        tvPopupMessage.text = Html.fromHtml("$first $second $last")

        btnAddFriend = view?.findViewById(R.id.popup_btn_add)!!
        btnAddFriend.setOnClickListener {
            activity.mPresenter?.addFriend(username, name)
            dialog?.dismiss()
        }

        dialog?.show()
    }
}