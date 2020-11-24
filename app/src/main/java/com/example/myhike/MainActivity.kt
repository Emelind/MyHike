package com.example.myhike

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() , CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView

    var chosenFilter = ""
    val categoryForest = "Forest"
    val categoryMountain = "Mountain"
    val categoryCoastal = "Coastal"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = Job()
        db = AppDatabase.getInstance(this)

        val filters = resources.getStringArray(R.array.Filters)
        val filterSpinner = findViewById<Spinner>(R.id.spinnerFilter)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
        filterSpinner.adapter = adapter
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                chosenFilter = filters[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                chosenFilter = "All hikes"
            }
        }

        var hikes = mutableListOf<Hike>()

        var allHikes = mutableListOf<Hike>()
        val list = loadAllHikes()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        launch {
            allHikes = list.await()
            recyclerView.adapter = Adapter(this@MainActivity, allHikes)
            recyclerView.adapter?.notifyDataSetChanged()
        }

        // Filters hikes in recyclerview
        val filterButton = findViewById<Button>(R.id.btn_filter)
        filterButton.setOnClickListener {
            when(chosenFilter) {
                "All hikes" -> {
                    recyclerView.adapter = Adapter(this@MainActivity, allHikes)
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                "Forest hikes" -> {
                    launch {
                        hikes = loadByCategory(categoryForest).await()
                        recyclerView.adapter = Adapter(this@MainActivity, hikes)
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
                "Mountain hikes" -> {
                    launch {
                        hikes = loadByCategory(categoryMountain).await()
                        recyclerView.adapter = Adapter(this@MainActivity, hikes)
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
                "Coastal hikes" -> {
                    launch {
                        hikes = loadByCategory(categoryCoastal).await()
                        recyclerView.adapter = Adapter(this@MainActivity, hikes)
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
                "Day hikes" -> {
                    launch {
                        hikes = loadByNightStops(0).await()
                        recyclerView.adapter = Adapter(this@MainActivity, hikes)
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
                "Less than 10k" -> {
                    launch {
                        hikes = loadByLength(10).await()
                        recyclerView.adapter = Adapter(this@MainActivity, hikes)
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }

        // Starts AddHikeActivity
        val addButton = findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            val intent = Intent(this, AddHikeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun loadAllHikes() : Deferred<MutableList<Hike>> =
        async(Dispatchers.IO) {
            db.hikeDao.getAll()
        }

    private fun loadByNightStops(numberOfNightStops: Int) : Deferred<MutableList<Hike>> =
        async(Dispatchers.IO) {
            db.hikeDao.findByNightStops(numberOfNightStops)
        }

    private fun loadByCategory(category: String) : Deferred<MutableList<Hike>> =
        async(Dispatchers.IO) {
            db.hikeDao.findByCategory(category)
        }

    private fun loadByLength(length: Int) : Deferred<MutableList<Hike>> =
            async(Dispatchers.IO) {
                db.hikeDao.findByLength(length)
            }
}