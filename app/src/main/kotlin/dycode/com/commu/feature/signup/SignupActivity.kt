package dycode.com.commu.feature.signup

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import dycode.com.commu.R
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commuchatapp.data.DataManager
import kotlinx.android.synthetic.main.activity_signup.*

/**
 * Created by Asus on 11/14/2017.
 */
class SignupActivity: BaseActivity(), SignupView, View.OnClickListener {

    companion object {
        fun start(context: Context){
            context.startActivity(Intent(context, SignupActivity::class.java))
        }
    }

    var mPresenter: SignupPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mPresenter = SignupPresenter(DataManager(baseGetBasicApi()))
        mPresenter?.attachView(this)

        val tvBacktologin: TextView = findViewById(R.id.custom_divider_text)
        tvBacktologin.text = "Have an account already?"
        tvBacktologin.setTextColor(Color.parseColor("#ffffff"))

        signup_btn.setOnClickListener(this)
        signup_tv_backtologin.paintFlags = signup_tv_backtologin.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        signup_tv_backtologin.setOnClickListener(this)

        //signup_et_email.setText("qwe@gmail.com")
        //signup_et_username.setText("qwe")
        //signup_et_password.setText("123456")
    }

    override fun onClick(view: View?) {
        when(view){
            signup_btn -> {
                val email = signup_et_email.text.toString()
                val username = signup_et_username.text.toString()
                val fullname = signup_et_fullname.text.toString()
                val password = signup_et_password.text.toString()
                mPresenter?.signup(email, username, fullname, password)
            }
            signup_tv_backtologin -> onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    /* ===== MvpView Implementation =====*/

    override fun onSignupSuccess(message: String) {
        baseShowSuccess(message)
    }

    override fun showLoading(isShow: Boolean) {
        baseShowLoading(isShow)
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        baseShowError(errorCode, errorMessage)
    }

    /* ===== End of MvpView Implementation =====*/

}