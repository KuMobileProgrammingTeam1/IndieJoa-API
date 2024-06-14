package org.example.indiejoaapi.stage.service

import okhttp3.FormBody
import org.example.indiejoaapi.common.service.ApiService
import org.example.indiejoaapi.stage.entity.Stage
import org.example.indiejoaapi.stage.repository.StageRepository
import org.springframework.boot.json.GsonJsonParser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class StageService(
    private val stageRepository: StageRepository,
    private val apiService: ApiService
) {
    fun crawlAllStages() {
        val baseUrl = "https://indistreet.graphcdn.app/graphql"
        var index = 0
        while (true) {
            val requestBody = getRequestBody(index)
            val response = apiService.post(baseUrl, requestBody)
            val body = response.body?.string()
            if (body.isNullOrEmpty()) {
                break
            }

            if (parseAndSave(body) == 0) {
                break
            }
            index += 100
        }
    }

    private fun parseAndSave(body: String): Int {
        val jsonParser = GsonJsonParser()
        val json = jsonParser.parseMap(body)
        val stages = (json["data"] as Map<*, *>)["stages"] as List<Map<*, *>>?

        if (stages.isNullOrEmpty()) {
            return 0
        }

        stages.forEach { stageInfo ->
            val indieStreetId = (stageInfo["id"] as String).toLong()
            val name = stageInfo["name"] as String? ?: ""
            val address = stageInfo["address"] as String? ?: ""
            val placeLink = stageInfo["placeLink"] as String? ?: ""
            val youtubeChannelLink = stageInfo["youtubeChannelLink"] as String? ?: ""

            val stage = stageRepository.findByIndieStreetId(indieStreetId) ?: Stage(
                id = 0,
                indieStreetId = indieStreetId,
                name = name,
                address = address,
                placeLink = placeLink,
                youtubeChannelLink = youtubeChannelLink
            )

            stageRepository.save(stage)
        }

        return stages.size
    }

    private fun getRequestBody(index: Int): FormBody {
        val dollarSign = "$"
        return FormBody.Builder()
            .add("operationName", "findStages")
            .add(
                "variables", """
                {
                    "where": {
                        "isLiveHall": true,
                        "isClosed": false
                    },
                    "sort": "updated_at:ASC",
                    "start": $index,
                    "limit": 100,
                    "liveWhere": {
                        "endDate_gte": ${Date().time},
                        "isCanceled": false
                    },
                    "liveLimit": 2
                }
            """.trimIndent()
            )
            .add(
                "query", """
                query findStages(${dollarSign}where: JSON, ${dollarSign}sort: String, ${dollarSign}start: Int, ${dollarSign}limit: Int!, ${dollarSign}liveWhere: JSON, ${dollarSign}liveLimit: Int) {
                stages(where: ${dollarSign}where, sort: ${dollarSign}sort, start: ${dollarSign}start, limit: ${dollarSign}limit) {
                id
                name
                address
                placeLink
                youtubeChannelLink
                useSlug
                slug
                lives(sort: "startDate:ASC", where: ${dollarSign}liveWhere, limit: ${dollarSign}liveLimit) {
                  id
                  title
                  startDate
                  endDate
                  isCanceled
                  ...LivePoster
                  __typename
                }
                defaultLivePoster {
                  formats
                  __typename
                }
                logo {
                  formats
                  __typename
                }
                updated_at
                __typename
              }
            }
            
            fragment LivePoster on Live {
              title
              posters {
                id
                formats
                mime
                url
                __typename
              }
              isCanceled
              __typename
            }
            """.trimIndent()
            ).build()
    }

    fun getStage(indieStreetId: Long): Stage {
        return stageRepository.findByIndieStreetId(indieStreetId)
            ?: throw IllegalArgumentException("Stage not found")
    }

    fun getStages(page: Int, size: Int): Page<Stage> {
        return stageRepository.findAll(PageRequest.of(page, size))
    }
}