package com.example.salesapp.Cart

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.salesapp.GetCartResponse
import com.example.salesapp.R
import com.example.salesapp.SharedViewModel.SharedViewModel
import com.example.salesapp.databinding.CartRvListItemBinding

class CartAdapter(
    private val cartViewModel: CartViewModel,
    private val sharedViewModel: SharedViewModel,
    private val context: Context
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    val cartList = mutableListOf<GetCartResponse>()
    private var onItemClickCallback: OnItemClickCallback? = null
    private var onItemCheckedCallback: OnItemCheckedCallback? = null

    interface OnItemClickCallback {
        fun onRemoveCartClicked(salesUsername: String, product_id: Int)
    }

    interface OnItemCheckedCallback {
        fun onItemChecked(item: GetCartResponse, isChecked: Boolean)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setOnItemCheckedCallback(onItemCheckedCallback: OnItemCheckedCallback) {
        this.onItemCheckedCallback = onItemCheckedCallback
    }

    inner class CartViewHolder(private val binding: CartRvListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val cartImage = binding.cartImage
        private val cartName = binding.cartName
        private val cartPrice = binding.cartPrice
        private val cartQuantity = binding.cartQtyTv
        private val cartIncrementButton = binding.cartIncBtn
        private val cartDecrementButton = binding.cartDecBtn
        private val cartCheckBox = binding.cartCheckbox

        private val removeCartButton = binding.removeCartBtn

        init {
            removeCartButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val cart = cartList[position]
                    onItemClickCallback?.onRemoveCartClicked(
                        sharedViewModel.salesUsername,
                        cart.product_id
                    )
                }
            }

            cartCheckBox.setOnCheckedChangeListener { _, isChecked ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val cart = cartList[position]
                    cart.isChecked = isChecked
                    onItemCheckedCallback?.onItemChecked(cart, isChecked)
                    cartViewModel.updateTotalPrice()

                }
            }
        }

        fun bind(response: GetCartResponse) {
            with(binding) {
                Glide.with(cartImage.context)
                    .load(response.product_img)
                    .into(cartImage)
                cartName.text = response.product_name
                cartPrice.text = context.getString(R.string.price_tag, response.product_price.toString())
                cartQuantity.text = response.qty.toString()

                cartIncrementButton.setOnClickListener {
                    cartViewModel.incrementQuantity(response)
                    cartQuantity.text = response.qty.toString()
                }

                cartDecrementButton.setOnClickListener {
                    cartViewModel.decrementQuantity(response)
                    cartQuantity.text = response.qty.toString()
                }

                cartCheckBox.isChecked = response.isChecked
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartRvListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = cartList.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]
        holder.bind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCartProducts(cartProduct: List<GetCartResponse>) {
        cartList.clear()
        cartList.addAll(cartProduct)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAllItemsChecked(checked: Boolean) {
        for (item in cartList) {
            item.isChecked = checked
        }
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<GetCartResponse> {
        return cartList.filter { it.isChecked }
    }

}