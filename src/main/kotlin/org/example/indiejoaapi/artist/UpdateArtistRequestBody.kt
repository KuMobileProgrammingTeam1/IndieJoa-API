package org.example.indiejoaapi.artist

data class UpdateArtistRequestBody(
    val artistId: Long,
    val name: String,
    val nameEn: String,
    val nameJp: String,
    val isSolo: Boolean,
    val imageUrl: String,
    val youtubeChannelLink: String,
    val twitterLink: String,
    val youtubeVideoLink: String
)

