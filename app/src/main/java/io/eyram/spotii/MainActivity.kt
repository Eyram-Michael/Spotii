package io.eyram.spotii


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private lateinit var textView: TextView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.text_view)
        button = findViewById(R.id.button_2)

        button.setOnClickListener {
            CustomTabsIntent
                .Builder()
                .build()
                .launchUrl(this, Uri.parse(viewModel.authQuery))
        }

        viewModel.result.observe(this, { result -> textView.text = result.toString() })

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val code = intent?.data?.getQueryParameter("code")!!

        viewModel.getAccessToken(code)

        viewModel.accessToken.observe(this, { response ->
            response.onSuccess { accessToken ->
                viewModel.getRefreshedAccessToken(accessToken.refreshToken)
            }
        })

        viewModel.refreshedAccessToken.observe(this , {response ->
            response.onSuccess {
                viewModel.searchArtist(
                    query = "Muse",
                    type = "artist",
                    accessToken = it.accessToken!!
                )
            }
        })
    }
}


