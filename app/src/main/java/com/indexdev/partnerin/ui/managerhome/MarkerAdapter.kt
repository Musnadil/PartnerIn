package com.indexdev.partnerin.ui.managerhome

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.indexdev.partnerin.data.model.response.ResponseGetMarkerItem
import com.indexdev.partnerin.databinding.ItemMarkerBinding

@SuppressLint("SetTextI18n")
class MarkerAdapter(private val onClickItem: OnClickListener) :
    RecyclerView.Adapter<MarkerAdapter.ViewHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<ResponseGetMarkerItem>() {
        override fun areItemsTheSame(
            oldItem: ResponseGetMarkerItem,
            newItem: ResponseGetMarkerItem
        ): Boolean {
            return oldItem.idPoi == newItem.idPoi
        }

        override fun areContentsTheSame(
            oldItem: ResponseGetMarkerItem,
            newItem: ResponseGetMarkerItem
        ): Boolean {
            return oldItem.idPoi == newItem.idPoi
        }
    }

    private val differ = AsyncListDiffer(this, diffCallBack)
    fun submitData(value: List<ResponseGetMarkerItem>?) = differ.submitList(value)

    inner class ViewHolder(private val binding: ItemMarkerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResponseGetMarkerItem) {
            binding.tv1.text = data.namaFasilitas
            binding.tv2.text = "Lat: ${data.lat}, Long: ${data.longi}"
            binding.root.setOnClickListener {
                onClickItem.onClickItem(data)
            }
        }
    }

    interface OnClickListener {
        fun onClickItem(data: ResponseGetMarkerItem)
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