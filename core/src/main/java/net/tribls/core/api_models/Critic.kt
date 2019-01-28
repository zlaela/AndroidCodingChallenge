package net.tribls.core.api_models

/**
 * The response contains:
 * @param status        - 200 = OK, 401 = Unauthorized request. Set api-key. 429: Too many requests
 * @param copyright     - Copyright to NYT
 * @param has_more      - Whether there are more reviews to load
 * @param num_results   - Number of results returned
 * @param results       - An object that contains an array of Critic objects
 */
class CriticData(
    val status: String,
    val copyright: String,
    val has_more: Boolean,
    val num_results: Int,
    val results: Array<Critic>
)

class Critic(
    val bio: String,
    val display_name: String,
    val multimedia: MultiMedia,
    val `seo-nmae`: String,   // TODO: typo???
    val sort_name: String,
    val status: String
)