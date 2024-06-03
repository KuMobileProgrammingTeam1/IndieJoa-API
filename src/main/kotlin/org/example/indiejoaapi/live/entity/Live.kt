package org.example.indiejoaapi.live.entity

import jakarta.persistence.*

@Entity
@Table(name = "live", schema = "PUBLIC")
class Live(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val indieStreetId: Long,
    val stageId: Long,
    val title: String,
    val description: String,
    val posterUrl: String,
    val purchaseTicketLink: String,
    val priceInfo: String,
    val startDate: String,
    val endDate: String,
)