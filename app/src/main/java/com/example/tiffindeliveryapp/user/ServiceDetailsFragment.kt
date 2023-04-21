package com.example.tiffindeliveryapp.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tiffindeliveryapp.R
import com.example.tiffindeliveryapp.datamodels.TiffinService
import com.example.tiffindeliveryapp.viewmodelfactory.ServiceDetailsViewModelFactory
import com.example.tiffindeliveryapp.viewmodels.ServiceDetailsViewModel

class ServiceDetailsFragment : Fragment() {
    private lateinit var serviceDetailsViewModel: ServiceDetailsViewModel
    private lateinit var serviceID:String
    private lateinit var service:TiffinService
    private lateinit var serviceTitle: TextView
    private lateinit var subscribeService:Button
    private lateinit var serviceDescription: TextView
    private lateinit var serviceSubscriberCount: TextView
    private lateinit var serviceRating: TextView
    private lateinit var serviceImage: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_service_details, container, false)
        serviceTitle = view.findViewById(R.id.food_service_title)
        serviceDescription = view.findViewById(R.id.food_service_description)
        serviceSubscriberCount = view.findViewById(R.id.food_service_subscriber_count)
        serviceRating = view.findViewById(R.id.food_service_rating)
        subscribeService = view.findViewById(R.id.subscribe_service)
        serviceImage = view.findViewById(R.id.food_service_image)
        arguments?.getString("ServiceID")?.let {
            serviceID = it
        }
        serviceDetailsViewModel = ViewModelProvider(this, ServiceDetailsViewModelFactory(serviceID)).get(ServiceDetailsViewModel::class.java)
        serviceDetailsViewModel.servicesList.observe(viewLifecycleOwner){
            if(!it.isEmpty()){
                service = it[0]
                updateService()
            }
        }
        subscribeService.setOnClickListener {
            val bundle = bundleOf("ServiceID" to service.id)
            findNavController().navigate(R.id.action_serviceDetailsFragment_to_serviceSubscriptionFragment, bundle)
        }
        return view
    }

    private fun updateService() {
        serviceTitle.text = service.title
        serviceDescription.text = service.description
        serviceSubscriberCount.text = service.subscriberCount.toString()+" subscribers"
        serviceRating.text = service.rating.toString()
    }




}