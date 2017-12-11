package dycode.com.commu.feature

import android.os.Bundle
import android.os.Handler
import dycode.com.commu.R
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commu.feature.login.LoginActivity

/**
 * Created by Asus on 9/19/2017.
 */
class Splash : BaseActivity() {

    var SPLASH_TIME: Long = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({

            LoginActivity.start(this)
            this.finish()
//            if(baseCheckLogin()){
//                MainActivity.startActivity(this)
//                this.finish()
//            }else{
//                LoginActivity.startActivity(this)
//                this.finish()
//            }

        }, SPLASH_TIME)
    }


}