package com.omarismayilov.bookapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.omarismayilov.bookapp.databinding.RowCategoryBinding

class AdabterCategory : RecyclerView.Adapter<AdabterCategory.HolderCategory>, Filterable {
    private val context:Context
    public var categoryArrayList : ArrayList<ModelCategory>

    private lateinit var binding: RowCategoryBinding

    private var filterList : ArrayList<ModelCategory>

    private var filter : FilterCategory? = null


    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderCategory(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        //get data
        val model = categoryArrayList[position]
        val id = model.id
        val uid = model.uid
        val category = model.category
        val timestamp = model.timestamp

        //set data
        holder.categoryTvrv.text = category

        //click delete button
        holder.deleteButton.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete").setMessage("Are you sure want to delete this category?")
                .setPositiveButton("Yes"){a,d->
                    deleteCategory(model,holder)
                }
                .setNegativeButton("No"){a,d->
                    a.dismiss()
                }
                .show()

        }

    }

    private fun deleteCategory(model: ModelCategory, holder: HolderCategory) {
        val id = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id).removeValue().addOnSuccessListener {
            Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }



    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView) {
        var categoryTvrv:TextView = binding.categoryTvrv
        var deleteButton:ImageButton = binding.deleteBtn

    }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterCategory(filterList,this)
        }
        return filter as FilterCategory
    }


}