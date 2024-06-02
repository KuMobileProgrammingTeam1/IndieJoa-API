package org.example.indiejoaapi.artist.controller

import org.example.indiejoaapi.artist.service.ArtistService
import org.springframework.web.bind.annotation.RestController

@RestController
class ArtistController(
    private val artistService: ArtistService
)