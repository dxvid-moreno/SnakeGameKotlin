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
            Toast.makeText(this, "Google sign-in failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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

        val googleButton = findViewById<ImageButton>(R.id.googleSignUpButton)
        googleButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun createAccount() {
        val email = findViewById<EditText>(R.id.emailSignInTxt).text.toString().trim()
        val password = findViewById<EditText>(R.id.passwTxt).text.toString().trim()
        val confirmPassword = findViewById<EditText>(R.id.confirmPasswTxt).text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                    goToMainActivity(email, ProviderType.BASIC)
                } else {
                    val error = task.exception?.message ?: "Error al crear la cuenta"
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
                        Toast.makeText(this, "Cuenta Google registrada correctamente", Toast.LENGTH_SHORT).show()
                        goToMainActivity(email, ProviderType.GOOGLE)
                    } else {
                        // Usuario ya registrado, cerrar sesión y mostrar error
                        auth.signOut()
                        googleSignInClient.signOut()
                        Toast.makeText(this, "Esta cuenta ya existe. Por favor inicia sesión.", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this, "Falló la autenticación con Google", Toast.LENGTH_SHORT).show()
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
