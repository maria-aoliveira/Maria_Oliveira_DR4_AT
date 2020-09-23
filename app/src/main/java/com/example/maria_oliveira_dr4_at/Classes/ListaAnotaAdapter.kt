package com.example.maria_oliveira_dr4_at.Classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maria_oliveira_dr4_at.R
import kotlinx.android.synthetic.main.lista_anota.view.*
import java.io.File

class ListaAnotaAdapter (val arquivos: List<InfoAnota>)
    : RecyclerView.Adapter<ListaAnotaAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view){

        val titulo = view.rcyTitulo
        val anota = view.rcyAnota
        val data = view.rcyData
        val img = view.rcyImg

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.lista_anota,
                parent,
                false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = arquivos.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val infoArq = arquivos[position]
        holder.titulo.text = infoArq.titulo
        holder.data.text = infoArq.data
        holder.anota.text = infoArq.texto
        holder.img.setImageBitmap(infoArq.imagem)
    }

}
