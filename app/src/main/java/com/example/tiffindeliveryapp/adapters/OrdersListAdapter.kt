package com.example.tiffindeliveryapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.tiffindeliveryapp.R
import com.example.tiffindeliveryapp.datamodels.NewOrder
import com.example.tiffindeliveryapp.datamodels.Order
import com.example.tiffindeliveryapp.datamodels.TiffinService

class OrdersListAdapter(var ordersList:ArrayList<NewOrder>): Adapter<OrdersListAdapter.OrderListViewHolder>() {
    private lateinit var clickListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        clickListener = listener
    }

    fun onDatasetChanged(list:ArrayList<NewOrder>){
        ordersList = list
        notifyDataSetChanged()
    }

    fun onNewOrderAdd(order:NewOrder, pos:Int){
        ordersList.add(order)
        notifyItemChanged(pos)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.orders_list_item, parent, false)
        return OrderListViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        holder.serviceTitle.text = ordersList[position].serviceName
        holder.orderStatus.text = if(ordersList[position].orderCompleted) "Completed" else "Not Completed"
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    class OrderListViewHolder(itemView: View, listener: OrdersListAdapter.OnItemClickListener) :
        RecyclerView.ViewHolder(itemView){
        val serviceTitle: TextView
        val orderStatus: TextView

        init {
            serviceTitle = itemView.findViewById(R.id.service_name)
            orderStatus = itemView.findViewById(R.id.order_status)
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
            }
        }
}