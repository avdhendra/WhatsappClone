package com.example.whatsappclone

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class SignUpActivity : AppCompatActivity() {
    lateinit var  userImgView:ShapeableImageView
    lateinit var nextBtn:MaterialButton
   lateinit var storage:StorageReference
   lateinit var nameEt:EditText
    val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val database by lazy {
        FirebaseFirestore.getInstance()
    }

    lateinit var downloadUrl:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    userImgView=findViewById(R.id.userImgView)
nextBtn=findViewById(R.id.nextBtn)
        nameEt=findViewById(R.id.nameEt)
        /*nameEt.addTextChangedListener {
            nextBtn.isEnabled=!(it.isNullOrEmpty())
        }*/

        nextBtn.setOnClickListener {
            nextBtn.isEnabled=false
            val name:String =nameEt.text.toString()
            if(name.isEmpty())
            {
                Toast.makeText(this,"Name Cannot be empty",Toast.LENGTH_SHORT).show()

            }
            else if(!::downloadUrl.isInitialized)
            {
                Toast.makeText(this,"Image cannot be Empty",Toast.LENGTH_SHORT).show()
            }
            else{
                val user=User(name,downloadUrl,downloadUrl,firebaseAuth.uid!!)
                database.collection("users").document(firebaseAuth.uid!!).set(user).addOnSuccessListener{
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                    overridePendingTransition(0,0)

                }.addOnCanceledListener {
                    Toast.makeText(this,"Failed to Upload",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    fun UserImage(view: android.view.View) {

        checkPermissionForImage()
        //FireBaseExtension- Image ThumbNail
    }

    private fun checkPermissionForImage() {
        if((ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)
            &&(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED))
        {
            val permission= arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissionWrite= arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission,1001)
            requestPermissions(permissionWrite,1002)

        }
        else{

            pickImageFromGallery()
        }

    }

    private fun pickImageFromGallery() {
        val intent=Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(
            intent,1000
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode==1000)
        {
            data?.data?.let {
                userImgView.setImageURI(it)
            uploadImage(it)
            }
        }
    }

    private fun uploadImage(it: Uri) {
nextBtn.isEnabled=false
        storage=FirebaseStorage.getInstance().getReference().child("uploads/"+firebaseAuth.uid.toString())
        val uploadTask=storage.putFile(it)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> { task->
            if(!task.isSuccessful)
            {
           task.exception.let {
               throw it!!
           }
            }
            return@Continuation storage.downloadUrl
        }).addOnCompleteListener{ task->
            if(task.isSuccessful) {
                downloadUrl = task.result.toString()
                nextBtn.isEnabled = true
            }
        }.addOnFailureListener {
            Toast.makeText(this,"Profile Image Failed",Toast.LENGTH_SHORT).show()
        }

    }
}