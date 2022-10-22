package com.omarismayilov.bookapp

import android.annotation.SuppressLint
import android.widget.Filter


class FilterCategory: Filter {
    private var filterList:ArrayList<ModelCategory>
    private var adapterCategory:AdabterCategory


    constructor(filterList: ArrayList<ModelCategory>, adapterCategory: AdabterCategory) : super() {
        this.filterList = filterList
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        if(constraint != null && constraint.isNotEmpty()){

            constraint = constraint.toString().uppercase()

            val filteredModels:ArrayList<ModelCategory> = ArrayList()

            for(i in 0 until filterList.size){
                //validate
                if(filterList[i].category.uppercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            results.count = filterList.size
            results.values =filterList
        }

        return results

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun publishResults(constraint: CharSequence?, results: FilterResults) {

        adapterCategory.categoryArrayList = results.values as ArrayList<ModelCategory>

        adapterCategory.notifyDataSetChanged()

    }
}