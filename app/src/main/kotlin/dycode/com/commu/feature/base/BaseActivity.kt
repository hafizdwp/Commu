package dycode.com.commu.feature.base

import android.app.ProgressDialog
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import dycode.com.commu.utils.TinyDB
import dycode.com.commu.cons.Constant
import dycode.com.commuchatapp.data.model.Api
import dycode.com.commuchatapp.data.model.Client
import kotlinx.android.synthetic.main.base_toolbar.*
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_NOT_FOUND

/**
 * Created by Asus on 9/19/2017.
 */
open class BaseActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: ProgressDialog
    private var mToast: Toast? = null

    var tinyDB: TinyDB? = null

    fun basePutList(key: String, list: ArrayList<String>)
            = tinyDB?.putListString(key, list)

    fun baseGetList(key: String): ArrayList<String>?
            = tinyDB?.getListString(key)

    /* API === */
    fun baseGetBasicApi(): Api = Client.getBasicRetrofit().create(Api::class.java)

    fun baseGetBearerApi(activity: BaseActivity): Api =
            Client.getBearerRetrofit(activity, baseGetToken()).create(Api::class.java)

    /* TOKEN === */
    fun baseSetToken(token: String) {
        tinyDB?.putString(Constant.Pref.TOKEN_ACCESS, token)
    }

    fun baseSetRefreshToken(refreshToken: String){
        tinyDB?.putString(Constant.Pref.TOKEN_REFRESH, refreshToken)
    }

    fun baseGetToken(): String {
        return tinyDB?.getString(Constant.Pref.TOKEN_ACCESS).toString()
    }

    fun baseGetRefreshToken(): String{
        return tinyDB?.getString(Constant.Pref.TOKEN_REFRESH).toString()
    }

    /* LOGIN === */
    fun baseSetLogin(isLogin: Boolean){
        tinyDB?.putBoolean(Constant.Pref.BOOL_LOGIN, isLogin)
    }

    fun baseGetLogin(): Boolean? {
        return tinyDB?.getBoolean(Constant.Pref.BOOL_LOGIN)
    }

    /* TOOLBAR === */
    fun baseSetupToolbar(isHaveArrow: Boolean, isHaveTitle: Boolean, title: String) {
        setSupportActionBar(base_toolbar)
        if (isHaveArrow) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }

        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (isHaveTitle) base_toolbar_title.text = title
    }

    fun baseShowLoading(isShow: Boolean) {
        if (isShow) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog.setCancelable(false)
            mProgressDialog.setMessage("Loading...")
            mProgressDialog.show()
        } else {
            mProgressDialog.dismiss()
        }
    }

    fun baseShowToast(message: String) {
        if (mToast != null) mToast?.cancel()
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        mToast?.show()
    }

    fun baseShowAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .show()
    }

    fun baseShowSuccess(message: String) = baseShowAlertDialog("Success", message)

    fun baseShowError(errorCode: Int, errorMessage: String) {
        when(errorCode){
            HTTP_BAD_REQUEST -> baseShowToast(errorMessage)
            HTTP_NOT_FOUND -> baseShowToast(errorMessage)
            else -> baseShowToast("$errorCode $errorMessage")
        }
    }
}