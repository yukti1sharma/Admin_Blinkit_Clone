package com.example.adminblinkitclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminblinkitclone.databinding.ItemViewProductCategoriesBinding

class CategoriesAdapter(
    private val categoryArrayList: ArrayList<com.example.adminblinkitclone.model.Category>,
    val onCategoryClicked: (com.example.adminblinkitclone.model.Category) -> Unit,
) : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>(){

    class CategoriesViewHolder(val binding : ItemViewProductCategoriesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(ItemViewProductCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = categoryArrayList[position]
        holder.binding.apply{
            ivCategoryImage.setImageResource(category.icon)
            tvCategoryTitle.text = category.category

        }

        holder.itemView.setOnClickListener {
            onCategoryClicked(category)
        }
    }
}