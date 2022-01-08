package com.example.whatsappclone

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserChat(itemview: View): RecyclerView.ViewHolder(itemview) {
    lateinit var timeTv: TextView
    lateinit var subtitleTv: TextView
    lateinit var titleTv: TextView
    lateinit var userImgView: ShapeableImageView
    lateinit var countTv:TextView
    fun bind(user:User)=with(itemView){
        timeTv=findViewById(R.id.timeTv)
        subtitleTv=findViewById(R.id.subTitleTv)
        titleTv=findViewById(R.id.titleTv)
        userImgView=findViewById(R.id.userImgView)
        countTv=findViewById(R.id.countTv)

        countTv.isVisible = false
        timeTv.isVisible = false

        titleTv.text = user.name
        subtitleTv.text = user.status
        Picasso.get()
            .load(user.thumbImage)
            .placeholder(R.drawable.defaultavatar)
            .error(R.drawable.defaultavatar)
            .into(userImgView)

    }
}