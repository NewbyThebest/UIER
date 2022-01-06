package com.newbie.uier

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CourseAdapter(private val context:Context, val data:List<CidBean>) : RecyclerView.Adapter<CourseAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title : TextView = itemView.findViewById(R.id.tv_title)
        val duration : TextView = itemView.findViewById(R.id.tv_duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_course,parent,false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = data[position].part
        holder.duration.text = data[position].duration
    }

    override fun getItemCount(): Int = data.size

}