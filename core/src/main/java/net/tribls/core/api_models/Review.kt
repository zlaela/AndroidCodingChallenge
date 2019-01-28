package net.tribls.core.api_models

/**
 * The response contains:
 * @param status        - 200 = OK, 401 = Unauthorized request. Set api-key. 429: Too many requests
 * @param copyright     - Copyright to NYT
 * @param has_more      - Whether there are more reviews to load
 * @param num_results   - Number of results returned
 * @param results       - An object that contains an array of Review objects
 */
class ReviewsData(
    val status: String,
    val copyright: String,
    val has_more: Boolean,
    val num_results: Int,
    val results: Array<Review>
)

class Review(
    val display_title: String,
    val mpaa_rating: String,
    val byline: String,
    val headline: String,
    val summary_short: String,
    val publication_date: String,
    val multimedia: MultiMedia
)