package com.example.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
private const val DELETED_VIEW_TYPE=1
private const val NORMAL_VIEW_TYPE=2

class contact_list : AppCompatActivity() {
    lateinit var mAdapter: FirestorePagingAdapter<User, RecyclerView.ViewHolder>
    lateinit var recyclerView: RecyclerView
    val auth by lazy {
        FirebaseAuth.getInstance()

    }
    val  database by lazy {
        FirebaseFirestore.getInstance().collection("users").orderBy("name", Query.Direction.ASCENDING)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)
        recyclerView= findViewById(R.id.recyclerviewcontact)
setupAdapter()
recyclerView.apply {
    layoutManager=LinearLayoutManager(applicationContext)
    adapter=mAdapter
}
    }

    private fun setupAdapter() {
        val config= PagingConfig(20,2,false)
        val options=FirestorePagingOptions.Builder<User>().setLifecycleOwner(this).setQuery(database,config,User::class.java).build()
        mAdapter=object:FirestorePagingAdapter<User,RecyclerView.ViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return when(viewType){
                    NORMAL_VIEW_TYPE->UserHolder(layoutInflater.inflate(R.layout.contactitem,parent,false))
                else -> EmptyViewHolder(layoutInflater.inflate(R.layout.empty_view,parent,false))
                }

            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: User) {
               if(holder is UserHolder)
               {
                   holder.bind(user = model){ name:String,photo:String,id:String ->

val intent= Intent(applicationContext,ChatBox::class.java)
                       intent.putExtra(UID,id)
                       intent.putExtra(NAME,name)
                       intent.putExtra(IMAGE,photo)
startActivity(intent)

                   }
               }
                else{

               }
            }

            override fun getItemViewType(position: Int): Int {

                val item=getItem(position)?.toObject(User::class.java)
                return if(auth.uid==item!!.uid){
                            DELETED_VIEW_TYPE
                }else{
                  NORMAL_VIEW_TYPE
                }
            }

        }
        mAdapter.addLoadStateListener {

        }

    }

}