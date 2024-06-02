package org.example.indiejoaapi.artist.entity

import jakarta.persistence.*

@Entity
@Table(name = "artist", schema = "PUBLIC")
class Artist(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val indieStreetId: Long,
    var name: String,
    var nameEn: String,
    var nameJp: String,
    var isSolo: Boolean,
    var imageUrl: String,
    var youtubeChannelLink: String,
    var twitterLink: String,
)