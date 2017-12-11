package dycode.com.commu.feature.profile.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import dycode.com.commu.R
import dycode.com.commu.utils.TinyDB
import dycode.com.commu.cons.Constant
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commuchatapp.data.DataManager
import kotlinx.android.synthetic.main.activity_profile_edit.*

/**
 * Created by Asus on 11/16/2017.
 */
class ProfileEditActivity : BaseActivity(), ProfileEditView, View.OnClickListener {


    var mPresenter: ProfileEditPresenter? = null
    var field: String? = null
    var title: String? = null
    var currentValue: String? = null

    val firebaseRoot = FirebaseDatabase.getInstance().reference
    val firebaseUsersRef = firebaseRoot.child(Constant.Node.USERS)

    companion object {
        val EXTRA_FIELD = "EXTRA_FIELD"
        val EXTRA_TITLE = "EXTRA_TITLE"
        val EXTRA_CURRENTVALUE = "EXTRA_CURRENTVALUE"
        fun start(context: Context, field: String, title: String, currentValue: String) {
            val intent = Intent(context, ProfileEditActivity::class.java)
            intent.putExtra(EXTRA_FIELD, field)
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_CURRENTVALUE, currentValue)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        field = intent.extras[EXTRA_FIELD].toString()
        title = intent.extras[EXTRA_TITLE].toString()
        currentValue = intent.extras[EXTRA_CURRENTVALUE].toString()

        baseSetupToolbar(true, true, title!!)

        tinyDB = TinyDB(this)
        mPresenter = ProfileEditPresenter(DataManager(baseGetBearerApi(this)))
        mPresenter?.attachView(this)

        profile_edit_btn_save.isEnabled = false
        profile_edit_btn_save.setOnClickListener(this)

        profile_edit_et.hint = field
        profile_edit_et.setText(currentValue)
        profile_edit_et.requestFocus()
        profile_edit_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var string = char.toString().trim()
                profile_edit_btn_save.isEnabled = !(string.isEmpty() || string == currentValue)
            }
        })
        profile_edit_tv.text = "current: $currentValue"
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    override fun onClick(p0: View?) {
        confirmUpdate()
    }

    /* ===== MvpView Implementation ===== */

    override fun confirmUpdate() {
        val value = profile_edit_et.text.toString()
        if (field == "username") {
            AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to update your username?")
                    .setPositiveButton("UPDATE", { dialogInterface, i ->
                        executeUpdate(field!!, value)
                        dialogInterface.dismiss()
                    })
                    .setNegativeButton("CANCEL", { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }).show()
        } else {
            executeUpdate(field!!, value)
        }
    }

    override fun onUpdateSuccess(message: String, field: String, value: String) {
        baseShowSuccess(message)
        tinyDB?.putBoolean(Constant.Pref.BOOL_PROFILE, true)
        tinyDB?.putString(Constant.Pref.UPDATE_PROFILE, field)
        tinyDB?.putString(field, value)

        //update the updated value to firebase users
        val myUserId = tinyDB?.getString(Constant.Pref.USER_ID)
        val map = HashMap<String, Any>()
        map.put(field, value)
        firebaseUsersRef.child(myUserId).updateChildren(map)
    }

    override fun executeUpdate(field: String, value: String) {
        mPresenter?.update(field, value)
    }

    override fun showLoading(isShow: Boolean) {
        baseShowLoading(isShow)
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        baseShowError(errorCode, errorMessage)
    }

    /* ===== End of MvpView Implementation ===== */
}