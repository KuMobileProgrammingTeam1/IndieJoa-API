package org.example.indiejoaapi.live.repository

import org.example.indiejoaapi.live.entity.Live
import org.springframework.data.jpa.repository.JpaRepository

interface LiveRepository : JpaRepository<Live, Long> {
    fun findByIndieStreetId(indieStreetId: Long): Live?
}