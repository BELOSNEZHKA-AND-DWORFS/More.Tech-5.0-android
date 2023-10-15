package com.vtb.hackaton.adapters

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.vtb.hackaton.R
import com.vtb.hackaton.domain.entity.OfficeFilter

class OfficeFiltersAdapter :
    RecyclerView.Adapter<OfficeFiltersAdapter.FilterViewHolder>() {
    val itemStateArray = SparseBooleanArray()

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filterSpecificButton: Button by lazy {
            itemView.findViewById(R.id.filterSpecific)
        }

        fun bind(position: Int) {
            if (itemStateArray[position]) {
                filterSpecificButton.setBackgroundColor(itemView.context.getColor(R.color.purple_700))
            } else {
                filterSpecificButton.setBackgroundColor(itemView.context.getColor(R.color.gray))
            }
        }
    }

    var listOfItems: MutableList<OfficeFilter> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FilterViewHolder(
        LayoutInflater.from(
            parent.context
        ).inflate(R.layout.office_filter_item, parent, false)
    )

    override fun getItemCount() = listOfItems.size

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = listOfItems[position]
        holder.apply {
            itemView.apply {
                filterSpecificButton.text = filter.specific
                setOnClickListener {
                    if (itemStateArray[position]){
                        itemStateArray.append(position, false)
                    } else {
                        itemStateArray.append(position, true)
                    }
                    holder.bind(position)
                    notifyItemChanged(position)
                }
            }
        }
    }
}