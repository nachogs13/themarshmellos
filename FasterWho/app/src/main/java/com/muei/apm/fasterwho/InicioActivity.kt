package com.muei.apm.fasterwho

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class InicioActivity : com.muei.apm.fasterwho.Toolbar(), NavigationView.OnNavigationItemSelectedListener {
    //private lateinit var drawerLayout: DrawerLayout
    //private lateinit var navView : NavigationView
    private val TAG= "InicioActivity"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_profile)
        layoutInflater.inflate(R.layout.activity_inicio,frameLayout)
        val toolbar: Toolbar = findViewById(R.id.toolbar2)
        toolbar.setTitle("Inicio")

        val btnFiltros : TextView = findViewById(R.id.textViewFiltros)
        btnFiltros.setOnClickListener{
            val intent = Intent(this, FiltersActivity::class.java)
            startActivity(intent)
        }

        val btnScan : Button = findViewById(R.id.buttonScanQr)
        btnScan.setOnClickListener{
            Toast.makeText(this, "Se busca ruta mediante código QR", Toast.LENGTH_SHORT).show()
        }

        val btnAnadirRuta : com.google.android.material.floatingactionbutton.FloatingActionButton = findViewById(R.id.floatingActionButton)
        btnAnadirRuta.setOnClickListener({
            Toast.makeText(this, "Iniciar Ruta", Toast.LENGTH_SHORT).show()
            //val intent = Intent(this, IniciarRutaActivity::class.java)
            val intent = Intent(this, SeguimientoActivity::class.java)
            startActivity(intent)
        })

        // listamos las rutas en Firebase del usuario, y comprobamos si los archivos KML existen
        // en el almacenamiento interno.
        // En caso de no existir se intenta descargarlos
        // !!! Pendiente hacer esto con una corrutina
        val storage = FirebaseStorage.getInstance()
         val db = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        lateinit var file : String
        lateinit var image: String
        val listRef = storage.reference.child("kmlsRutas/${firebaseAuth.currentUser.email}")

        /*listRef.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { prefix ->
                    Log.i("prueba1", prefix.name)
                }

                items.forEach { item ->
                    var prue: Path = Paths.get("${this.filesDir}/${item.name}")
                    if (Files.exists(prue)) {
                        Log.i("prueba2", "Existe ${item.name}")
                    } else {
                        Log.i("prueba2", "No existe ${item.name}")
                        val localFile = File(this.filesDir, item.name)
                        item.
                        storage.reference.child("kmlsRutas/${firebaseAuth.currentUser.email}/${item.name}").getFile(localFile).addOnSuccessListener {
                            Log.i("prueba2", "Archivo creado")
                        }.addOnFailureListener{ it ->
                            Log.i("prueba2", "fallo ${it.toString()}")
                        }
                    }

                }
            }
            .addOnFailureListener {
                Log.i("prueba", "error")
            }*/
        // descargamos las rutas publicas
        db.collection("rutas")
            .whereEqualTo("public",true)
            .get().addOnSuccessListener {
                for (document in it) {
                    file = document.data.get("kml") as String
                    image = document.data.get("imagen") as String
                    // intentamos descargar el kml
                    try {
                        if (Files.exists(Paths.get("${this.filesDir}/$file"))) {
                            Log.i(TAG, "Existe ${file}")
                        } else {
                            Log.i(TAG, "No existe ${file}")
                            val localFile = File(this.filesDir, file)
                            storage.reference.child("kmlsRutas/${file}").getFile(localFile).addOnSuccessListener {
                                Log.i(TAG, "Archivo ${file} creado")
                            }.addOnFailureListener{ it ->
                                Log.i(TAG, "fallo ${it.toString()}")
                            }
                        }
                    } catch (e : Exception) {
                        Log.d(TAG, "Ha ocurrido un error al descargar la ruta publica")
                    }
                    // intentamos descargar la imagen
                    try {
                        if (Files.exists(Paths.get("${this.filesDir}/$image"))) {
                            Log.i(TAG, "Existe imagen ${image}")
                        } else {
                            Log.i(TAG, "No existe imagen ${image}")
                            val localFile = File(this.filesDir, image)
                            storage.reference.child("ImgRutas/${image}").getFile(localFile).addOnSuccessListener {
                                Log.i(TAG, "Imagen ${image} creada")
                            }.addOnFailureListener{ it ->
                                Log.i(TAG, "fallo ${it.toString()}")
                            }
                        }
                    } catch (e : Exception) {
                        Log.d(TAG, "Ha ocurrido un error al descargar la imagen publica")
                    }

                }
            }

        // descargamos las rutas privadas y sus imágenes
        db.collection("rutas")
            .whereEqualTo("usuario",firebaseAuth.currentUser.email)
            .get().addOnSuccessListener {
                for (document in it) {
                    file = document.data.get("kml") as String
                    image = document.data.get("imagen") as String
                    // intentamos descargar el kml
                    try {
                        if (Files.exists(Paths.get("${this.filesDir}/$file"))) {
                            Log.i(TAG, "Existe ${file}")
                        } else {
                            Log.i(TAG, "No existe ${file}")
                            val localFile = File(this.filesDir, file)
                            storage.reference.child("kmlsRutas/${file}").getFile(localFile).addOnSuccessListener {
                                Log.i(TAG, "Archivo ${file} creado")
                            }.addOnFailureListener{ it ->
                                Log.i(TAG, "fallo ${it.toString()}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "Ha ocurrido un error al descargar las rutas privadas")
                    }
                    // intentamos descargar la imagen
                    try {
                        if (Files.exists(Paths.get("${this.filesDir}/$image"))) {
                            Log.i(TAG, "Existe imagen ${image}")
                        } else {
                            Log.i(TAG, "No existe imagen ${image}")
                            val localFile = File(this.filesDir, image)
                            storage.reference.child("ImgRutas/${image}").getFile(localFile).addOnSuccessListener {
                                Log.i(TAG, "Imagen ${image} creada")
                            }.addOnFailureListener{ it ->
                                Log.i(TAG, "fallo ${it.toString()}")
                            }
                        }
                    } catch (e : Exception) {
                        Log.d(TAG, "Ha ocurrido un error al descargar la imagen privada")
                    }

                }
            }
    }

    override fun onResume() {
        super.onResume()
        navView.menu.getItem(0).setChecked(true)
    }
}