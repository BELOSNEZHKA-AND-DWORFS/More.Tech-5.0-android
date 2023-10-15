package com.vtb.hackaton.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.vtb.hackaton.R
import com.vtb.hackaton.domain.entity.OfficeSnippet

class OfficeListAdapter(
    private val onClick: (office: OfficeSnippet) -> Unit = {},
) :
    RecyclerView.Adapter<OfficeListAdapter.OfficeViewHolder>() {

    inner class OfficeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val makePathButton: AppCompatImageButton by lazy {
            itemView.findViewById(R.id.btnMakePath)
        }
        val callButton: AppCompatImageButton by lazy {
            itemView.findViewById(R.id.btnMakeCall)
        }
        val tvHeaderContent: TextView by lazy {
            itemView.findViewById(R.id.tvHeaderContent)
        }
        val tvWorkingTime: TextView by lazy {
            itemView.findViewById(R.id.tvWorkingTime)
        }
    }

    var listOfItems: MutableList<OfficeSnippet> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OfficeViewHolder(
        LayoutInflater.from(
            parent.context
        ).inflate(R.layout.office_info_item, parent, false)
    )

    override fun getItemCount() = listOfItems.size

    override fun onBindViewHolder(holder: OfficeViewHolder, position: Int) {
        val officeSnippet = listOfItems[position]
        holder.apply {
            itemView.apply {
                tvHeaderContent.text = officeSnippet.headContent
                tvWorkingTime.text = officeSnippet.workingTime
                setOnClickListener { onClick.invoke(officeSnippet) }
            }
        }
    }
}