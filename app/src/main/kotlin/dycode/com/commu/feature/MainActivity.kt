package dycode.com.commu.feature

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import dycode.com.commu.R
import dycode.com.commu.cons.Constant
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commu.feature.login.LoginActivity
import dycode.com.commu.feature.newmes.NewmesActivity
import dycode.com.commu.feature.profile.ProfileActivity
import dycode.com.commu.feature.search.SearchActivity
import dycode.com.commu.firebase.FirebaseMessagingService
import dycode.com.commu.utils.Bikin
import dycode.com.commu.utils.TinyDB
import dycode.com.commuchatapp.data.DataManager
import dycode.com.commuchatapp.feature.main.MainFragment
import dycode.com.commuchatapp.feature.main.MainFragmentEmpty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.base_toolbar.*

class MainActivity : BaseActivity(), MainView, NavigationView.OnNavigationItemSelectedListener {

    val TAG = "MainActivity"

    var mPresenter: MainPresenter? = null

    var mDrawerLayout: DrawerLayout? = null
    var mDrawerView: NavigationView? = null
    var mProfileImageView: ImageView? = null
    var tvNameDrawer: TextView? = null
    var tvUsernameDrawer: TextView? = null

    val firebaseRoot = FirebaseDatabase.getInstance().reference
    val firebaseUsersRef = firebaseRoot.child(Constant.Node.USERS)
    val firebaseTokensRef = firebaseRoot.child(Constant.Node.FCMTOKENS)

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        baseSetupToolbar(false, true, "Message")

        tinyDB = TinyDB(this)
        mPresenter = MainPresenter(DataManager(baseGetBearerApi(this)))
        mPresenter?.attachView(this)

        //firebase
        checkIfMyNodeExists()

        //CRITICALLY IMPORTANT
        saveMyFCMToken()

        /* DRAWER SETUP*/
        mDrawerLayout = main_drawer_layout
        mDrawerView = main_nav_view
        setupDrawer()

        mDrawerView?.setNavigationItemSelectedListener(this)
        var view: View? = mDrawerView?.getHeaderView(0)
        with(view) {
            mProfileImageView = this?.findViewById(R.id.main_drawer_iv)
            tvNameDrawer = this?.findViewById(R.id.main_drawer_tv_name)
            tvUsernameDrawer = this?.findViewById(R.id.main_drawer_tv_username)
        }
        setupDrawerContent()
        /* END OF DRAWER SETUP */

        //load myroom API to decide which fragment
        mPresenter?.myroom()

    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    override fun onResume() {
        super.onResume()
        setupDrawerContent()
    }

    override fun onBackPressed() {
        if (main_drawer_layout.isDrawerOpen(GravityCompat.START)) {
            main_drawer_layout.closeDrawer(GravityCompat.START)
        } else super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.drawer_profile -> ProfileActivity.start(this)
            R.id.drawer_logout -> confirmLogout()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_search -> SearchActivity.start(this)
            R.id.main_newmessage -> NewmesActivity.start(this)
        }
        return true
    }

    /* ===== MvpView Implementation ===== */

    override fun onMyroomSuccess(roomCount: Int) {
        val fragment: Fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if (roomCount == 0) {
            fragment = MainFragmentEmpty()
            fragmentTransaction.replace(R.id.main_frame, fragment)
            fragmentTransaction.commit()
        } else {
            fragment = MainFragment()
            fragmentTransaction.replace(R.id.main_frame, fragment)
            fragmentTransaction.commit()
        }
    }

    override fun confirmLogout() {
        AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("LOGOUT", { dialogInterface, i ->
                    mPresenter?.logout()
                })
                .setNegativeButton("CANCEL", { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .show()
    }

    override fun onLogoutSuccess() {
        baseSetLogin(false)
        this.finish()
        LoginActivity.start(this)
    }

    override fun checkIfMyNodeExists() {
        val myuserid = tinyDB?.getString(Constant.Pref.USER_ID)
        firebaseUsersRef.child(myuserid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    val map = HashMap<String, Any>()
                    tinyDB?.getString(Constant.Pref.FULLNAME)?.let { map.put("fullname", it) }
                    tinyDB?.getString(Constant.Pref.PHOTO)?.let { map.put("photo", it) }
                    tinyDB?.getString(Constant.Pref.USERNAME)?.let { map.put("username", it) }

                    firebaseUsersRef.child(myuserid).ref.setValue(map)
                }
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    override fun saveMyFCMToken() {
        val token = FirebaseInstanceId.getInstance().token
        Log.i("EWEWEWEWEWE", token)
        val myUserId = tinyDB?.getString(Constant.Pref.USER_ID)
        val map = HashMap<String, String>()
        if (token != null) {
            map.put("token", token)
            firebaseTokensRef.child(myUserId).ref.setValue(map)
        }else{
            Log.i(TAG, "token nya null?")
        }
    }

    override fun showLoading(isShow: Boolean) {
        baseShowLoading(isShow)
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        baseShowError(errorCode, errorMessage)
    }

    /* ===== End of MvpView Implementation ===== */

    private fun setupDrawer() {
        val toggle = object : ActionBarDrawerToggle(
                this, mDrawerLayout, base_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

//            override fun onDrawerOpened(view: View?) {
//                super.onDrawerOpened(view)
//                mDrawerView.setCheckedItem(pos)
//            }
//
//            override fun onDrawerClosed(view: View?) {
//                super.onDrawerClosed(view)
//                mDrawerView.setCheckedItem(pos)
//            }
        }
        mDrawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupDrawerContent() {
        with(tinyDB) {
            val fullname = this?.getString(Constant.Pref.FULLNAME)
            val username = this?.getString(Constant.Pref.USERNAME)
            val photo = this?.getString(Constant.Pref.PHOTO)
            tvNameDrawer?.text = fullname
            tvUsernameDrawer?.text = "@$username"
            Bikin.glide(this@MainActivity, photo, mProfileImageView)
        }
    }
}
