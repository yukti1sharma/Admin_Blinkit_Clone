package com.example.adminblinkitclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminblinkitclone.R
import com.example.adminblinkitclone.Utils
import com.example.adminblinkitclone.adapter.AdapterCartProducts
import com.example.adminblinkitclone.databinding.FragmentOrderDetailsBinding
import com.example.adminblinkitclone.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class orderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private var status = 0
    private var currentStatus = 0
    private var orderId = ""
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderDetailsBinding.inflate(layoutInflater)

        getValues()
        settingStatus(status)
        onBackButtonClicked()
        lifecycleScope.launch{getOrderedProducts()}
        onChangeStatusButtonClicked()

        return binding.root
    }

    private fun onChangeStatusButtonClicked() {
        binding.btnChangeStatus.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener {menu ->
                when(menu.itemId){
                    R.id.menuReceived -> {
                        currentStatus = 1
                        if(currentStatus > status){
                            status = 1
                            settingStatus(1)
                            viewModel.updateOrderStatus(orderId, 1)
                            lifecycleScope.launch {
                                viewModel.sendNotification(orderId, "Received", "Your order is received")
                            }
                        }
                        else{
                            Utils.showToast(requireContext(), "Order is already received....")
                        }
                        true
                    }

                    R.id.menuDispatched -> {
                        currentStatus = 2
                        if(currentStatus > status){
                            status = 2
                            settingStatus(2)
                            viewModel.updateOrderStatus(orderId, 2)
                            lifecycleScope.launch {
                                viewModel.sendNotification(orderId, "Dispatched", "Your order is dispatched")
                            }
                        }
                        else{
                            Utils.showToast(requireContext(), "Order is already dispatched....")
                        }
                        true
                    }

                    R.id.menuDelivered -> {
                        currentStatus = 3
                        if(currentStatus > status){
                            status = 3
                            settingStatus(3)
                            viewModel.updateOrderStatus(orderId, 3)
                            lifecycleScope.launch {
                                viewModel.sendNotification(orderId, "Delivered", "Your order is delivered")
                            }
                        }
                        true
                    }
                    else -> {false}
                }
            }
        }
    }

    private fun getOrderedProducts() {
        lifecycleScope.launch {
            viewModel.getOrderedProducts(orderId).collect{cartList->
                adapterCartProducts = AdapterCartProducts()
                binding.rvProductItems.adapter = adapterCartProducts
                adapterCartProducts.differ.submitList(cartList)
            }
        }

    }

    private fun settingStatus(status : Int) {
        val viewsMap = mapOf(
            0 to listOf(binding.iv1),
            1 to listOf(binding.iv1, binding.iv2, binding.view1),
            2 to listOf(binding.iv1, binding.iv2, binding.view1, binding.iv3, binding.view2),
            3 to listOf(binding.iv1, binding.iv2, binding.view1, binding.iv3, binding.view2, binding.iv4, binding.view3)
        )

        val viewsToInt = viewsMap.getOrDefault(status, emptyList())

        for(view in viewsToInt){
            view.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
        }
    }


    private fun getValues() {
        val bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderId").toString()
        binding.tvUserAddress.text = bundle.getString("userAddress").toString()
    }

    private fun onBackButtonClicked() {
        binding.TbOrdersDetailFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailsFragment_to_orderFragment)

        }
    }

}