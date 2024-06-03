package org.example.indiejoaapi.stage.entity

import jakarta.persistence.*

@Entity
@Table(name = "stage", schema = "PUBLIC")
class Stage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val indieStreetId: Long,
    val name: String,
    val address: String,
    val placeLink: String,
    val youtubeChannelLink: String,
)