package dycode.com.commu.feature.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import dycode.com.commu.R
import dycode.com.commu.utils.TinyDB
import dycode.com.commu.cons.Constant
import dycode.com.commu.feature.MainActivity
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commu.feature.forpass.ForpassDialog
import dycode.com.commu.feature.signup.SignupActivity
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.data.model.*
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by Asus on 11/14/2017.
 */
class LoginActivity : BaseActivity(), LoginView, View.OnClickListener {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    var mPresenter: LoginPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tinyDB = TinyDB(this)
        mPresenter = LoginPresenter(DataManager(baseGetBasicApi()))
        mPresenter?.attachView(this)

        //check if was logged
        if(baseGetLogin() == true){
            gotoMain()
        }

        login_et_username.setText("hafizdwp")
        login_et_password.setText("123456")

        login_btn.setOnClickListener(this)
        login_tv_forpass.setOnClickListener(this)
        login_tv_signup.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view){
            login_btn -> {
                val username = login_et_username.text.toString()
                val password = login_et_password.text.toString()
                mPresenter?.login(username, password)
            }
            login_tv_forpass -> ForpassDialog(this, this).show()
            login_tv_signup -> SignupActivity.start(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    /* ===== MvpView Implementation =====*/

    override fun onLoginSuccess(token: String, refreshToken: String) {
        baseSetToken(token)
        baseSetRefreshToken(refreshToken)

        mPresenter?.detachView()
        mPresenter = LoginPresenter(DataManager(baseGetBearerApi(this)))
        mPresenter?.attachView(this)
        mPresenter?.getMyProfile()

        mPresenter = LoginPresenter(DataManager(baseGetBasicApi()))
        mPresenter?.attachView(this)
    }

    override fun onGetMyProfileSuccess(userData: UserData) {
        with(userData.user){
            tinyDB?.putString(Constant.Pref.USERNAME, username)
            tinyDB?.putString(Constant.Pref.EMAIL, email)
            tinyDB?.putString(Constant.Pref.USER_ID, userId)
        }
        with(userData.userdata){
            //untuk sekarang
            tinyDB?.putString(Constant.Pref.FULLNAME, fullname ?: "unnamed")
            tinyDB?.putString(Constant.Pref.PHOTO, photo ?: "")
        }

        //other task
        tinyDB?.putString(Constant.Pref.PASSWORD, login_et_password.text.toString())
        tinyDB?.putBoolean(Constant.Pref.BOOL_PROFILE, false)
        tinyDB?.putBoolean(Constant.Pref.BOOL_LOGIN, true)

        gotoMain()
    }

    override fun gotoMain() {
        MainActivity.start(this)
        this.finish()
    }

    override fun showLoading(isShow: Boolean) {
        baseShowLoading(isShow)
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        baseShowError(errorCode, errorMessage)
    }

    /* ===== End of MvpView Implementation =====*/
}