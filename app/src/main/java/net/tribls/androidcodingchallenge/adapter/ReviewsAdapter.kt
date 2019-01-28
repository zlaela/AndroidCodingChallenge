package adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_review.view.*
import net.tribls.androidcodingchallenge.R
import net.tribls.models.ReviewModel
import utils.inflate
import utils.loadImg

class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>() {
    private var reviews = mutableListOf<ReviewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder
    = ReviewsViewHolder(parent.inflate(R.layout.list_item_review))

    override fun getItemCount(): Int = reviews.size

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        val item = reviews[position]
        holder.bind(item)
        holder.show()
    }

    fun addItems(items: List<ReviewModel>) {
        // insert reviews before the last-index of the list
        val initPosition = getLastPosition()
        reviews.addAll(initPosition, items)
        notifyItemRangeInserted(initPosition, initPosition + items.size)
    }

    // Clear the list of posts and add a new list, appending a spinner item at the end
    fun clearAndAddItems(items: List<ReviewModel>){
        reviews.clear()
        notifyItemRangeChanged(0, getLastPosition())

        reviews.addAll(items)
        notifyItemRangeInserted(0, reviews.size)
    }

    // Filter all non-post item types and return the reviews in a list
    fun getItems(): List<ReviewModel> = reviews

    // Find the last position in the list. If lastIndex is out of bounds, assume empty list
    private fun getLastPosition() = if(reviews.lastIndex == -1) 0 else reviews.lastIndex

    class ReviewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ReviewModel) = with(itemView) {
            display_title.text = item.display_title
            mpaa_rating.text = item.mpaa_rating
            byline.text = item.byline
            publication_date.text = item.publication_date

            item.multimedia?.let {
                image.loadImg(it.src)
            }
        }
        fun show(){
            itemView.divider
        }
    }
}



