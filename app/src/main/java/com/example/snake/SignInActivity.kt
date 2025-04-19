package com.example.snake

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        auth = FirebaseAuth.getInstance()

        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            createAccount()
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

        // Llama a Firebase Auth para crear la cuenta
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))  // Redirige a MainActivity
                    finish()  // Cierra la actividad actual
                } else {
                    val error = task.exception?.message ?: "Error al crear la cuenta"
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                }
            }
    }


    fun BackToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}




