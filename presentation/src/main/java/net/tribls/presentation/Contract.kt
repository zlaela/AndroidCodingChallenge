package net.tribls.presentation

import net.tribls.models.ReviewsList

interface ReviewsView {
    /**
     * Called when we have reviews
     */
    fun onReviewsAvailable(reviews: ReviewsList)

    /**
     * Called when we have lost connectivity
     */
    fun onNetworkLost()

    /**
     * Called when we have no cached items
     */
    fun onNoCache()
}

interface ReviewsPresenter {
    /**
     * Get the reviews for the given type
     * @param type - the review filter. Allowed values are [all, picks]
     */
    fun getReviewsForType(type: String, offset: Int, order: String)

}