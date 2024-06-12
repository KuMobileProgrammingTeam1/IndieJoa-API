package org.example.indiejoaapi.artist.controller

import io.swagger.v3.oas.annotations.Operation
import org.example.indiejoaapi.artist.UpdateArtistRequestBody
import org.example.indiejoaapi.artist.entity.Artist
import org.example.indiejoaapi.artist.service.ArtistService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
class ArtistController(
    private val artistService: ArtistService
) {
    @Operation(summary = "아티스트 크롤링", description = "아티스트를 크롤링하여 저장합니다.")
    @PostMapping("/artist/crawl")
    fun crawlArtist() {
        artistService.crawlAllArtists()
    }

    @Operation(summary = "아티스트 조회", description = "아티스트를 조회합니다. 페이징 처리가 가능합니다.")
    @GetMapping("/artists")
    fun getArtists(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "") name: String
    ): Page<Artist> {
        return artistService.getArtists(page, size, name)
    }

    @Operation(summary = "아티스트 정보 업데이트", description = "아티스트 정보를 업데이트합니다.")
    @PostMapping("/artist")
    fun updateArtist(
        @RequestBody body: UpdateArtistRequestBody
    ) {
        artistService.updateArtist(
            body.id,
            body.name,
            body.nameEn,
            body.nameJp,
            body.isSolo,
            body.imageUrl,
            body.youtubeChannelLink,
            body.twitterLink,
            body.youtubeVideoLink
        )
    }
}