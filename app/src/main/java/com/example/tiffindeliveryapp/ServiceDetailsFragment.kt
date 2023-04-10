package com.example.tiffindeliveryapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.tiffindeliveryapp.datamodels.TiffinService
import com.example.tiffindeliveryapp.viewmodelfactory.ServiceDetailsViewModelFactory
import com.example.tiffindeliveryapp.viewmodels.ServiceDetailsViewModel
import org.w3c.dom.Text
import kotlin.math.log

class ServiceDetailsFragment : Fragment() {
    private lateinit var serviceDetailsViewModel: ServiceDetailsViewModel
    private lateinit var serviceID:String
    private lateinit var service:TiffinService
    private lateinit var serviceTitle: TextView
    private lateinit var serviceDescription: TextView
    private lateinit var serviceSubscriberCount: TextView
    private lateinit var serviceRating: TextView
    private lateinit var serviceImage: ImageView
    private lateinit var serviceMenu:TextView
    private lateinit var serviceTiffinTypes:LinearLayout
    private lateinit var serviceReviews:LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_service_details, container, false)
        serviceTitle = view.findViewById(R.id.food_service_title)
        serviceDescription = view.findViewById(R.id.food_service_description)
        serviceSubscriberCount = view.findViewById(R.id.food_service_subscriber_count)
        serviceRating = view.findViewById(R.id.food_service_rating)
        serviceImage = view.findViewById(R.id.food_service_image)
        serviceMenu = view.findViewById(R.id.service_menu)
        serviceTiffinTypes = view.findViewById(R.id.service_tiffin_types)
        serviceReviews = view.findViewById(R.id.service_reviews)
        arguments?.getString("ServiceID")?.let {
            serviceID = it
        }
        serviceDetailsViewModel = ViewModelProvider(this, ServiceDetailsViewModelFactory(serviceID)).get(ServiceDetailsViewModel::class.java)
        serviceDetailsViewModel.servicesList.observe(viewLifecycleOwner){
            if(!it.isEmpty()){
                service = it[0]
                Log.d("TAG", "updateService: "+service.title)
                updateService(inflater)
            }
        }
        return view
    }

    private fun updateService(inflator:LayoutInflater) {
        serviceTitle.text = service.title
        serviceDescription.text = service.description
        serviceSubscriberCount.text = service.subscriberCount.toString()+" subscribers"
        serviceRating.text = service.rating.toString()
        serviceMenu.text = service.todaysMenu

        for(s in service.tiffinTypes.keys){
            val tiffinType = TextView(requireContext())
            tiffinType.text = service.tiffinTypes.get(s)
            serviceTiffinTypes.addView(tiffinType)
        }

        for(s in service.customerReviews.keys){
            val review = layoutInflater.inflate(R.layout.review_list_item, serviceReviews, false)
            review.findViewById<TextView>(R.id.review_user).text = s
            review.findViewById<TextView>(R.id.review_comment).text = service.customerReviews.get(s)
            serviceReviews.addView(review)
        }


    }
}