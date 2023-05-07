package com.indexdev.partnerin.ui.superadminhome

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.indexdev.partnerin.data.model.response.ResponseGetAllUserPartnerItem
import com.indexdev.partnerin.databinding.ItemMarkerBinding

class UserAdapter(private val onClickItem: OnClickListener) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<ResponseGetAllUserPartnerItem>() {
        override fun areItemsTheSame(
            oldItem: ResponseGetAllUserPartnerItem,
            newItem: ResponseGetAllUserPartnerItem
        ): Boolean {
            return oldItem.idMitra == newItem.idMitra
        }

        override fun areContentsTheSame(
            oldItem: ResponseGetAllUserPartnerItem,
            newItem: ResponseGetAllUserPartnerItem
        ): Boolean {
            return oldItem.idMitra == newItem.idMitra
        }
    }

    private val differ = AsyncListDiffer(this, diffCallBack)
    fun submitData(value: List<ResponseGetAllUserPartnerItem>?) = differ.submitList(value)

    inner class ViewHolder(private val binding: ItemMarkerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: ResponseGetAllUserPartnerItem) {
            binding.tv1.text = data.namaUsaha
            binding.tv2.text = data.jenisUsaha
            binding.root.setOnClickListener {
                onClickItem.onClickItem(data)
            }
        }
    }

    interface OnClickListener {
        fun onClickItem(data: ResponseGetAllUserPartnerItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemMarkerBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = differ.currentList[position]
        data.let {
            holder.bind(data)
        }
    }

    override fun getItemCount() = differ.currentList.size
}