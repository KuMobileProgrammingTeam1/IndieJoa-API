package org.example.indiejoaapi.stage.controller

import io.swagger.v3.oas.annotations.Operation
import org.example.indiejoaapi.stage.service.StageService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class StageController(
    private val stageService: StageService
) {
    @Operation(summary = "공연장 크롤링", description = "공연장을 크롤링하여 저장합니다.")
    @PostMapping("/stage/crawl")
    fun crawlStage() {
        stageService.crawlAllStages()
    }

    @Operation(summary = "공연장 조회", description = "공연장을 조회합니다.")
    @PostMapping("/stage")
    fun getStages(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ) {
        stageService.getStages(page, size)
    }
}