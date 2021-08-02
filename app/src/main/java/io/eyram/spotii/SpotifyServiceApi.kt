package io.eyram.spotii

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private val logger = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(logger)
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.spotify.com/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(okHttpClient)
    .build()



interface SpotifyServiceApi {

    @GET("v1/search")
    suspend fun getArtistSearch(
        @QueryMap query: Map<String, String>,
        @Header("Authorization") authCode: String
    ): SearchResult

    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String,

        ): AccessToken

    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    suspend fun getRefreshAccessToken(
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken : String ,
        @Field("client_id") clientId: String
    ): AccessToken
}

object SpotifyServiceObject {
    val retrofitService: SpotifyServiceApi by lazy {
        retrofit.create(SpotifyServiceApi::class.java)
    }
}

