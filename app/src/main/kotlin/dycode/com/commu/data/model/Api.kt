package dycode.com.commuchatapp.data.model

import dycode.com.commu.cons.Constant
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import rx.Observable

/**
 * Created by Asus on 9/19/2017.
 */
interface Api {

    @FormUrlEncoded
    @POST(Constant.URL.LOGIN)
    fun refreshToken(@Field("grant_type") grant_type: String,
                     @Field("refresh_token") refresh_token: String)
            : Observable<ApiResponse<LoginData>>

    @FormUrlEncoded
    @POST(Constant.URL.LOGIN)
    fun refreshTokena(@Field("grant_type") grant_type: String,
                      @Field("refresh_token") refresh_token: String)
            : Call<ApiResponse<LoginData>>
//    @POST(Constant.URL.LOGIN)
//    fun refreshToken(@Body model: RefreshTokenModel): Observable<ApiResponse<LoginData>>

    @FormUrlEncoded
    @POST(Constant.URL.LOGIN)
    fun login(@Field("username") username: String,
              @Field("password") password: String,
              @Field("grant_type") grant_type: String): Observable<ApiResponse<LoginData>>

//    @POST(Constant.URL.SIGNUP)
//    fun signup(@Body model: SignupModel): Observable<ApiResponse<SignupData>>

    @POST(Constant.URL.SIGNUP)
    fun signup(@Body model: SignupModel): Observable<ApiResponse<String>>

    @GET(Constant.URL.USER)
    fun getProfile(): Observable<ApiResponse<UserData>>

    @POST(Constant.URL.FORPASS)
    fun forpass(@Body model: ForpassModel): Observable<ApiResponse<String>>

    @GET(Constant.URL.LOGOUT)
    fun logout(): Observable<ApiResponse<String>>

    @Multipart
    @POST(Constant.URL.USER)
    fun updateProfile(@PartMap map: HashMap<String, RequestBody>): Observable<ApiResponse<UserData>>

    //khusus update foto profil
    @Multipart
    @POST(Constant.URL.USER)
    fun updatePhotoProfile(@Part image: MultipartBody.Part): Observable<ApiResponse<UserData>>

    @POST(Constant.URL.CHANGEPASS)
    fun changePassword(@Body model: ChangepassModel): Observable<ChangepassResponse>

    @GET(Constant.URL.SEARCH + "/{username}")
    fun search(@Path("username") username: String): Observable<ApiResponse<List<SearchData>>>

    @POST(Constant.URL.FRIENDS)
    fun addFriend(@Body model: FriendModel): Observable<ApiResponse<String>>

    @GET(Constant.URL.FRIENDS+"?search=&offset=0&limit=100")
    fun getFriendlist(): Observable<ApiResponse<List<FriendlistData>>>

    @GET(Constant.URL.FIND_USER + "/{username}")
    fun getFriendData(@Path("username") username: String): Observable<ApiResponse<UserData>>

    @GET(Constant.URL.ROOM)
    fun getMyRoom(): Observable<ApiResponse<List<MyroomData>>>

    @POST(Constant.URL.ROOM)
    fun createNewRoom(@Body model: NewroomModel): Observable<ApiResponse<NewroomData>>

    @POST(Constant.URL.PUSHNOTIF)
    fun pushnotif(@Body model: PushnotifModel): Observable<Meta>

}