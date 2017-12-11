
package dycode.com.commu.feature.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.FirebaseDatabase
import dycode.com.commu.utils.Bikin
import dycode.com.commu.R
import dycode.com.commu.utils.TinyDB
import dycode.com.commu.cons.Constant
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commu.feature.profile.changepass.ChangepassActivity
import dycode.com.commu.feature.profile.edit.ProfileEditActivity
import dycode.com.commu.feature.profile.friends.FriendsActivity
import dycode.com.commuchatapp.data.DataManager
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by Asus on 11/16/2017.
 */
class ProfileActivity : BaseActivity(), ProfileView, View.OnClickListener {

    var mPresenter: ProfilePresenter? = null

    val firebaseRoot = FirebaseDatabase.getInstance().reference
    val firebaseUsersRef = firebaseRoot.child(Constant.Node.USERS)

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ProfileActivity::class.java))
        }

        val REQUEST_CODE = 1
        val PERMISSIONS_STORAGE =
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun verifyStorage(activity: AppCompatActivity) {
        val writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_CODE
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        baseSetupToolbar(true, true, "Profile")
        verifyStorage(this)

        tinyDB = TinyDB(this)
        mPresenter = ProfilePresenter(DataManager(baseGetBearerApi(this)))
        mPresenter?.attachView(this)

        profile_tv_changephoto.paintFlags = profile_tv_changephoto.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        profile_tv_changephoto.setOnClickListener(this)

        profile_cv_username.setOnClickListener(this)
        profile_cv_fullname.setOnClickListener(this)
        profile_cv_friends.setOnClickListener(this)
        profile_btn_changepass.setOnClickListener(this)

        //load content from sharedpref
        with(tinyDB) {
            profile_tv_email.text = this?.getString(Constant.Pref.EMAIL)
            profile_tv_username.text = this?.getString(Constant.Pref.USERNAME)
            profile_tv_fullname.text = this?.getString(Constant.Pref.FULLNAME)
            Bikin.glide(
                    this@ProfileActivity,
                    this?.getString(Constant.Pref.PHOTO),
                    profile_civ)
        }

        refreshContent()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.profile_cv_username ->
                ProfileEditActivity.start(
                        this, Constant.Pref.USERNAME, "Username", profile_tv_username.text.toString())
            R.id.profile_cv_fullname ->
                ProfileEditActivity.start(
                        this, Constant.Pref.FULLNAME, "Fullname", profile_tv_fullname.text.toString())
            R.id.profile_cv_friends ->
                FriendsActivity.start(this)
            R.id.profile_btn_changepass -> confirmChangePassword()
            R.id.profile_tv_changephoto -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 100)
            }
        }
    }

    //khusus image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        baseShowToast("$requestCode, $resultCode, $data")

        if (data != null) {
            val uriSelectedImage: Uri? = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver?.query(uriSelectedImage, filePathColumn, null, null, null) ?: return

            cursor?.moveToFirst()

            val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
            val filePath = columnIndex?.let { cursor?.getString(it) }

            cursor?.close()

            val file = File(filePath)
            val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestBody)

            mPresenter?.updatePhotoProfile(body)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    override fun onResume() {
        super.onResume()
        refreshContent()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }

    /* ===== MvpView Implementation ===== */

    override fun refreshContent() {
        val status: Boolean = tinyDB?.getBoolean(Constant.Pref.BOOL_PROFILE)!!
        if (status) {
            val field = tinyDB?.getString(Constant.Pref.UPDATE_PROFILE)
            when (field) {
                Constant.Pref.USERNAME -> profile_tv_username.text = tinyDB?.getString(Constant.Pref.USERNAME)
                Constant.Pref.FULLNAME -> profile_tv_fullname.text = tinyDB?.getString(Constant.Pref.FULLNAME)
                Constant.Pref.PHOTO -> Bikin.glide(
                        this@ProfileActivity,
                        tinyDB?.getString(Constant.Pref.PHOTO),
                        profile_civ)
            }

            //refresh content finished
            tinyDB?.putBoolean(Constant.Pref.BOOL_PROFILE, false)
        }
    }

    override fun confirmChangePassword() {
        val myPassword = tinyDB?.getString(Constant.Pref.PASSWORD)

        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_changepass, null)
        val builder = (AlertDialog.Builder(this)).setView(view)
        val dialog = builder.create()

        val dialog_btnSubmit: Button = view.findViewById(R.id.dialog_changepass_btn_submit)
        val dialog_btnCancel: Button = view.findViewById(R.id.dialog_changepass_btn_cancel)
        val dialog_et: EditText = view.findViewById(R.id.dialog_changepass_et)
        dialog_btnSubmit.setOnClickListener {
            var password = dialog_et.text.toString()
            showLoading(true)
            Handler().postDelayed({
                showLoading(false)
                if (password == myPassword) {
                    ChangepassActivity.start(this, password)
                    dialog.dismiss()
                } else {
                    baseShowToast("Password do not match!")
                }
            }, 1000)
        }
        dialog_btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onUpdatePhotoProfileSuccess(message: String, photoUrl: String) {
        baseShowSuccess(message)
        tinyDB?.putString(Constant.Pref.PHOTO, photoUrl)
        tinyDB?.putString(Constant.Pref.UPDATE_PROFILE, Constant.Pref.PHOTO)
        tinyDB?.putBoolean(Constant.Pref.BOOL_PROFILE, true)
        refreshContent()

        //update the photourl to firebase
        val myUserId = tinyDB?.getString(Constant.Pref.USER_ID)
        val map = HashMap<String, Any>()
        map.put("photo", photoUrl)
        firebaseUsersRef.child(myUserId).updateChildren(map)
    }

    override fun showLoading(isShow: Boolean) {
        baseShowLoading(isShow)
    }

    override fun showError(errorCode: Int, errorMessage: String) {
        baseShowError(errorCode, errorMessage)
    }

    /* ===== End of MvpView Implementation ===== */

}