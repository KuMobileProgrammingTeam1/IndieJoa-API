package org.example.indiejoaapi.live.repository

import org.example.indiejoaapi.live.entity.Live
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository

interface LiveRepository : JpaRepository<Live, Long> {
    fun findByIndieStreetId(indieStreetId: Long): Live?
    fun findByTitleContains(title: String, pageRequest: PageRequest): Page<Live>
    fun findByTitleContainsOrderByStartDateDesc(
        title: String,
        pageRequest: PageRequest
    ): Page<Live>
}