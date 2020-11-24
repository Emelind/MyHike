package com.example.myhike

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class Adapter(val context: Context, val myHikes: MutableList<Hike>) : RecyclerView.Adapter<Adapter.ViewHolder>() ,
    CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var db: AppDatabase

    val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val listView =layoutInflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(listView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hike = myHikes[position]
        holder.tvShowHikeName.text = hike.name
        holder.tvShowHikeArea.text = "Area: " + hike.area
        holder.tvShowHikeCategory.text = "Category: " + hike.category
        holder.tvShowHikeLength.text = "Length: " + hike.length.toString()
        holder.tvShowHikeNumberOfNightStops.text = "Number of night stops: " + hike.nightstops.toString()
        holder.tvShowHikeFavorite.text = "Favorite? " + hike.favorite
        holder.hikePosition = position
    }

    override fun getItemCount() = myHikes.size

    fun deleteHike(position: Int, hike: Hike) {
        val dialogBuilder = AlertDialog.Builder(context)

        job = Job()
        db = AppDatabase.getInstance(context)

        dialogBuilder.setTitle("Delete hike?")
            .setMessage("Remove ${myHikes[position].name} from list?")
            .setPositiveButton("Remove") { dialog, id ->
                myHikes.removeAt(position)
                notifyDataSetChanged()
                deleteHikeDb(hike)

            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.show()
    }

    inner class ViewHolder(listView: View) : RecyclerView.ViewHolder(listView) {
        val tvShowHikeName = listView.findViewById<TextView>(R.id.tv_showHikeName)
        val tvShowHikeArea = listView.findViewById<TextView>(R.id.tv_showHikeArea)
        val tvShowHikeCategory = listView.findViewById<TextView>(R.id.tv_showHikeCategory)
        val tvShowHikeLength = listView.findViewById<TextView>(R.id.tv_showHikeLength)
        val tvShowHikeNumberOfNightStops = listView.findViewById<TextView>(R.id.tv_showHikeNumberOfNightStops)
        val tvShowHikeFavorite = listView.findViewById<TextView>(R.id.tv_showHikeFavorite)

        val buttonShowHikeDelete = listView.findViewById<ImageButton>(R.id.button_showHikeDelete)

        var hikePosition = 0

        init {

            buttonShowHikeDelete.setOnClickListener {
                Log.d("!!!", "adapter: trucks[0]: ${myHikes[hikePosition]}")
                val hike = myHikes[hikePosition]
                deleteHike(hikePosition, hike)
            }
        }
    }
    fun deleteHikeDb(hike: Hike) {
        launch(Dispatchers.IO) {
            db.hikeDao.delete(hike)
        }
    }

}