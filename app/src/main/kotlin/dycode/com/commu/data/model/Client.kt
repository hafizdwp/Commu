package dycode.com.commuchatapp.data.model

import dycode.com.commu.utils.RxErrorHandlingCallAdapterFactory
import dycode.com.commu.cons.Constant
import dycode.com.commu.feature.base.BaseActivity
import dycode.com.commu.feature.base.BaseFragment
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/*
 * Created by Asus on 9/19/2017.
 */
class Client {
    companion object {
        fun getBasicRetrofit(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(Constant.URL.BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(getBasicAuth())
                    .build()
        }

        fun getBearerRetrofit(activity: BaseActivity, token: String): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(Constant.URL.BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(getBearerAuth(activity, token))
                    .build()
        }

        fun getBearerRetrofit(baseFragment: BaseFragment, token: String): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(Constant.URL.BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(getBearerAuth(baseFragment, token))
                    .build()
        }

        //khusus NewmesActivity
        fun getBearerRetrofitNewmes(activity: BaseActivity, token: String): Retrofit{
            return Retrofit.Builder()
                    .baseUrl(Constant.URL.BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                    .client(getBearerAuth(activity, token))
                    .build()
        }

        private fun getBasicAuth(): OkHttpClient? {
            val credentials = Credentials.basic(
                    Constant.CREDENTIAL_USERNAME,
                    Constant.CREDENTIAL_PASSWORD)
            var builder: OkHttpClient.Builder = OkHttpClient.Builder()
            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                        .addHeader("Authorization", credentials)
                        .build()
                chain.proceed(request)
            }
            return builder.build()
        }

        private fun getBearerAuth(activity: BaseActivity, token: String): OkHttpClient {
            val bearer = "Bearer " + token

            val builder = OkHttpClient.Builder()

            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                        .addHeader("Authorization", bearer)
                        .build()
                return@addInterceptor chain.proceed(request)
            }

            builder.authenticator { route, response ->

                val retrofit = Retrofit.Builder()
                        .baseUrl(Constant.URL.BASE)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getBasicAuth())
                        .build()

                val api = retrofit.create(Api::class.java)
                val refreshToken = activity.baseGetRefreshToken()
                val refreshTokenResponse: Response<ApiResponse<LoginData>> =
                        api.refreshTokena(Constant.GRANT_TYPE_REFTOKEN, refreshToken).execute()

                if(refreshTokenResponse.isSuccessful){
                    val data = refreshTokenResponse.body()?.data!!
                    val newToken = data.accessToken
                    val newRefreshToken = data.refreshToken
                    activity.baseSetToken(newToken)
                    activity.baseSetRefreshToken(newRefreshToken)

                    return@authenticator response.request().newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .build()
                }else{
                    return@authenticator null
                }
            }

            return builder.build()
        }

        private fun getBearerAuth(baseFragment: BaseFragment, token: String): OkHttpClient {
            val bearer = "Bearer " + token

            val builder = OkHttpClient.Builder()

            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                        .addHeader("Authorization", bearer)
                        .build()
                return@addInterceptor chain.proceed(request)
            }

            builder.authenticator { route, response ->

                val retrofit = Retrofit.Builder()
                        .baseUrl(Constant.URL.BASE)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getBasicAuth())
                        .build()

                val api = retrofit.create(Api::class.java)
                val refreshToken = baseFragment.baseGetRefreshToken()
                val refreshTokenResponse: Response<ApiResponse<LoginData>> =
                        api.refreshTokena(Constant.GRANT_TYPE_REFTOKEN, refreshToken).execute()

                if(refreshTokenResponse.isSuccessful){
                    val data = refreshTokenResponse.body()?.data!!
                    val newToken = data.accessToken
                    val newRefreshToken = data.refreshToken
                    baseFragment.baseSetToken(newToken)
                    baseFragment.baseSetRefreshToken(newRefreshToken)

                    return@authenticator response.request().newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .build()
                }else{
                    return@authenticator null
                }
            }

            return builder.build()
        }
    }
}
