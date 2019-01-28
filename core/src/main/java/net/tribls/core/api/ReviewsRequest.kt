package net.tribls.core.api

import io.reactivex.Observable
import net.tribls.core.api_models.ReviewsData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RxReviewsRequest {
    /**
     * Movie reviews are filtered by type
     * https://api.nytimes.com/svc/movies/v2/reviews/{type}.json
     *
     * @param type - How to filter the reviews(required).
     *             - Allowed values are [all, picks]
     *
     * Movies are sent in 20-result blocks. To paginate, set offset
     * @param offset - Sets the starting point of the result set.
     *               - Must be multiple of 20
     *
     * Results can be ordered
     * @param order - How to order the results
     *              - Allowed values are [by-opening-date, by-publication-date, by-title]
     */
    @GET("{type}.json")

    // Register an endpoint from which the JSON is fetched.
    // This returns a RxJava Observable, to which an Observer can subscribe
    fun rxGetReviewsByType(
        @Path("type") type: String?,
        @Query("offset") offset: Int?,
        @Query("order") order: String?
    ) : Observable<ReviewsData>
}