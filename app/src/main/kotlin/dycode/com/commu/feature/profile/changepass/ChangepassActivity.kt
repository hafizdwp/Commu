package dycode.com.commu.feature.profile.changepass

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import dycode.com.commu.utils.NetworkError
import dycode.com.commu.R
import dycode.com.commu.utils.TinyDB
import dycode.com.commu.cons.Constant
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commu.feature.base.MvpView
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.ChangepassModel
import dycode.com.commuchatapp.data.model.ChangepassResponse
import kotlinx.android.synthetic.main.activity_changepass.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Asus on 11/17/2017.
 */
class ChangepassActivity : BaseActivity(), MvpView {

    val TAG = "ChangepassActivity"
    lateinit var oldPassword: String

    companion object {
        val EXTRA_OLDPASS = "EXTRA_OLDPASS"
        var BOOL_1: Boolean = false
        var BOOL_2: Boolean = false

        fun start(context: Context, password: String) {
            val intent = Intent(context, ChangepassActivity::class.java)
            intent.putExtra(EXTRA_OLDPASS, password)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepass)

        baseSetupToolbar(true, true, "Change Password")
        tinyDB = TinyDB(this)

        oldPassword = intent.extras[EXTRA_OLDPASS].toString()

        changepass_et.requestFocus()
        changepass_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val string = char.toString().trim()
                BOOL_1 = !string.isEmpty()
                validateButton()
            }

        })
        changepass_et_reconfirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val string = char.toString().trim()
                BOOL_2 = !string.isEmpty()
                validateButton()
            }

        })

        changepass_btn_submit.isEnabled = false
        changepass_btn_submit.setOnClickListener {
            changePassword()
        }
    }

    private fun validateButton() {
        changepass_btn_submit.isEnabled = BOOL_1 && BOOL_2
    }

    private fun changePassword() {
        val newPassword = changepass_et.text.toString()
        val newPasswordReconfirm = changepass_et_reconfirm.text.toString()
        if (newPassword != newPasswordReconfirm) {
            baseShowToast("New password and the reconfirmation do not match!")
        } else {
            showLoading(true)
            val dataManager = DataManager(baseGetBearerApi(this@ChangepassActivity))
            dataManager.changePassword(ChangepassModel(oldPassword, newPassword, newPassword))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response: ChangepassResponse ->
                        Log.i(TAG, response.toString())
                        //set password baru ke sharedpref
                        tinyDB?.putString(Constant.Pref.PASSWORD, newPassword)

                        val message = response.meta.message
                        showLoading(false)
                        onChangePasswordSuccess(message)

                    }, { error: Throwable ->
                        Log.e(TAG, error.toString())
                        val networkError = NetworkError(error)
                        showLoading(false)
                        showError(networkError.appErrorCode, networkError.appErrorMessage)
                    })
        }
    }

    private fun onChangePasswordSuccess(message: String) {
        AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", { dialogInterface, i ->
                    dialogInterface.dismiss()
                    onBackPressed()
                })
                .show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }

    override fun showLoading(isShow: Boolean) {
        baseShowLoading(isShow)
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        baseShowError(errorCode, errorMessage)
    }
}