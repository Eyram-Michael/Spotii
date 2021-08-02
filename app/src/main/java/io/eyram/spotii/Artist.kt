package io.eyram.spotii

import com.squareup.moshi.Json

data class SearchResult(
    val artists: PagingObject?,
)

data class PagingObject(
    val href: String,
    @Json(name = "items")
    val artists: List<Artist>,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int


)

data class Artist(
    @Json(name = "external_urls")
    val externalUrls: ExtUrl,
    val followers: Followers,
    val genres: List<String>,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val popularity: Int,
    val type: String,
    val uri: String
) {
    data class Followers(
        val href: String?,
        val total: Int
    )

    data class Image(
        val height: Int?,
        val url: String,
        val width: Int?
    )

    data class ExtUrl(
        val spotify: String
    )
}
