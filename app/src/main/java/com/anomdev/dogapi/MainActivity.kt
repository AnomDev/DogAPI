package com.anomdev.dogapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.anomdev.dogapi.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DogAdapter
    private val dogImages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svDogs.setOnQueryTextListener(this)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = DogAdapter(dogImages)
        binding.rvDogs.layoutManager = LinearLayoutManager(this)
        binding.rvDogs.adapter = adapter
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchByName(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val call: Response<DogsResponse> =
                getRetrofit().create(APIService::class.java).getDogsByBreeds("$query/images")
            val puppies: DogsResponse? = call.body()
            //Como estamos en el hilo secundario y debemos pintar el Recycler en el hilo principal debemos volver a él. Para ello se utiliza lo siguiente:
            runOnUiThread {
                if (call.isSuccessful) {
                    //Si la llamada ha sido satisfactoria, mostraríamos el RecyclerView.
                    //Primero almacenamos las imagenes en una variable 'images', como la lista que venga puede estar vacía necesitamos ponerle un operador Elvis que se encargue de gestionar el null, en este caso pintando una lista vacía
                    val images: List<String> = puppies?.images ?: emptyList()
                    dogImages.clear()
                    dogImages.addAll(images)
                    adapter.notifyDataSetChanged()
                } else {
                    showError()
                }
            }

        }
    }

    private fun showError() {
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }
    // Este método se llamara cuando el usuario termine de escribir en el buscado y pulse ENTER.

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()){
            searchByName(query.lowercase(Locale.getDefault()))
        }
        return true
    }

    // Este método se llamara cada vez que el usuario escriba o borre UNA letra en el buscador. en este caso no nos interesa asi que no trabajamos con él. Pide un booleano, le damos un true y listo.
    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}