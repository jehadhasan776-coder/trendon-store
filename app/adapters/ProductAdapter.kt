package com.trendonstore.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.trendonstore.aws.dynamodb.Product

/**
 * محول RecyclerView لعرض قائمة المنتجات
 * Product List Adapter
 */
class ProductAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.product_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.product_price)
        private val ratingTextView: TextView = itemView.findViewById(R.id.product_rating)
        private val imageView: ImageView = itemView.findViewById(R.id.product_image)
        private val stockTextView: TextView = itemView.findViewById(R.id.product_stock)

        fun bind(product: Product) {
            nameTextView.text = product.name
            priceTextView.text = "${product.price} د.ا"
            ratingTextView.text = "⭐ ${product.rating}"
            stockTextView.text = "المتوفر: ${product.stock}"
            
            Picasso.get()
                .load(product.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(imageView)
            
            itemView.setOnClickListener { onItemClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
