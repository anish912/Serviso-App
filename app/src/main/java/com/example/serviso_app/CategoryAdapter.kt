package com.example.serviso_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private var categories: List<Category>,
    private val onClick: (Category) -> Unit
)
    : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>(){

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()  // Notify the adapter that the data has changed
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder:CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category, onClick)
    }

    override fun getItemCount(): Int =
        categories.size

    fun submitList(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val name: TextView = itemView.findViewById(R.id.textCategoryName)
        private val image: ImageView = itemView.findViewById(R.id.imageCategory)

        fun bind(category: Category, onClick: (Category) -> Unit) {
            name.text = category.name
            image.setImageResource(category.img)
            itemView.setOnClickListener { onClick(category) }
        }
    }


}