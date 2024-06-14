package org.example.indiejoaapi.stage.controller

import io.swagger.v3.oas.annotations.Operation
import org.example.indiejoaapi.stage.entity.Stage
import org.example.indiejoaapi.stage.service.StageService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
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
    @GetMapping("/stages")
    fun getStages(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): Page<Stage> {
        return stageService.getStages(page, size)
    }

    @Operation(summary = "공연장 상세 조회", description = "공연장을 상세 조회합니다.")
    @GetMapping("/stage")
    fun getStage(
        @RequestParam id: Long
    ): Stage {
        return stageService.getStage(id)
    }
}