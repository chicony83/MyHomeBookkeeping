package com.chico.myhomebookkeeping.ui.dialogs.selectAsDefault.currency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.Currencies

class SelectCurrencyAsDefaultDialogAdapter(
    private val list:List<Currencies>
):RecyclerView.Adapter<SelectCurrencyAsDefaultDialogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_select_currency_as_default_dialog,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(private val item:View):RecyclerView.ViewHolder(item) {
        var textView :TextView = item.findViewById(R.id.nameCurrency)
        fun bind(currencies: Currencies) {
            textView.text = currencies.currencyName
        }
    }

}