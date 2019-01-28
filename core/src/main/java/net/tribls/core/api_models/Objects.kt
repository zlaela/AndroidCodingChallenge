package net.tribls.core.api_models

/**
 * A representation of the multimedia JSON object returned from the API
 */
data class MultiMedia(
    var height: Int,
    var src: String,
    var type: String,
    var width: Int
)

/**
 * A representation of the link JSON object
 */
data class Link(
    var suggested_link_text: String,
    var type: String,
    var url: String
)