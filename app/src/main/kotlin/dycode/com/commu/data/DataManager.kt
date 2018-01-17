package dycode.com.commuchatapp.data

import dycode.com.commuchatapp.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.Observable

/**
 * Created by Asus on 9/19/2017.
 */
class DataManager(val api: Api) {

    fun login(username: String,
              password: String,
              grantType: String): Observable<ApiResponse<LoginData>>
            = api.login(username, password, grantType)

//    fun refreshToken(model: RefreshTokenModel): Observable<ApiResponse<LoginData>>
//            = api.refreshToken(model)
//    fun refreshToken(grantType: String, refreshToken: String): Observable<ApiResponse<LoginData>>
//            = api.refreshToken(grantType, refreshToken)

    fun getProfile(): Observable<ApiResponse<UserData>>
            = api.getProfile()

    fun signup(model: SignupModel): Observable<ApiResponse<String>>
            = api.signup(model)

    fun forpass(model: ForpassModel): Observable<ApiResponse<String>>
            = api.forpass(model)
//
//    fun getProfile(): Observable<UserResponse>
//            = api.getProfile()
//
    fun updateProfile(map: HashMap<String, RequestBody>): Observable<ApiResponse<UserData>>
            = api.updateProfile(map)
//
    fun updatePhotoProfile(image: MultipartBody.Part) : Observable<ApiResponse<UserData>>
            = api.updatePhotoProfile(image)
//
    fun logout(): Observable<ApiResponse<String>>
            = api.logout()
//
    fun changePassword(model: ChangepassModel): Observable<ChangepassResponse>
            = api.changePassword(model)
//
    fun search(username: String): Observable<ApiResponse<List<SearchData>>>
            = api.search(username)
//
    fun addFriend(model: FriendModel): Observable<ApiResponse<String>>
            = api.addFriend(model)
//
    fun getFriendlist(): Observable<ApiResponse<List<FriendlistData>>>
            = api.getFriendlist()
//
    fun getFriendData(username: String): Observable<ApiResponse<UserData>>
            = api.getFriendData(username)
//
    fun getMyRoom(): Observable<ApiResponse<List<MyroomData>>> = api.getMyRoom()
//
    fun createNewRoom(model: NewroomModel): Observable<ApiResponse<NewroomData>>
            = api.createNewRoom(model)

    fun pushnotif(model: PushnotifModel): Observable<Meta> = api.pushnotif(model)
}