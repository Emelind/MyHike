package com.example.myhike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AddHikeActivity : AppCompatActivity() , CoroutineScope {

    lateinit var addHikeName: EditText
    lateinit var addHikeArea: EditText
    lateinit var addHikeCategorySpinner: Spinner
    lateinit var addHikeLength: EditText
    lateinit var addHikeNumberOfNightStops: EditText
    lateinit var addHikeFavorite: CheckBox
    lateinit var buttonAddHikeSave: Button
    lateinit var buttonAddHikeCancel: Button
    val categoryForest = "Forest"
    val categoryMountain = "Mountain"
    val categoryCoastal = "Coastal"
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

        addHikeName = findViewById(R.id.addHikeName)
        addHikeArea = findViewById(R.id.addHikeAreaName)
        addHikeLength = findViewById(R.id.addHikeLength)
        addHikeNumberOfNightStops = findViewById(R.id.addHikeNumberOfNightStops)
        addHikeFavorite = findViewById(R.id.addHikeFavoriteCheckBox)
        buttonAddHikeSave = findViewById(R.id.button_addHikeSave)
        buttonAddHikeCancel = findViewById(R.id.button_addHikeCancel)

        val categories = resources.getStringArray(R.array.Categories)

        addHikeCategorySpinner = findViewById(R.id.addHikeSpinner)
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
                finish()
            }
        }
    }
    fun saveHike(hike: Hike) {
        launch(Dispatchers.IO) {
            db.hikeDao.insert(hike)
        }
    }
}