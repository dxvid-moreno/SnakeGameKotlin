package com.example.snake

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.example.snake.Validators

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Setup Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Analytics event
        FirebaseAnalytics.getInstance(this).logEvent("login_screen_shown", Bundle())

        setupUI()
    }

    private fun setupUI() {
        title = getString(R.string.login)

        // UI fields
        val emailField = findViewById<EditText>(R.id.emailTxt)
        val passwordField = findViewById<EditText>(R.id.passwordTxt)
        val loginButton = findViewById<Button>(R.id.logInButton)
        val googleButton = findViewById<ImageButton>(R.id.googleSignInButton)
        val resetPassword = findViewById<TextView>(R.id.textView3)

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
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken.isNullOrEmpty()) {
                    showToast(getString(R.string.error_no_id_token))
                    return
                }
                firebaseAuthWithGoogle(idToken)
            } catch (e: ApiException) {
                showToast(getString(R.string.error_google_account, e.message))
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val email = auth.currentUser?.email ?: ""
                    navigateHome(email, ProviderType.GOOGLE)
                } else {
                    showToast(getString(R.string.error_google_login, task.exception?.message))
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
}