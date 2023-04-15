package com.example.tiffindeliveryapp

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import com.example.tiffindeliveryapp.datamodels.Order
import com.example.tiffindeliveryapp.datamodels.TiffinService
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ServiceSubscriptionFragment : Fragment() {
    private lateinit var subscriptionStartDate:TextView
    private lateinit var subscriptionEndDate:TextView
    private lateinit var SubsStartDate:Date
    private lateinit var SubsEndDate:Date
    private lateinit var dayDeliveryDate:Date
    private lateinit var subscriptionTypes:RadioGroup
    private lateinit var oneDayDelivery:RadioButton
    private lateinit var dayDelivery:TextView
    private lateinit var createSubscription:RadioButton
    private lateinit var oneDayDeliveryDate:LinearLayout
    private lateinit var createSubscriptionDates:LinearLayout
    private lateinit var serviceID:String
    private lateinit var service: TiffinService
    private lateinit var tiffinTypes:RadioGroup
    private lateinit var foodPreferences:RadioGroup
    private lateinit var proceedToPayment:Button
    private val TAG = "TAG"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_subscription, container, false)
        arguments?.getString("ServiceID")?.let {
            serviceID = it
            loadService()
        }
        init(view)
        setActions()
        return view
    }

    private fun init(view:View){
        subscriptionStartDate = view.findViewById(R.id.subscription_start_date)
        subscriptionEndDate = view.findViewById(R.id.subscription_end_date)
        subscriptionTypes = view.findViewById(R.id.subscription_type_choices)
        oneDayDelivery = view.findViewById(R.id.day_delivery)
        dayDelivery = view.findViewById(R.id.one_day_delivery)
        createSubscription = view.findViewById(R.id.create_subscription)
        oneDayDeliveryDate = view.findViewById(R.id.one_day_delivery_date)
        createSubscriptionDates = view.findViewById(R.id.subscription_start_end_date)
        tiffinTypes = view.findViewById(R.id.tiffin_type_choices)
        foodPreferences = view.findViewById(R.id.food_preferences)
        proceedToPayment = view.findViewById(R.id.proceed_to_payment)
    }

    private fun setActions(){
        val myCalender = Calendar.getInstance()
        subscriptionStartDate.text = "${myCalender.get(Calendar.DAY_OF_MONTH)}-${myCalender.get(Calendar.MONTH)+1}-${myCalender.get(Calendar.YEAR)}"
        subscriptionEndDate.text = "${myCalender.get(Calendar.DAY_OF_MONTH)}-${myCalender.get(Calendar.MONTH)+1}-${myCalender.get(Calendar.YEAR)}"
        dayDelivery.text = "${myCalender.get(Calendar.DAY_OF_MONTH)}-${myCalender.get(Calendar.MONTH)+1}-${myCalender.get(Calendar.YEAR)}"
        subscriptionStartDate.setOnClickListener {
            openDatePickerDialog(subscriptionStartDate)
        }
        subscriptionEndDate.setOnClickListener {
            openDatePickerDialog(subscriptionEndDate)
        }
        subscriptionTypes.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.day_delivery -> {
                    oneDayDeliveryDate.isGone = false
                    createSubscriptionDates.isGone = true
                }
                R.id.create_subscription -> {
                    oneDayDeliveryDate.isGone = true
                    createSubscriptionDates.isGone = false
                }
            }
        }
        dayDelivery.setOnClickListener{
            openDatePickerDialog(dayDelivery)
        }
        proceedToPayment.setOnClickListener {
            findNavController().navigate(R.id.action_serviceSubscriptionFragment_to_paymentFragment)
        }
    }

    private fun openDatePickerDialog(dateView:TextView){
        val myCalender = Calendar.getInstance()
        val mDay = Calendar.DAY_OF_MONTH
        val mMonth = Calendar.MONTH
        val mYear = Calendar.YEAR
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, day)
            val format = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(format, Locale.UK)
            dateView.text = sdf.format(myCalender.time)
        }
        when(dateView){
            subscriptionStartDate -> SubsStartDate = myCalender.time
            subscriptionEndDate -> SubsEndDate = myCalender.time
            dayDelivery -> dayDeliveryDate = myCalender.time
        }
        DatePickerDialog(requireContext(), datePicker, myCalender.get(mYear), myCalender.get(mMonth), myCalender.get(mDay)).show()
    }

    private fun loadService(){
        val db = Firebase.firestore
        Log.d(TAG, "loadService: ${serviceID}")
        val list = ArrayList<TiffinService>()
        db.collection("TiffinServices")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (serviceID == document.id) {
                        val service = document.toObject(TiffinService::class.java)
                        service.id = document.id
                        list.add(service)
                    }
                }
                if(list.isNotEmpty()){
                    service = list[0]
                }else{
                    service = TiffinService()
                }
                setService()
            }
    }

    private fun setService() {
        for(s in service.tiffinTypes.keys){
            val radioButton = RadioButton(context)
            radioButton.text = "${s} - ${service.tiffinTypes.get(s)}"
            tiffinTypes.addView(radioButton)
        }
        if(service.veg){
            val radioButton = RadioButton(context)
            radioButton.text = "Veg"
            foodPreferences.addView(radioButton)
        }
        if(service.nonVeg){
            val radioButton = RadioButton(context)
            radioButton.text = "Non-Veg"
            foodPreferences.addView(radioButton)
        }

    }

}