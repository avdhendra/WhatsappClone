package com.example.whatsappclone

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

    lateinit var timeTv:TextView
    lateinit var subtitleTv:TextView
    lateinit var titleTv:TextView
    lateinit var userImgView:CircleImageView

    fun bind(user:User, onClick: (name:String, photo: String,id:String) -> Unit)=with(itemView){

        timeTv=findViewById(R.id.timeTv)
        subtitleTv=findViewById(R.id.subTitleTv)
        titleTv=findViewById(R.id.titleTv)
        userImgView=findViewById(R.id.userImgView)

        timeTv.isVisible=false
        titleTv.text=user.name
        subtitleTv.text=user.status
Picasso.get().load(user.thumbImage).placeholder(R.drawable.defaultavatar)
    .error(R.drawable.defaultavatar).into(userImgView)
        setOnClickListener{
            onClick.invoke(user.name,user.thumbImage,user.uid)
        }

    }

}