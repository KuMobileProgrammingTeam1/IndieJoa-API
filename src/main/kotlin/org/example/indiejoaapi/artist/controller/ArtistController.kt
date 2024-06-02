package org.example.indiejoaapi.artist.controller

import io.swagger.v3.oas.annotations.Operation
import org.example.indiejoaapi.artist.service.ArtistService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArtistController(
    private val artistService: ArtistService
) {
    @Operation(summary = "아티스트 크롤링", description = "아티스트를 크롤링하여 저장합니다.")
    @PostMapping("/artist/crawl")
    fun crawlArtist() {
        artistService.crawlAllArtists()
    }
}