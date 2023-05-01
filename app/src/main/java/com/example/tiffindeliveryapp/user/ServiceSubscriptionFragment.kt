package com.example.tiffindeliveryapp.user

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
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
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import com.example.tiffindeliveryapp.R
import com.example.tiffindeliveryapp.datamodels.Order
import com.example.tiffindeliveryapp.datamodels.TiffinService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

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
        setActions(view)
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

    private fun setActions(view:View){
        val myCalender = Calendar.getInstance()
        subscriptionStartDate.text = "${myCalender.get(Calendar.DAY_OF_MONTH)}-${myCalender.get(Calendar.MONTH)+1}-${myCalender.get(Calendar.YEAR)}"
        subscriptionEndDate.text = "${myCalender.get(Calendar.DAY_OF_MONTH)}-${myCalender.get(Calendar.MONTH)+1}-${myCalender.get(Calendar.YEAR)}"
        dayDelivery.text = "${myCalender.get(Calendar.DAY_OF_MONTH)}-${myCalender.get(Calendar.MONTH)+1}-${myCalender.get(Calendar.YEAR)}"
        SubsStartDate = myCalender.time
        SubsEndDate = myCalender.time
        dayDeliveryDate = myCalender.time
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
            val tiffinType = view.findViewById<RadioButton>(tiffinTypes.checkedRadioButtonId).text.toString()
            val preference = view.findViewById<RadioButton>(foodPreferences.checkedRadioButtonId).text.toString()
            val addressLine1 = view.findViewById<EditText>(R.id.address_line1)
            val addressLine2 = view.findViewById<EditText>(R.id.address_line2)
            val addressDistrict = view.findViewById<EditText>(R.id.address_district)
            val addressState = view.findViewById<EditText>(R.id.address_state)
            val addressPincode = view.findViewById<EditText>(R.id.address_pincode)
            val address = HashMap<String, String>()
            val customOrder = view.findViewById<EditText>(R.id.custom_order).text.toString()
            address.put(addressLine1.hint.toString(), addressLine1.text.toString())
            address.put(addressLine2.hint.toString(), addressLine2.text.toString())
            address.put(addressDistrict.hint.toString(), addressDistrict.text.toString())
            address.put(addressState.hint.toString(), addressState.text.toString())
            address.put(addressPincode.hint.toString(), addressPincode.text.toString())
            when(subscriptionTypes.checkedRadioButtonId){
                R.id.day_delivery -> {
                    val order = Firebase.auth.currentUser?.uid?.let { it1 ->
                        Order(
                            serviceID,
                            it1,
                            dayDeliveryDate,
                            dayDeliveryDate,
                            tiffinType,
                            preference,
                            address,
                            false,
                            "COD",
                            customOrder
                        )
                    }
                    saveOrder(order)
                }                R.id.create_subscription -> {
                    val order = Firebase.auth.currentUser?.uid?.let { it1 ->
                        Order(
                            serviceID,
                            it1,
                            SubsStartDate,
                            SubsEndDate,
                            tiffinType,
                            preference,
                            address,
                            false,
                            "COD",
                            customOrder
                        )
                    }
                saveOrder(order)
                }            }
            findNavController().navigate(R.id.action_serviceSubscriptionFragment_to_paymentFragment)
        }
    }

    private fun saveOrder(order:Order?) {
        val mPrefs = activity?.getSharedPreferences("order", Context.MODE_PRIVATE)
        val editor = mPrefs?.edit()
        val gson = Gson()
        val json = gson.toJson(order)
        editor?.putString("currentOrder", json)
        editor?.commit()
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
            when(dateView){
                subscriptionStartDate -> SubsStartDate = myCalender.time
                subscriptionEndDate -> SubsEndDate = myCalender.time
                dayDelivery -> dayDeliveryDate = myCalender.time
            }
        }
        DatePickerDialog(requireContext(), datePicker, myCalender.get(mYear), myCalender.get(mMonth), myCalender.get(mDay)).show()
    }

    private fun loadService(){
        val db = Firebase.firestore
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