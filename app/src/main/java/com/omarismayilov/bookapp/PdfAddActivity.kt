package com.omarismayilov.bookapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.omarismayilov.bookapp.databinding.ActivityPdfAddBinding
import kotlinx.android.synthetic.main.activity_add_category.*
import kotlinx.android.synthetic.main.activity_pdf_add.*

class PdfAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfAddBinding
    //firebase auth
    private lateinit var auth:FirebaseAuth
    //progress dialog (show while uploading pdf)
    private lateinit var progressDialog: ProgressDialog
    //arraylist to hold pdf categories
    private lateinit var categoryArrayList:ArrayList<ModelCategory>
    //uri of picked pdf
    private var pdfUri : Uri? = null

    //Tag
    private val TAG = "PDF_ADD_TAG"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        loadPdfCategories()
        //setup dialogs
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }

        binding.attachPdfButton.setOnClickListener {
            pdfPickIntent()
        }

        binding.submitBtn.setOnClickListener {
            //Validate Data
            //Upload pdf to Firebase storage
            //Get url of uploaded pdf
            //Uptood Pdf info to Firebase db

            validateData()


        }

    }

    private var title = ""
    private var description = ""
    private var category = ""

    private fun validateData() {
        Log.d(TAG, "validateData: Validating data")
        // get data
        title = titleEt.text.toString().trim()
        description = descriptionEt.text.toString().trim()
        category = categoryTv.text.toString().trim()

        //validate data
        if(title.isEmpty()){
            titleEt.error = ""
        }
        else if(description.isEmpty()){
            descriptionEt.error = ""
        }
        else if(category.isEmpty()){
            Toast.makeText(this,"Choose Category",Toast.LENGTH_SHORT).show()
        }
        else if(pdfUri==null){
            Toast.makeText(this,"Pick PDF",Toast.LENGTH_SHORT).show()
        }
        else{
            //upload pdf
            uploadPdftoStorage()
        }



    }

    private fun uploadPdftoStorage() {
        Log.d(TAG, "uploadPdftoStorage: uploading to storage...")

        progressDialog.setMessage("UploadingPdf")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        val pathandname = "Books/$timestamp"
        val storageRef = FirebaseStorage.getInstance().getReference(pathandname)

        storageRef.putFile(pdfUri!!).addOnSuccessListener {taskSnapshot ->
                Log.d(TAG, "uploadPdftoStorage: Uploaded, now getting url")
                progressDialog.setMessage("Getting url...")

                val uriTask : Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);

                val uploadedPdfUrl = "${uriTask.result}"

                Toast.makeText(this,"PDF picked",Toast.LENGTH_SHORT).show()

                uploadPdfInfoToDb(uploadedPdfUrl,timestamp)


            }.addOnFailureListener{
                Log.d(TAG, "uploadPdftoStorage: Failed uploading due to ${it.localizedMessage}")
                Toast.makeText(this,"Failed to upload due to ${it.localizedMessage}",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
        }


    }

    private fun uploadPdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {
        Log.d(TAG, "uploadPdfInfoToDb: uploading to db")
        progressDialog.setMessage("Uploading pdf info to  database...")

        val uid = auth.uid

        val hashMap:HashMap<String,Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["url"] = "$uploadedPdfUrl"
        hashMap["timestamp"] = timestamp
        hashMap["viewsCount"] = 0
        hashMap["downloadsCount"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this,"Pdf uploading succesfully",Toast.LENGTH_SHORT).show()
                Log.d(TAG, "uploadPdfInfoToDb: Uploaded pdf info to db")
                progressDialog.dismiss()
                pdfUri = null
                titleEt.setText("")
                descriptionEt.setText("")
                categoryTv.text = ""
            }
            .addOnFailureListener {
                Log.d(TAG, "uploadPdfInfoToDb: Failed uploading due to ${it.localizedMessage}")
                Toast.makeText(this,"Failed to upload due to ${it.localizedMessage}",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                
                
            }


    }

    private fun loadPdfCategories() {
        Log.d(TAG,"loadPdfCategories: Loading pdf categories")
        //init arrayList
        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()

                for(ds in snapshot.children){
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun  categoryPickDialog(){
        Log.d(TAG, "categoryPickDialog: Showing pick pdf dialog")

        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)

        for (i in categoryArrayList.indices){
            categoriesArray[i] = categoryArrayList[i].category
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick category")
            .setItems(categoriesArray){dialog,which ->
                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryId = categoryArrayList[which].id

                binding.categoryTv.text = selectedCategoryTitle
                Log.d(TAG, "categoryPickDialog: Selected category id - $selectedCategoryId")
                Log.d(TAG, "categoryPickDialog: Selected category title - $selectedCategoryTitle")


            }.show()

    }

    
    private fun pdfPickIntent(){
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)

    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result ->
            if(result.resultCode == RESULT_OK){
                Log.d(TAG, "Pdf picked:: ")
                pdfUri = result.data!!.data
            }
            else{
                Log.d(TAG, "pdf pick cancelled: ")
                Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
            }

        }
    )


}