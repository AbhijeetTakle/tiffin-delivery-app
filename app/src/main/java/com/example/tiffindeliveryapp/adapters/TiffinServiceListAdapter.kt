package com.example.tiffindeliveryapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.tiffindeliveryapp.R
import com.example.tiffindeliveryapp.datamodels.TiffinService

class TiffinServiceListAdapter(var services:ArrayList<TiffinService>):Adapter<TiffinServiceListAdapter.TiffinServiceViewHolder>() {

    private lateinit var clickListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        clickListener = listener
    }

    fun onDatasetChanged(list:ArrayList<TiffinService>){
        services = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TiffinServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.service_list_item, parent, false)
        return TiffinServiceViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: TiffinServiceViewHolder, position: Int) {
        holder.serviceTitle.text = services[position].title
        holder.serviceDescription.text = services[position].description
        holder.serviceSubscriberCount.text = services[position].subscriberCount.toString()+" subscribers"
        holder.serviceRating.text = String.format("%.1f ",services[position].rating)
    }

    override fun getItemCount(): Int {
        return services.size
    }

    class TiffinServiceViewHolder(itemView: View, listener:OnItemClickListener) :ViewHolder(itemView){
        val serviceTitle:TextView
        val serviceDescription:TextView
        val serviceSubscriberCount:TextView
        val serviceRating:TextView
        val serviceImage:ImageView

        init {
            serviceTitle = itemView.findViewById(R.id.food_service_title)
            serviceDescription = itemView.findViewById(R.id.food_service_description)
            serviceSubscriberCount = itemView.findViewById(R.id.food_service_subscriber_count)
            serviceRating = itemView.findViewById(R.id.food_service_rating)
            serviceImage = itemView.findViewById(R.id.food_service_image)
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}