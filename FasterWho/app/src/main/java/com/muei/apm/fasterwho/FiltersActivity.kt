package com.muei.apm.fasterwho

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.maps.android.ktx.utils.toLatLngList
import org.w3c.dom.Text

class FiltersActivity : AppCompatActivity() {
    private var progressSeekBar : Int = 0
    private lateinit var seekBarDist: SeekBar
    private lateinit var rating: RatingBar
    private lateinit var nivelDificultad : RatingBar
    private lateinit var listView: ListView
    private var listOfPlaces = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)
        listView = findViewById<ListView>(R.id.listviewSearch)
        seekBarDist = findViewById(R.id.seekBar)
        rating = findViewById(R.id.ratingBarFiltros)
        nivelDificultad  = findViewById(R.id.ratingBarNivelDificultad)
        getDistancia()
        obtenerFiltros()

        val button : Button = findViewById(R.id.limpiarFiltros)
        button.visibility = View.VISIBLE
        button.setOnClickListener { limpiarFiltros() }
        val dialogDifLevelButton : ImageButton = findViewById(R.id.imageButtonInfo)
        dialogDifLevelButton.setOnClickListener {
            InfoFiltrosDialogFragment().show(supportFragmentManager,"Info Filtros")
            Toast.makeText(this, "Información de los niveles de dificultad", Toast.LENGTH_SHORT).show()
        }

        val btnCancelar : Button = findViewById(R.id.buttonCancelar)
        btnCancelar.setOnClickListener {
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
        }

        val btnAplicar : Button = findViewById(R.id.buttonAplicar)
        btnAplicar.setOnClickListener {
            aplicarFiltros()
            Toast.makeText(this, "Se aplican los filtros para las rutas", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
        }
        Places.initialize(this, "AIzaSyCgbkk8WGUqputkPFycIpL3ycIIt7_5HPc")

        val placesClient = Places.createClient(this)
        val mSearchButton: SearchView = findViewById(R.id.simpleSearchView)

        var textSelected = ""

        mSearchButton.setOnQueryTextListener(object : OnQueryTextListener{
            var adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1,listOfPlaces)
            override fun onQueryTextSubmit(query: kotlin.String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: kotlin.String?): Boolean {
                if (newText.isNullOrEmpty() || newText.isNullOrBlank()) {
                    listView.visibility = View.GONE
                    //button.visibility = View.VISIBLE
                    listOfPlaces.clear()
                }
                if(!newText.equals(textSelected)){
                    if (!newText.isNullOrEmpty() || !newText.isNullOrBlank()){
                        listView.visibility = View.VISIBLE
                        //button.visibility = View.VISIBLE
                        listOfPlaces.clear()
                        val token = AutocompleteSessionToken.newInstance()
                        // Create a RectangularBounds object.
                        val bounds = RectangularBounds.newInstance(
                                LatLng(41.8074776, -9.3015156),
                                LatLng(43.7923795, -6.733953199999999)
                        )
                        // Use the builder to create a FindAutocompletePredictionsRequest.
                        val request = FindAutocompletePredictionsRequest.builder()
                                .setLocationRestriction(bounds)
                                .setSessionToken(token)
                                .setTypeFilter(TypeFilter.CITIES)
                                .setQuery(mSearchButton.query.toString())
                                .build()
                        placesClient.findAutocompletePredictions(request)
                                .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                                    for (prediction in response.autocompletePredictions) {
                                        listOfPlaces.add(prediction.getFullText(null).toString())
                                        listView.adapter =
                                                ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1,listOfPlaces)
                                    }
                                }.addOnFailureListener { exception: Exception? ->
                                    if (exception is ApiException) {
                                        Log.e("TAG", "Place not found: " + exception.statusCode)
                                    }
                                }
                        adapter.filter.filter(newText)
                    }else{
                        listView.visibility = View.GONE
                        //button.visibility = View.GONE
                    }
                }

                return false
            }

        })
        listView.setOnItemClickListener { parent, view, position, id ->
            listView.visibility = View.GONE
            textSelected = listOfPlaces.get(id.toInt())
            mSearchButton.setQuery(listOfPlaces.get(id.toInt()),true)
        }
    }

    private fun aplicarFiltros(){

        val sharedPreferences : SharedPreferences = this.getSharedPreferences(getString(R.string.preference_filtersActivity_key),Context.MODE_PRIVATE) ?: return
        with(sharedPreferences.edit()){
            putFloat(getString(R.string.puntuacion),rating.rating)
            putInt(getString(R.string.distancia),progressSeekBar)
            putFloat(getString(R.string.nivel_de_dificultad),nivelDificultad.rating)
            apply()
        }
    }
    private fun obtenerFiltros(){
        val sharedPreferences : SharedPreferences = this.getSharedPreferences(getString(R.string.preference_filtersActivity_key),Context.MODE_PRIVATE) ?: return

        if (sharedPreferences.contains(getString(R.string.puntuacion))){
            rating.rating = sharedPreferences.getFloat(getString(R.string.puntuacion),0F)
        }
        if (sharedPreferences.contains(getString(R.string.distancia))){
            val textSeekBar : TextView = findViewById(R.id.textSeekBar)
            textSeekBar.text = sharedPreferences.getInt(getString(R.string.distancia),0).toString()
            seekBarDist.progress = sharedPreferences.getInt(getString(R.string.distancia),0)
            progressSeekBar = sharedPreferences.getInt(getString(R.string.distancia),0)

        }
        if (sharedPreferences.contains(getString(R.string.nivel_de_dificultad))){
            nivelDificultad.rating = sharedPreferences.getFloat(getString(R.string.nivel_de_dificultad),0F)
        }
    }
    private fun limpiarFiltros(){
        val sharedPreferences : SharedPreferences = this.getSharedPreferences(getString(R.string.preference_filtersActivity_key),Context.MODE_PRIVATE) ?: return
        sharedPreferences.edit {
            remove(getString(R.string.puntuacion))
            remove(getString(R.string.nivel_de_dificultad))
            remove(getString(R.string.distancia))
            apply()
        }
        progressSeekBar = 0
        findViewById<TextView>(R.id.textSeekBar).text = ""
        rating.rating = 0F
        nivelDificultad.rating = 0F
        seekBarDist.progress = 0

    }
    private fun getDistancia(){
        val textSeekBar : TextView = findViewById(R.id.textSeekBar)
        val seekBar: SeekBar = findViewById(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progressSeekBar = progress
                //seekBarDist.progress = progress
                textSeekBar.text = "$progress km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(applicationContext,"start tracking", Toast.LENGTH_SHORT).show()
                var textSeekBar : TextView = findViewById(R.id.textSeekBar)
                textSeekBar.text
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(applicationContext,"stop tracking", Toast.LENGTH_SHORT).show()
            }
        })
    }
}