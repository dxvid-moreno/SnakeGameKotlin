package com.example.snake

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Prepare modern launcher for Sign-In result
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val email = account.email ?: ""
                val idToken = account.idToken
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken, email)
                } else {
                    showToast(getString(R.string.error_default))
                }
            } catch (e: ApiException) {
                Log.w(getString(R.string.log_tag_login), getString(R.string.google_signin_failed), e)
                showToast(getString(R.string.error_default))
            }
        }

        // Analytics event
        FirebaseAnalytics.getInstance(this).logEvent(getString(R.string.event_login_screen_shown), Bundle())

        setupUI()
    }

    private fun setupUI() {
        title = getString(R.string.login)

        val emailField = findViewById<EditText>(R.id.emailTxt)
        val passwordField = findViewById<EditText>(R.id.passwordTxt)
        val loginButton = findViewById<Button>(R.id.logInButton)
        val googleButton = findViewById<ImageButton>(R.id.googleSignInButton)
        val resetPassword = findViewById<TextView>(R.id.lostPasswordText)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            when {
                !Validators.isEmailValid(email) -> showToast(getString(R.string.error_invalid_email))
                !Validators.isPasswordValid(password) -> showToast(getString(R.string.error_invalid_password))
                else -> auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navigateHome(email, ProviderType.BASIC)
                        } else {
                            showToast(getString(R.string.error_basic_login, task.exception?.message))
                        }
                    }
            }
        }

        resetPassword.setOnClickListener {
            val email = emailField.text.toString().trim()
            if (Validators.isEmailValid(email)) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) showToast(getString(R.string.reset_email_sent))
                        else showToast(getString(R.string.error_reset_email, task.exception?.message))
                    }
            } else showToast(getString(R.string.error_invalid_email))
        }

        googleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        val signUpButton = findViewById<TextView>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            openSignIn(it)
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String, email: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser == true

                    if (isNewUser) {
                        Log.d(getString(R.string.log_tag_login), getString(R.string.new_user_logged_in_google, email))
                        showToast(getString(R.string.new_google_user_registered))
                    } else {
                        Log.d(getString(R.string.log_tag_login), getString(R.string.existing_user_logged_in_google, email))
                    }

                    navigateHome(email, ProviderType.GOOGLE)
                } else {
                    Log.w(getString(R.string.log_tag_login), "signInWithCredential:failure", task.exception)
                    showToast(getString(R.string.error_default))
                }
            }
    }

    private fun navigateHome(email: String, provider: ProviderType) {
        Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
            startActivity(this)
        }
        finish()
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message ?: getString(R.string.error_default), Toast.LENGTH_LONG).show()
    }

    fun openSignIn(view: View) {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    fun backToHome(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
