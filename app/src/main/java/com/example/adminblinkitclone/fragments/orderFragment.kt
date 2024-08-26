package com.example.adminblinkitclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminblinkitclone.R
import com.example.adminblinkitclone.adapter.AdapterOrders
import com.example.adminblinkitclone.databinding.FragmentOrderBinding
import com.example.adminblinkitclone.model.OrderedItems
import com.example.adminblinkitclone.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class orderFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapterOrders : AdapterOrders

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(layoutInflater)
        getAllOrders()
        return binding.root
    }

    private fun getAllOrders() {

        binding.shimmerViewContainer.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.getAllOrder().collect{orderList->
                if(orderList.isNotEmpty()){
                    val orderedList = ArrayList<OrderedItems>()
                    for(orders in orderList){
                        val title = StringBuilder()
                        var totalPrice = 0;
                        for(products in orders.orderList!!){
                            val price = products.productPrice?.substring(1)?.toInt()  // price main se rupees sign hat jaayega
                            val itemCount = products.productCount!!
                            totalPrice += (price?.times(itemCount)!!)

                            title.append("${products.productCategory}, ")
                        }

                        val orderedItems = OrderedItems(orders.orderId, orders.orderDate, orders.orderStatus, title.toString(), totalPrice, orders.userAddress)
                        orderedList.add(orderedItems)
                    }
                    adapterOrders = AdapterOrders(requireContext(), ::onOrderItemViewClicked)
                    binding.rvOrders.adapter = adapterOrders
                    adapterOrders.differ.submitList(orderedList)

                    binding.shimmerViewContainer.visibility = View.GONE
                }

            }
        }

    }

    fun onOrderItemViewClicked(orderedItems: OrderedItems){
        val bundle = Bundle()
        bundle.putInt("status", orderedItems.itemStatus!!)
        bundle.putString("orderId", orderedItems.orderId)
        bundle.putString("userAddress", orderedItems.userAddress)

        findNavController().navigate(R.id.action_orderFragment_to_orderDetailsFragment, bundle)
    }

}