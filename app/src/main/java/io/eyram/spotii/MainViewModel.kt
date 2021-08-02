package io.eyram.spotii

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eyram.spotii.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainViewModel : ViewModel() {

    private val clientId = "8949c7b3b8b04c4aab36a6cd5b5ac172"
    private val redirectUri = "ioeyramspotii://callback"
    private val randomStrings = UUID.randomUUID().toString()
    private val codeVerifier = UUID.randomUUID().toString().plus(randomStrings)

    private val _result = MutableLiveData<SearchResult>()
    val result: LiveData<SearchResult>
        get() = _result

    private val _accessToken = MutableLiveData<Result<AccessToken>>()
    val accessToken: LiveData<Result<AccessToken>>
        get() = _accessToken

    private val _refreshedAccessToken = MutableLiveData<Result<AccessToken>>()
    val refreshedAccessToken: LiveData<Result<AccessToken>>
        get() = _refreshedAccessToken

    val authQuery = "https://accounts.spotify.com/authorize?response_type=code&" +
            "client_id=$clientId&" +
            "redirect_uri=$redirectUri&" +
            "code_challenge=${codeChallenge(codeVerifier)}&" +
            "code_challenge_method=S256"

    //without the padding flag this returns an http 400 error
    private fun encodeB64(input: ByteArray) =
        Base64.encodeToString(input, Base64.URL_SAFE or Base64.NO_PADDING)

    private fun codeChallenge(input: String) = encodeB64(Utils.sha256(input))

    fun searchArtist(query: String, type: String, accessToken: String) {
        viewModelScope.launch {
            val result = SpotifyServiceObject.retrofitService.getArtistSearch(
                mapOf(Pair("q", query), Pair("type", type)),
                "Bearer $accessToken"
            )
            _result.value = result
        }
    }

    fun getAccessToken(code: String) {
        viewModelScope.launch {
            val result = runCatching {
                SpotifyServiceObject.retrofitService.getAccessToken(
                    clientId = clientId,
                    grantType = "authorization_code",
                    code = code,
                    redirectUri = redirectUri,
                    codeVerifier = codeVerifier
                )
            }

            withContext(Dispatchers.Main) {
                _accessToken.value = result
            }
        }
    }

    fun getRefreshedAccessToken(refreshToken: String) {
        viewModelScope.launch {
            val result = runCatching {
                SpotifyServiceObject.retrofitService.getRefreshAccessToken(
                    grantType = "refresh_token",
                    refreshToken = refreshToken,
                    clientId = clientId
                )
            }

            withContext(Dispatchers.Main) {
                _refreshedAccessToken.value = result
            }
        }
    }
}