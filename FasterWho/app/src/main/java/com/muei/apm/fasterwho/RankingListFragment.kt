package com.muei.apm.fasterwho

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A fragment representing a list of Items.
 */
class RankingListFragment : Fragment() {

    private var columnCount = 1
    private var rank : String? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ranking_list_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                var infLim = 0
                var supLim = 0
                Log.i("heehee","$rank")
                when (arguments?.getString("rank")) {
                    "Leyenda" -> {
                        infLim = 20000
                        supLim = 10000000
                    }
                    "Diamante" -> {
                        infLim = 15000
                        supLim = 20000
                    }
                    "Oro" -> {
                        infLim = 10000
                        supLim = 15000
                    }
                    "Plata" -> {
                        infLim = 5000
                        supLim = 10000
                    }
                    "Bronce" -> {
                        infLim = 0
                        supLim = 5000
                    }
                }

                db.collection("usuarios")
                        .whereGreaterThanOrEqualTo("ptosRanking",infLim)
                        .orderBy("ptosRanking")
                        .get().addOnSuccessListener { it ->
                            val items: MutableList<ItemRanking> = ArrayList()
                            for (document in it) {
                                items.add(
                                        ItemRanking(
                                                document.get("username").toString(),
                                                document.get("ptosRanking") as Long?
                                        )
                                )
                            }
                            for (item in items) {
                                if (item.userPts!! > supLim) {
                                    items.remove(item)
                                }
                            }
                            items.reverse()
                            adapter = MyRankingListRecyclerViewAdapter(items)
                        }
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(rank: String) =
            RankingListFragment().apply {
                arguments = Bundle().apply {
                    putString("rank",rank)
                }
            }
    }
}