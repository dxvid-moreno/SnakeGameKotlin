package com.example.snake

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Asegúrate de agregar tu `client_id` en strings.xml
            .requestEmail()
            .build()

        GoogleSignIn.getClient(this, gso)
    }

    private val RC_SIGN_IN = 9001 // Este código se utilizará para identificar el resultado de Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Firebase Analytics Event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("event_name", bundle)

        auth = FirebaseAuth.getInstance()

        // Set up the UI elements
        setup()
    }

    private fun setup() {
        title = "Autenticación"

        // Email and Password Login
        val logInButton = findViewById<Button>(R.id.logInButton)
        val emailTxt = findViewById<EditText>(R.id.emailTxt)
        val passwordTxt = findViewById<EditText>(R.id.passwordTxt)

        logInButton.setOnClickListener {
            if (emailTxt.text.isNotEmpty() && passwordTxt.text.isNotEmpty()) {
                auth.signInWithEmailAndPassword(emailTxt.text.toString(), passwordTxt.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            showAlert()
                        }
                    }
            }
        }

        // Google Sign-In Button
        val googleSignInButton = findViewById<ImageButton>(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN) // Usamos startActivityForResult aquí
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al obtener cuenta de Google: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user = auth.currentUser
                showHome(user?.email ?: "", ProviderType.GOOGLE)
            }
            .addOnFailureListener { exception ->
                // Mostrar error con un Toast
                Toast.makeText(this, "Error al iniciar sesión con Google: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }


    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun showAlert() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Se produjo un error, intenta de nuevo")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }

    // Navigate back to home screen
    fun BackToHome(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Open the Sign In screen
    fun openSignIn(view: View) {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }
}
