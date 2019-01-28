package net.tribls.core
/**
 * https://developer.nytimes.com/docs/movie-reviews-api/1/overview
 *
 * All URIs are relative to the base url [BASE_URL]
 * To get the list of reviews, [REVIEWS_URL] is used in the API
 *  - Another option is to get movie critic names, in which case [CRITICS_URL] can be used
 */
const val BASE_URL = "http://api.nytimes.com/svc/movies/v2/"
const val REVIEWS_URL = "${BASE_URL}reviews/"
const val CRITICS_URL = "${BASE_URL}critics/"

const val KEY = "KoRB4K5LRHygfjCL2AH6iQ7NeUqDAGAB"
