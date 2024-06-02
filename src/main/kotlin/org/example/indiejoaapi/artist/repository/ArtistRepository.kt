package org.example.indiejoaapi.artist.repository

import org.example.indiejoaapi.artist.entity.Artist
import org.springframework.data.jpa.repository.JpaRepository

interface ArtistRepository : JpaRepository<Artist, Long> {
    fun findByIndieStreetId(indieStreetId: Long): Artist?
}