package dycode.com.commu.feature.forpass

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import dycode.com.commu.R
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commu.feature.login.LoginActivity
import dycode.com.commuchatapp.data.DataManager
import java.net.HttpURLConnection

/**
 * Created by Asus on 11/16/2017.
 */
class ForpassDialog(val context: Context, val activity: LoginActivity) : BaseActivity(), ForpassView, View.OnClickListener {

    var mPresenter: ForpassPresenter? = null
    var mProgressDialog: ProgressDialog? = null
    var mToast: Toast? = null
    lateinit var mDialog: AlertDialog
    lateinit var mDialogBuilder: AlertDialog.Builder
    lateinit var mInflater: LayoutInflater
    lateinit var mView: View

    var etEmail: EditText? = null
    var btnSubmit: Button? = null
    var btnCancel: Button? = null

    fun show() {
        mPresenter = ForpassPresenter(DataManager(baseGetBasicApi()))
        mPresenter?.attachView(this)

        mInflater = activity.layoutInflater
        mView = mInflater.inflate(R.layout.dialog_forpass, null)
        mDialogBuilder = AlertDialog.Builder(context)
        mDialogBuilder.setView(mView)

        mDialog = mDialogBuilder.create()

        etEmail = mView.findViewById(R.id.forpass_et)
        btnSubmit = mView.findViewById(R.id.forpass_btn_submit)
        btnCancel = mView.findViewById(R.id.forpass_btn_cancel)

        btnSubmit?.setOnClickListener(this)
        btnCancel?.setOnClickListener(this)

        mDialog.show()

        //etEmail?.setText("hafizdwp@gmail.com")
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.forpass_btn_submit -> {
                var email = etEmail?.text.toString()
                mPresenter?.forpass(email);
            }
            R.id.forpass_btn_cancel -> {
                mDialog.dismiss()
                mPresenter?.detachView()
            }
        }
    }

    /* ===== MvpView Implementation =====*/

    override fun onForpassSuccess(message: String) {
        showAlertDialog("Success", message)
    }

    override fun showLoading(isShow: Boolean) {
        if (isShow) {
            mProgressDialog = ProgressDialog(context)
            mProgressDialog?.setCancelable(false)
            mProgressDialog?.setMessage("Loading...")
            mProgressDialog?.show()
        } else {
            mProgressDialog?.dismiss()
        }
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        when (errorCode) {
            HttpURLConnection.HTTP_BAD_REQUEST -> showToast(errorMessage)
            else -> showToast("$errorCode $errorMessage")
        }
    }

    /* ===== End of MvpView Implementation =====*/

    private fun showToast(message: String) {
        if (mToast != null) mToast?.cancel()
        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        mToast?.show()
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .show()
    }
}