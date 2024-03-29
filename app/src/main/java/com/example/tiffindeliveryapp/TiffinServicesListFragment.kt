package com.example.tiffindeliveryapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiffindeliveryapp.adapters.TiffinServiceListAdapter
import com.example.tiffindeliveryapp.datamodels.TiffinService
import com.example.tiffindeliveryapp.viewmodels.TiffinServicesListViewModel

class TiffinServicesListFragment : Fragment() {
    private lateinit var tiffinServicesList:RecyclerView
    private lateinit var tiffinServiceListAdapter: TiffinServiceListAdapter
    private lateinit var tiffinServicesListViewModel: TiffinServicesListViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tiffin_services_list, container, false)
        tiffinServicesListViewModel = ViewModelProvider(this).get(TiffinServicesListViewModel::class.java)
        setObserverOnServicesList()
        tiffinServicesList = view.findViewById(R.id.tiffin_services_list)
        tiffinServicesListViewModel.servicesList.value?.let {
            tiffinServiceListAdapter = TiffinServiceListAdapter(it)
        }
        tiffinServicesList.adapter = tiffinServiceListAdapter
        tiffinServicesList.layoutManager = LinearLayoutManager(context)
        setContactClickAction()
        return view
    }

    private fun setObserverOnServicesList() {
        tiffinServicesListViewModel.servicesList.observe(viewLifecycleOwner){
            tiffinServiceListAdapter.onDatasetChanged(it)
        }
    }

    private fun setContactClickAction(){
        tiffinServiceListAdapter.setOnItemClickListener(object:TiffinServiceListAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val bundle = bundleOf("ServiceID" to "${tiffinServicesListViewModel.servicesList.value?.get(position)?.id}")
                findNavController().navigate(R.id.action_tiffinServicesListFragment_to_serviceDetailsFragment, bundle)
            }
        })
    }
}