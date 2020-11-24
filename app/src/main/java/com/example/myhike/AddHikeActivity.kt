package com.example.myhike

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AddHikeActivity : AppCompatActivity() , CoroutineScope {

    val categoryNotApplicable = "Not Applicable"
    var chosenCategory = ""

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_hike)

        job = Job()
        db = AppDatabase.getInstance(this)

        val addHikeName = findViewById<EditText>(R.id.addHikeName)
        val addHikeArea = findViewById<EditText>(R.id.addHikeAreaName)
        val addHikeLength = findViewById<EditText>(R.id.addHikeLength)
        val addHikeNumberOfNightStops = findViewById<EditText>(R.id.addHikeNumberOfNightStops)
        val addHikeFavorite = findViewById<CheckBox>(R.id.addHikeFavoriteCheckBox)
        val buttonAddHikeSave = findViewById<Button>(R.id.button_addHikeSave)
        val buttonAddHikeCancel = findViewById<Button>(R.id.button_addHikeCancel)

        val categories = resources.getStringArray(R.array.Categories)

        val addHikeCategorySpinner = findViewById<Spinner>(R.id.addHikeSpinner)
        if(addHikeCategorySpinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            addHikeCategorySpinner.adapter = adapter
        }
        addHikeCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                chosenCategory = categories[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                chosenCategory = categoryNotApplicable
            }
        }

        buttonAddHikeCancel.setOnClickListener {
            finish()
        }

        buttonAddHikeSave.setOnClickListener {
            if(addHikeName.text.isEmpty() || addHikeArea.text.isEmpty() || addHikeLength.text.isEmpty() ||
                    addHikeNumberOfNightStops.text.isEmpty()) {
                Toast.makeText(this, "Fyll i alla fält", Toast.LENGTH_SHORT).show()
            } else {
                val name = addHikeName.text.toString()
                val area = addHikeArea.text.toString()
                val category = chosenCategory
                val length = addHikeLength.text.toString().toInt()
                val nightstops = addHikeNumberOfNightStops.text.toString().toInt()
                var favorite = false
                if(addHikeFavorite.isChecked) {
                    favorite = true
                }
                val hike = Hike(0, name, area, category, length, nightstops, favorite)
                saveHike(hike)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun saveHike(hike: Hike) {
        launch(Dispatchers.IO) {
            db.hikeDao.insert(hike)
        }
    }
}

