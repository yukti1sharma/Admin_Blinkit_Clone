package com.example.adminblinkitclone.viewmodels

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.adminblinkitclone.CartProducts
import com.example.adminblinkitclone.Utils
import com.example.adminblinkitclone.model.Notification
import com.example.adminblinkitclone.model.Orders
import com.example.adminblinkitclone.model.Product
import com.example.userblinkitclone.api.ApiUtilities
import com.example.userblinkitclone.models.NotificationData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class AdminViewModel : ViewModel() {

    private val _isImagesUploaded = MutableStateFlow(false)
    var isImagesUploaded: StateFlow<Boolean> = _isImagesUploaded

    private val _downloadedUrls = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    var downloadedUrls: StateFlow<ArrayList<String?>> = _downloadedUrls

    private val _isProductSaved = MutableStateFlow(false)
    var isProductSaved: StateFlow<Boolean> = _isProductSaved


    fun saveImagesInDB(imageUri : ArrayList<Uri>){

        val downloadURLs = ArrayList<String?>()

        // path define
        imageUri.forEach { uri ->
            val imageRef = FirebaseStorage.getInstance().reference.child(Utils.getCurrentUserid()).child("images").child(UUID.randomUUID().toString())
            imageRef.putFile(uri).continueWithTask {
                imageRef.downloadUrl
            }.addOnCompleteListener { task->
                val url = task.result
                downloadURLs.add(url.toString())

                if(downloadURLs.size == imageUri.size)
                {
                    _isImagesUploaded.value = true
                    _downloadedUrls.value = downloadURLs
                }
            }
        }


    }

    fun saveProduct(product: Product){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomId}").setValue(product)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productRandomId}").setValue(product)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productRandomId}").setValue(product)
                            .addOnSuccessListener {
                                _isProductSaved.value = true
                            }
                    }
            }
    }

    fun fetchAllTheProducts(category: String): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()

                for(product in snapshot.children){
                    val prod = product.getValue(Product::class.java)

                    if(category == "All" || prod?.productCategory == category)
                    {
                        products.add(prod!!)
                    }

                }

                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO: ("hghuyhjbn ")
            }

        }

        db.addValueEventListener(eventListener)

        awaitClose{db.removeEventListener(eventListener)

        }
    }

    fun savingUpdateProducts(product: Product){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productRandomId}").setValue(product)
    }

    fun getAllOrder(): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").orderByChild("orderStatus")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for (orders in snapshot.children){
                    val order = orders.getValue(Orders::class.java)
                        orderList.add(order!!)

                }
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(eventListener)
        awaitClose {
            db.removeEventListener(eventListener)
        }
    }

    fun getOrderedProducts(orderId : String) : Flow<List<CartProducts>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)

        val eventListener = object : ValueEventListener{
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(eventListener)
        awaitClose{db.removeEventListener(eventListener)}
    }

    fun updateOrderStatus(orderId: String, status : Int){
        FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId).child("orderStatus").setValue(status)
    }

    suspend fun sendNotification(orderId: String, title : String, message : String){
        val getToken = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId).child("orderingUserId").get()


        getToken.addOnCompleteListener {task ->
            val userUid = task.result.getValue(String :: class.java)
            val userToken = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(userUid!!).child("userToken").get()
            userToken.addOnCompleteListener {

                val notification = Notification(it.result.getValue(String::class.java), NotificationData(title, message))  // object creration
                ApiUtilities.notificationAPI.sendNotification(notification).enqueue(object :
                    Callback<Notification> {
                    override fun onResponse(
                        call: Call<Notification>,
                        response: Response<Notification>
                    ) {
                        if(response.isSuccessful){
                            Log.d("GGG", "Sent Notification")
                            Log.d("GGG", it.result.getValue(String::class.java).toString())
                        }
                    }

                    override fun onFailure(call: Call<Notification>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
            }

        }
    }

    fun logOutUser(){
        FirebaseAuth.getInstance().signOut()
    }
}