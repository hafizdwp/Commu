package dycode.com.commu.feature.base

import android.app.ProgressDialog
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.widget.Toast
import dycode.com.commu.utils.TinyDB
import dycode.com.commu.cons.Constant
import dycode.com.commuchatapp.data.model.Api
import dycode.com.commuchatapp.data.model.Client
import java.net.HttpURLConnection

/**
 * Created by Asus on 10/31/2017.
 */
open class BaseFragment : Fragment() {

    private lateinit var mProgressDialog: ProgressDialog
    private var mToast: Toast? = null

    var tinyDB: TinyDB? = null

    fun basePutList(key: String, list: ArrayList<String>)
            = tinyDB?.putListString(key, list)

    fun baseGetList(key: String): ArrayList<String>?
            = tinyDB?.getListString(key)

    /* API === */
    fun baseGetBasicApi(): Api = Client.getBasicRetrofit().create(Api::class.java)

    fun baseGetBearerApi(baseFragment: BaseFragment): Api =
            Client.getBearerRetrofit(baseFragment, baseGetToken()).create(Api::class.java)

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

    fun baseShowLoading(isShow: Boolean) {
        if (isShow) {
            mProgressDialog = ProgressDialog(context)
            mProgressDialog.setCancelable(false)
            mProgressDialog.setMessage("Loading...")
            mProgressDialog.show()
        } else {
            mProgressDialog.dismiss()
        }
    }

    fun baseShowToast(message: String) {
        if (mToast != null) mToast?.cancel()
        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        mToast?.show()
    }

    fun baseShowAlertDialog(title: String, message: String) {
        AlertDialog.Builder(context)
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
            HttpURLConnection.HTTP_BAD_REQUEST -> baseShowToast(errorMessage)
            HttpURLConnection.HTTP_NOT_FOUND -> baseShowToast(errorMessage)
            else -> baseShowToast("$errorCode $errorMessage")
        }
    }
}