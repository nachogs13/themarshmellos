package com.muei.apm.fasterwho

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

open class Toolbar : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var navView : NavigationView
    lateinit var frameLayout: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        configureToolbar()
        frameLayout = findViewById(R.id.main_content)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val header = navView.getHeaderView(0)
        val text : TextView = header.findViewById(R.id.userNameTool)
        navView.setNavigationItemSelectedListener(this)

        db.collection("usuarios").document(firebaseAuth.currentUser!!.email!!.toString()).get()
            .addOnSuccessListener {
                Log.d("usuario", it.toString())
                val username = it.get("username").toString()
                text.text = username
                if(it.get("imgPerfil")!=null){
                    val storage = FirebaseStorage.getInstance()
                    val img = it.get("imgPerfil") as DocumentReference
                    GlideApp.with(this).load(storage.getReference(img.path)).into(header.findViewById(R.id.imageViewTool))
                }
            }
    }

    private fun configureToolbar(){
        val toolbar: Toolbar = findViewById(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intentInicio = Intent(this, InicioActivity::class.java)
        val intentProfile = Intent(this, ProfileActivity::class.java)
        val intentRankings = Intent(this, RankingActivity::class.java)
        val intentRutas = Intent(this, MisRutas::class.java)
        val intentAmigos = Intent(this, MisAmigos::class.java)

        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(intentInicio.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_profile -> {
                startActivity(intentProfile.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_rankings -> {
                startActivity(intentRankings.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                Toast.makeText(this, "Rankings", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_routes -> {
                startActivity(intentRutas.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                Toast.makeText(this, "Mis rutas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_amigos -> {
                startActivity(intentAmigos.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                Toast.makeText(this, "Mis amigos", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                //mGoogleSignInClient.signOut()
                FirebaseAuth.getInstance().signOut()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

                mGoogleSignInClient.signOut().addOnCompleteListener(this){
                    val intent = Intent(this, MainActivity::class.java)
                    //Toast.makeText(this, "Saliendo de la app", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                }
                val sharedPreferences : SharedPreferences = this.getSharedPreferences(getString(R.string.preference_filtersActivity_key), Context.MODE_PRIVATE)
                sharedPreferences.edit {
                    remove(getString(R.string.puntuacion))
                    remove(getString(R.string.nivel_de_dificultad))
                    remove(getString(R.string.distancia))
                    apply()
                }


            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}





