package dycode.com.commuchatapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Asus on 9/19/2017.
 */
data class BadResponse(@SerializedName("meta") val meta: Meta,
                       @SerializedName("data") val data: String)

data class Meta(@SerializedName("code") val code: String,
                @SerializedName("message") val message: String)

data class Data(@SerializedName("data") val data: String)

data class ApiResponse<T>(
        @SerializedName("meta")
        @Expose
        var meta: Meta,

        @SerializedName("data")
        @Expose
        var data: T
)

//login
data class LoginData(@SerializedName("access_token") val accessToken: String,
                     @SerializedName("refresh_token") val refreshToken: String,
                     @SerializedName("accessTokenExpiresAt") val accessTokenExpiresDate: String,
                     @SerializedName("refreshTokenExpiresAt") val refreshTokenExpiresDate: String)

data class RefreshTokenModel(val grant_type: String,
                             val refresh_token: String)

//data class LoginResponse(@SerializedName("meta") val meta: Meta,
//                         @SerializedName("data") val data: LoginData)

//data class Meta(@SerializedName("code") val code: String,
//                @SerializedName("message") val message: String)

////signup
//data class SignupData(@SerializedName("data") val data: String)
data class SignupModel(val email: String,
                       val username: String,
                       val fullname: String,
                       val password: String,
                       val passwordConfirmation: String)
//
////login
//data class LoginData(@SerializedName("access_token") val token: String)
//
//data class LoginResponse(@SerializedName("meta") val meta: Meta,
//                         @SerializedName("data") val data: LoginData)
//
////forpass

data class ForpassModel(var email: String)
//
////logout
//data class LogoutResponse(@SerializedName("meta") val meta: Meta,
//                          @SerializedName("data") val data: String)
//
////user
data class UserData(@SerializedName("user") val user: UserDataUser,
                    @SerializedName("userdata") val userdata: UserDataUserdata)

data class UserDataUser(@SerializedName("username") val username: String,
                        @SerializedName("email") val email: String,
                        @SerializedName("id_user") val userId: String)

data class UserDataUserdata(@SerializedName("photo") val photo: String?,
                            @SerializedName("fullname") val fullname: String?)

//
////change password
data class ChangepassModel(val oldPassword: String,
                           val newPassword: String,
                           val newPasswordConfirmation: String)

//
data class ChangepassResponse(@SerializedName("meta") val meta: Meta)


//
//search
data class SearchData(@SerializedName("username") val username: String,
                      @SerializedName("email") val email: String,
                      @SerializedName("data") val data: SearchDataUserdata)

data class SearchDataUserdata(@SerializedName("fullname") val fullname: String?,
                              @SerializedName("photo") val photo: String?)

data class SearchModel(val username: String,
                       val email: String,
                       val fullname: String?,
                       var photo: String?)

////friend
data class FriendModel(var user: String)

data class FriendlistModel(val username: String,
                           val email: String,
                           val fullname: String,
                           val photo: String?)

data class FriendlistUserdata(@SerializedName("fullname") val fullname: String,
                              @SerializedName("photo") val photo: String)

data class FriendlistData(@SerializedName("username") val username: String,
                          @SerializedName("email") val email: String,
                          @SerializedName("data") val userdata: FriendlistUserdata)

//
//data class FriendListResponse(@SerializedName("meta") val meta: Meta,
//                              @SerializedName("data") val data: List<FriendListData>)
//
////chat
//data class ChatModel(val id_user: String,
//                     val text: String?,
//                     val image: String?)

//
data class OtherUserModel(val name: String?,
                          val photo: String?)

//
////room
//data class RoomDataUser(@SerializedName("id_user") val idUser: String,
//                        @SerializedName("username") val username: String)
//
//data class RoomData(@SerializedName("type") val type: String,
//                    @SerializedName("id_room") val idroom: String,
//                    @SerializedName("photo") val photo: String?,
//                    @SerializedName("date") val date: String,
//                    @SerializedName("user") val user: ArrayList<RoomDataUser>)
//
data class NewroomModel(val room_type: String,
                        val user: String)

data class NewroomData(@SerializedName("id_room") val idroom: String,
                       @SerializedName("type") val type: String,
                       @SerializedName("date") val date: String,
                       @SerializedName("user") val user: List<MyroomDataUser>)

data class NewroomResponse(@SerializedName("meta") val meta: Meta,
                           @SerializedName("data") val data: NewroomData)

data class MyroomData(@SerializedName("id_room") val idroom: String,
                      @SerializedName("type") val type: String,
                      @SerializedName("date") val date: String,
                      @SerializedName("user") val user: ArrayList<MyroomDataUser>)

data class MyroomDataUser(@SerializedName("id_user") val idUser: String,
                          @SerializedName("username") val username: String)

data class MyroomModel(val idroom: String,
                       val username: String,
                       var photo: String?,
                       var fullname: String?)

data class PushnotifModel(val token: String,
                          val title: String,
                          val message: String)
