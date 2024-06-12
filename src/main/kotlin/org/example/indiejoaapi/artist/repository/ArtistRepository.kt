package org.example.indiejoaapi.artist.repository

import org.example.indiejoaapi.artist.entity.Artist
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository

interface ArtistRepository : JpaRepository<Artist, Long> {
    fun findByIndieStreetId(indieStreetId: Long): Artist?
    fun findByNameContains(name: String, pageRequest: PageRequest): Page<Artist>
}