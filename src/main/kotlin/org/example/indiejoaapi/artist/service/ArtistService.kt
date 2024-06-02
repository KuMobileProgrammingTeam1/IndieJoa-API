package org.example.indiejoaapi.artist.service

import org.example.indiejoaapi.artist.repository.ArtistRepository
import org.springframework.stereotype.Service

@Service
class ArtistService(
    private val artistRepository: ArtistRepository
)