package org.example.indiejoaapi.live.entity

import jakarta.persistence.*

@Entity
@Table(name = "live", schema = "PUBLIC")
class Live(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val indieStreetId: Long,
    var stageId: Long,
    var title: String,
    var description: String,
    var posterUrl: String,
    var purchaseTicketLink: String,
    var priceInfo: String,
    var startDate: String,
    var endDate: String,
)