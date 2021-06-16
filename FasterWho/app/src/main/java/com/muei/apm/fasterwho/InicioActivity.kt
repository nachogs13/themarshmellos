package com.muei.apm.fasterwho

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths

class InicioActivity : com.muei.apm.fasterwho.Toolbar(), NavigationView.OnNavigationItemSelectedListener {
    private val tag = "InicioActivity"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_inicio,frameLayout)
        val toolbar: Toolbar = findViewById(R.id.toolbar2)
        toolbar.title = "Inicio"

        val rel : RelativeLayout = findViewById(R.id.loadingPanel2)
        rel.visibility = View.VISIBLE

        val btnFiltros : TextView = findViewById(R.id.textViewFiltros)
        btnFiltros.setOnClickListener{
            val intent = Intent(this, FiltersActivity::class.java)
            intent.putExtra("parent", "InicioActivity")
            startActivity(intent)
        }

        val btnAnadirRuta : com.google.android.material.floatingactionbutton.FloatingActionButton = findViewById(R.id.floatingActionButton)
        btnAnadirRuta.setOnClickListener {
            Toast.makeText(this, "Iniciar Ruta", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SeguimientoActivity::class.java)
            startActivity(intent)
        }

        // listamos las rutas en Firebase del usuario, y comprobamos si los archivos KML existen
        // en el almacenamiento interno.
        // En caso de no existir se intenta descargarlos
        // !!! Pendiente hacer esto con una corrutina
        val storage = FirebaseStorage.getInstance()
        val db = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()

        lateinit var file : String
        lateinit var image: String

        // descargamos las rutas publicas
        db.collection("rutas")
            .whereEqualTo("public",true)
            .get().addOnSuccessListener { it ->
                for (document in it) {

                file = document.data["kml"] as String
                image = document.data["imagen"] as String
                // intentamos descargar el kml
                try {
                    if (Files.exists(Paths.get("${this.filesDir}/$file"))) {
                        Log.i(tag, "Existe $file")
                    } else {
                        Log.i(tag, "No existe $file")
                        val localFile = File(this.filesDir, file)
                        storage.reference.child("kmlsRutas/${file}").getFile(localFile).addOnSuccessListener {
                            Log.i(tag, "Archivo $file creado")
                        }.addOnFailureListener{
                            Log.i(tag, "fallo $it")
                        }
                    }
                } catch (e : Exception) {
                    Log.d(tag, "Ha ocurrido un error al descargar la ruta publica $file")
                }
                // intentamos descargar la imagen
                try {
                    if (Files.exists(Paths.get("${this.filesDir}/$image"))) {
                        Log.i(tag, "Existe imagen $image")
                    } else {
                        Log.i(tag, "No existe imagen $image")
                        val localFile = File(this.filesDir, image)
                        storage.reference.child("ImgRutas/${image}").getFile(localFile).addOnSuccessListener {
                            Log.i(tag, "Imagen $image creada")
                        }.addOnFailureListener{
                            Log.i(tag, "fallo $it")
                        }
                    }
                } catch (e : Exception) {
                    Log.d(tag, "Ha ocurrido un error al descargar la imagen $image")
                }

            }
                rel.visibility = View.GONE
        }

        // descargamos las rutas privadas y sus imágenes
        db.collection("rutas")
            .whereEqualTo("usuario",firebaseAuth.currentUser!!.email!!)
            .get().addOnSuccessListener { it ->
                for (document in it) {
                    file = document.data["kml"] as String
                    image = document.data["imagen"] as String
                    // intentamos descargar el kml
                    try {
                        if (Files.exists(Paths.get("${this.filesDir}/$file"))) {
                            Log.i(tag, "Existe $file")
                        } else {
                            Log.i(tag, "No existe $file")
                            val localFile = File(this.filesDir, file)
                            storage.reference.child("kmlsRutas/${file}").getFile(localFile).addOnSuccessListener {
                                Log.i(tag, "Archivo $file creado")
                            }.addOnFailureListener{
                                Log.i(tag, "fallo $it")
                            }
                        }
                    } catch (e: Exception) {
                        Log.d(tag, "Ha ocurrido un error al descargar la rutas privada $file")
                    }

                    // intentamos descargar la imagen
                    try {
                        if (Files.exists(Paths.get("${this.filesDir}/$image"))) {
                            Log.i(tag, "Existe imagen $image")
                        } else {
                            Log.i(tag, "No existe imagen $image")
                            val localFile = File(this.filesDir, image)
                            storage.reference.child("ImgRutas/${image}").getFile(localFile).addOnSuccessListener {
                                Log.i(tag, "Imagen $image creada")
                            }.addOnFailureListener{
                                Log.i(tag, "fallo $it")
                            }
                        }
                    } catch (e : Exception) {
                        Log.d(tag, "Ha ocurrido un error al descargar la imagen $image")
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        navView.menu.getItem(0).isChecked = true
    }


    fun sinResultados(isEmp: Boolean) {
        val sinRes : TextView = findViewById(R.id.textViewSinResultados)
        if (isEmp) {
            sinRes.visibility = View.VISIBLE
        } else {
            sinRes.visibility = View.GONE
        }
    }
}