package net.tribls.androidcodingchallenge

import adapter.ReviewsAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.tribls.androidcodingchallenge.utils.BROADCAST_CONNECTIVITY_GAINED
import net.tribls.androidcodingchallenge.utils.BROADCAST_CONNECTIVITY_LOST
import net.tribls.models.ReviewsList
import net.tribls.presentation.NetworkStateChecker
import net.tribls.presentation.PresenterImpl
import net.tribls.presentation.ReviewsView
import utils.InfiniteScrollListener
import utils.inflate
import java.io.File

class ReviewsFragment: Fragment(), ReviewsView, NetworkStateChecker {
    private lateinit var adapter : ReviewsAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var presenter: PresenterImpl
    private lateinit var sadOverlay: View
    private var reviewsList: ReviewsList? = null
    private var offset = 0
    private val connectivityChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            // When we received the broadcast
            intent?.let {
                if(it.action ==BROADCAST_CONNECTIVITY_LOST){
                    onNetworkLost()
                } else if(it.action == BROADCAST_CONNECTIVITY_GAINED) {
                    onNetworkAvailable()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = container?.inflate(R.layout.fragment_reviews)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            // Register a LocalBroadcast listener
            val filter = IntentFilter()
            filter.addAction(BROADCAST_CONNECTIVITY_LOST)
            filter.addAction(BROADCAST_CONNECTIVITY_GAINED)
            LocalBroadcastManager.getInstance(it).registerReceiver(
                connectivityChangeReceiver, filter
            )
            // init the presenter with callback and a file for caching
            presenter = PresenterImpl(this, getOrMakeCacheFile())
        }

        sadOverlay = view.findViewById(R.id.sad_overlay)
        recyclerView = view.findViewById(R.id.recyclerview)
        setupRecyclerView()

        // Check for stored reviews
        savedInstanceState?.let { bundle ->
            if(bundle.containsKey(MOVIE_REVIEWS)){
                reviewsList = bundle.get(MOVIE_REVIEWS) as ReviewsList
                reviewsList?.let { list ->
                    // Get the next 20 from the model
                    offset = list.lastOffset
                    // Clear the displayed reviews and add the ones we stored
                    adapter.clearAndAddItems(list.reviews)
                }
            }
        } ?: run {
            // Get fresh reviews if savedInstanceState is null
            presenter.getReviewsForType("all", 0,"by_opening-date")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // When the view is destroyed, save the list of reviews we've seen here
        val reviews = adapter.getItems()
        if(reviewsList != null && reviews.isNotEmpty()){
            outState.putParcelable(MOVIE_REVIEWS, reviewsList?.copy(reviews = reviews))
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.setView(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.clearView()
    }

    override fun onReviewsAvailable(reviews: ReviewsList) {
        adapter.addItems(reviews.reviews)
        adapter.notifyDataSetChanged()
        reviewsList = reviews
        offset = reviews.lastOffset
        sadOverlay.visibility = View.GONE
    }

    override fun onNetworkLost() {
        view?.let {
            Snackbar
                .make(it, "No Network", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry") {
                    getReviews()
                }
                .show()
        }
    }

    private fun onNetworkAvailable() {
        getReviews()
    }

    override fun onNoCache() {
        sadOverlay.visibility = View.VISIBLE
        view?.let {
            Snackbar
                .make(it, "No Cache", Snackbar.LENGTH_INDEFINITE)
                .show()
        }
    }

    override fun isOnline(): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = ReviewsAdapter()
        recyclerView.adapter = adapter

        // add Infinite Scroll to the recycler view
        recyclerView.clearOnScrollListeners()
        recyclerView.addOnScrollListener(
            InfiniteScrollListener({
                getReviews()
            },
                recyclerView.layoutManager as LinearLayoutManager
            )
        )
    }

    private fun getOrMakeCacheFile() : File {
        val files = File(activity?.getExternalFilesDir(null), "cache")
        return if (!files.mkdirs()) {
            File(activity?.getExternalFilesDir(null), "cache")
        } else {
            files
        }
    }

    private fun getReviews() {
        presenter.getReviewsForType("all", offset, "by_opening-date")
    }

    companion object {
        private const val MOVIE_REVIEWS = "MOVIE_REVIEWS"

        @JvmStatic
        fun newInstance() = ReviewsFragment()
    }
}