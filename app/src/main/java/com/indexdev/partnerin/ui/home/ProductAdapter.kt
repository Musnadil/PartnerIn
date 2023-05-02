package com.indexdev.partnerin.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.indexdev.partnerin.data.model.response.ResponseProductByIdMitra
import com.indexdev.partnerin.databinding.ItemProductBinding
import com.indexdev.partnerin.ui.currency

class ProductAdapter(private val onClickItem: OnClickListener) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    private val diffCallBack = object : DiffUtil.ItemCallback<ResponseProductByIdMitra>() {
        override fun areItemsTheSame(
            oldItem: ResponseProductByIdMitra,
            newItem: ResponseProductByIdMitra
        ): Boolean {
            return oldItem.idProduk == newItem.idProduk
        }

        override fun areContentsTheSame(
            oldItem: ResponseProductByIdMitra,
            newItem: ResponseProductByIdMitra
        ): Boolean {
            return oldItem.idProduk == newItem.idProduk
        }
    }
    private val differ = AsyncListDiffer(this, diffCallBack)
    fun submitData(value: List<ResponseProductByIdMitra>?) = differ.submitList(value)

    inner class ViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResponseProductByIdMitra) {
            Glide.with(binding.root)
                .load("http://192.168.0.107:8080/gambar/${data.gambar}")
                .transform(CenterCrop())
                .into(binding.ivProduct)
            binding.tvProductName.text = data.namaProduk
            binding.tvPrice.text = currency(data.harga.toInt())
            binding.root.setOnClickListener {
                onClickItem.onClickItem(data)
            }
        }
    }

    interface OnClickListener {
        fun onClickItem(data: ResponseProductByIdMitra)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemProductBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = differ.currentList[position]
        data.let {
            holder.bind(data)
        }
    }

    override fun getItemCount() = differ.currentList.size
}