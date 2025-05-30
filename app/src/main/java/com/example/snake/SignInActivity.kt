package com.example.snake

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Toast.makeText(
                this,
                "${getString(R.string.google_sign_in_failed)}: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        auth = FirebaseAuth.getInstance()

        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            createAccount()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleButton = findViewById<ImageButton>(R.id.googleSignInButton)
        googleButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun createAccount() {
        val email = findViewById<EditText>(R.id.emailSignInTxt).text.toString().trim()
        val password = findViewById<EditText>(R.id.passwTxt).text.toString().trim()
        val confirmPassword = findViewById<EditText>(R.id.confirmPasswTxt).text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, getString(R.string.error_password_length), Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.account_created_success), Toast.LENGTH_SHORT).show()
                    goToMainActivity(email, ProviderType.BASIC)
                } else {
                    val error = task.exception?.message ?: getString(R.string.account_creation_failed)
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser == true
                    val email = auth.currentUser?.email ?: "unknown"

                    if (isNewUser) {
                        Toast.makeText(this, getString(R.string.google_account_registered), Toast.LENGTH_SHORT).show()
                        goToMainActivity(email, ProviderType.GOOGLE)
                    } else {
                        auth.signOut()
                        googleSignInClient.signOut()
                        Toast.makeText(this, getString(R.string.google_account_exists), Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this, getString(R.string.google_auth_failed), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToMainActivity(email: String, provider: ProviderType) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("provider", provider.name)
        startActivity(intent)
        finish()
    }

    fun BackToLogin(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
