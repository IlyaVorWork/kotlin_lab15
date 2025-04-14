package com.ilyavorontsov.lab15

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.Serializable

data class Good(
    val name: String,
    val quantity: String,
) : Serializable

class ShoppingListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var tvName: TextView
    var tvQuantity: TextView

    init {
        tvName = itemView.findViewById(R.id.tvGoodName)
        tvQuantity = itemView.findViewById(R.id.tvQuantity)
    }
}

class ShoppingListAdapter(val goods: MutableList<Good>) : RecyclerView.Adapter<ShoppingListItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.shopping_list_item, parent, false)

        val holder = ShoppingListItemHolder(view)
        view.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != NO_POSITION) {
                onItemClickListener?.invoke(pos)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ShoppingListItemHolder, position: Int) {
        holder.tvName.text = goods[position].name
        holder.tvQuantity.text = goods[position].quantity
    }

    override fun getItemCount(): Int {
        return goods.size
    }

    private var onItemClickListener: ((Int) -> Unit)? = null
    fun setOnItemClickListener(f: (Int) -> Unit) {
        onItemClickListener = f
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var btnAddGood: FloatingActionButton
    private var goods: MutableList<Good> = mutableListOf()
    private lateinit var list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = ShoppingListAdapter(goods)

        adapter.setOnItemClickListener {
            val builder = AlertDialog.Builder(this)

            val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_good, null)
            val etGoodName = view.findViewById<EditText>(R.id.etGoodNameInput)
            val etGoodQuantity = view.findViewById<EditText>(R.id.etGoodQuantityInput)

            etGoodName.setText(goods[it].name)
            etGoodQuantity.setText(goods[it].quantity)

            builder
                .setView(view)
                .setPositiveButton("Ок") { dialog, which ->
                    goods[it] = Good(etGoodName.text.toString(), etGoodQuantity.text.toString())
                    adapter.notifyItemChanged(it)
                }
                .setNegativeButton("Отмена") { dialog, which ->
                    dialog.cancel()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                goods.removeAt(pos)
                adapter.notifyItemRemoved(pos)
            }
        }

        list = findViewById(R.id.rvShoppingList)
        list.adapter = adapter

        val swipeHelper = ItemTouchHelper(swipeCallback)
        swipeHelper.attachToRecyclerView(list)

        btnAddGood = findViewById(R.id.btnAddGood)

        btnAddGood.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)

            val view = LayoutInflater.from(it.context).inflate(R.layout.dialog_add_good, null)
            val etGoodName = view.findViewById<EditText>(R.id.etGoodNameInput)
            val etGoodQuantity = view.findViewById<EditText>(R.id.etGoodQuantityInput)

            builder
                .setView(view)
                .setPositiveButton("Ок") { dialog, which ->
                    goods.add(Good(etGoodName.text.toString(), etGoodQuantity.text.toString()))
                    adapter.notifyItemInserted(goods.size - 1)
                }
                .setNegativeButton("Отмена") { dialog, which ->
                    dialog.cancel()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}