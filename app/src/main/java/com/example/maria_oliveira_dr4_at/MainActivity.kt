package com.example.maria_oliveira_dr4_at

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private var mUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        mUser = auth.currentUser

        //usuario ja cadastrado
        if (mUser != null) {
            startActivity(
                Intent(
                    this,
                    HomeActivity::class.java)
            )
        }

        btnCadastrarUsuario.setOnClickListener {
            try {
                cadastrar(edtEmail, edtSenha)
                    .addOnSuccessListener {
                        mUser = it.user
                        Toast.makeText(
                            this,
                            "Cadastro realizado com sucesso.",
                            Toast.LENGTH_LONG).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            it.message,
                            Toast.LENGTH_LONG).show()
                    }
                Toast.makeText(
                    this,
                    "Efetuando Cadastro.",
                    Toast.LENGTH_LONG).show()
            } catch (e: Throwable) {
                Toast.makeText(
                    this,
                    e.message,
                    Toast.LENGTH_LONG).show()
            }
        }

        btnLogar.setOnClickListener {
            try {
                logar(edtEmail, edtSenha)
                    .addOnSuccessListener {
                        if (it != null){
                            Toast.makeText(
                                this,
                                "Bem vindo ${it.user?.email}!",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(
                                Intent(
                                    this,
                                    HomeActivity::class.java
                                )
                            )
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } catch (e: Throwable){
                Toast.makeText(
                    this,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }


    fun cadastrar(
        edtEmail : EditText,
        edtSenha : EditText
    )
            : Task<AuthResult> {
        val email = edtEmail.text.toString()
        val senha = edtSenha.text.toString()
        if (email.isNullOrEmpty()
            || senha.isNullOrEmpty())
            throw object : Throwable() {
                override val message: String?
                    get() = "Preencha todos os campos"
            }
        return auth.createUserWithEmailAndPassword(
            email, senha
        )
    }

    fun logar(
        edtEmail: EditText,
        edtSenha: EditText
    )
            : Task<AuthResult> {
        val email = edtEmail.text.toString()
        val senha = edtSenha.text.toString()
        if (email.isNullOrEmpty() || senha.isNullOrEmpty())
            throw object : Throwable() {
                override val message: String?
                    get() = "Preencha todos os campos"
            }
        return auth.signInWithEmailAndPassword(
            email, senha
        )
    }
}

