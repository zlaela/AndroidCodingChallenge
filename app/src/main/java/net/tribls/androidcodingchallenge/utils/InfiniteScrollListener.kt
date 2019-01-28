package utils

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

/**
 * Receives a function (get the reviews, in this case) as the parameter.
 * The function will be invoked every time
 * we get to the end of the recyclerview (minus the specified threshold amount)
 * Here, I add new reviews when there are 5 revs left until the end of the list
 **/
class InfiniteScrollListener(
    private val func: () -> Unit,
    private val layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    private var previousTotal = 0
    private var loading = true
    private var visibleItemsThreshold = 5
    private var firstVisibleItem = 0
    private var visibleItemsCount = 0
    private var totalItemsCount = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if(dy > 1) {
            visibleItemsCount = recyclerView.childCount
            totalItemsCount = recyclerView.layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

            if(loading) {
                if(totalItemsCount > previousTotal) {
                    loading = false
                    previousTotal = totalItemsCount
                }
            }

            if(!loading &&
                (totalItemsCount - visibleItemsCount)
                <= (firstVisibleItem + visibleItemsThreshold)){
                // We reached the end
                Log.i(InfiniteScrollListener::class.java.canonicalName, "Reached the end")
                func()  // <-- run the passed in function (requestPosts)
                recyclerView.recycledViewPool.clear()
                loading = true
            }
        }
    }
}