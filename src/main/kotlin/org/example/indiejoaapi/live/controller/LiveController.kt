package org.example.indiejoaapi.live.controller

import io.swagger.v3.oas.annotations.Operation
import org.example.indiejoaapi.live.service.LiveService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LiveController(
    private val liveService: LiveService
) {
    @Operation(summary = "공연정보 크롤링", description = "공연정보를 크롤링하여 저장합니다.")
    @PostMapping("/live/crawl")
    fun crawlLive() {
        liveService.crawlAllLives()
    }

    @Operation(summary = "공연정보 조회", description = "공연정보를 조회합니다.")
    @GetMapping("/lives")
    fun getLives(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ) {
        liveService.getLives(page, size)
    }


}