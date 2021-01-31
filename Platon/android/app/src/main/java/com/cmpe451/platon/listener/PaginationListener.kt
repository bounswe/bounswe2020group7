package com.cmpe451.platon.listener

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Pagination allows the user to see the latest content with little wait time and for providing smooth user experience
abstract class PaginationListener(private val layoutManager: LinearLayoutManager, val PAGE_SIZE:Int=5, val PAGE_START:Int = 0) : RecyclerView.OnScrollListener() {

    // Handles scroll event of the recycler view
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        if (!isLoading && !isLastPage) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                loadMoreItems()
            }
        }
    }

    // Loads element
    protected abstract fun loadMoreItems()
    abstract var isLastPage: Boolean
    abstract var isLoading: Boolean
    abstract var currentPage: Int
}