package org.example.indiejoaapi.live.service

import okhttp3.FormBody
import org.example.indiejoaapi.common.service.ApiService
import org.example.indiejoaapi.live.entity.Live
import org.example.indiejoaapi.live.repository.LiveRepository
import org.springframework.boot.json.GsonJsonParser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class LiveService(
    private val liveRepository: LiveRepository,
    private val apiService: ApiService
) {
    fun crawlAllLives() {
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
        val lives = (json["data"] as Map<*, *>)["lives"] as List<Map<*, *>>?

        if (lives.isNullOrEmpty()) {
            return 0
        }


        lives.forEach { liveInfo ->
            val indieStreetId = (liveInfo["id"] as String).toLong()
            val title = liveInfo["title"] as String? ?: ""
            val startDate = liveInfo["startDate"] as String? ?: ""
            val endDate = liveInfo["endDate"] as String? ?: ""
            val description = liveInfo["description"] as String? ?: ""
            val priceInfo = liveInfo["priceInfo"] as String? ?: ""
            val indieStreetStageId =
                if (liveInfo["stage"] != null)
                    ((liveInfo["stage"] as Map<*, *>)["id"] as String? ?: "0").toLong()
                else 0L
            val posterUrl = (liveInfo["posters"] as List<*>?)?.let {
                if (it.isEmpty()) return@let null
                it[0] as Map<*, *>?
            }
                ?.let { it["formats"] as Map<*, *>? }
                ?.let { it["thumbnail"] as Map<*, *>? }
                ?.let { "https://indistreet.com/_next/image?url=https://indistreet-api.roto.codes${it["url"] as String}&w=2048&q=100" }
                ?: ""
            val purchaseTicketLink = liveInfo["purchaseTicketLink"] as String? ?: ""


            val live = liveRepository.findByIndieStreetId(indieStreetId)?.let {
                it.title = title
                it.startDate = startDate
                it.endDate = endDate
                it.description = description
                it.priceInfo = priceInfo
                it.stageId = indieStreetStageId
                it.posterUrl = posterUrl
                it.purchaseTicketLink = purchaseTicketLink
                it
            } ?: Live(
                id = 0,
                indieStreetId = indieStreetId,
                title = title,
                startDate = startDate,
                endDate = endDate,
                description = description,
                priceInfo = priceInfo,
                stageId = indieStreetStageId,
                posterUrl = posterUrl,
                purchaseTicketLink = purchaseTicketLink
            )

            liveRepository.save(live)
        }

        return lives.size
    }

    private fun getRequestBody(index: Int): FormBody {
        val dollarSign = "$"
        return FormBody.Builder()
            .add("operationName", "liveList")
            .add(
                "variables", """
                {
                    "limit": 100,
                    "start": $index,
                    "where": {},
                    "sort": "startDate:DESC"
                }
            """.trimIndent()
            )
            .add(
                "query", """
                query liveList(${dollarSign}sort: String, ${dollarSign}where: JSON, ${dollarSign}start: Int, ${dollarSign}limit: Int!) {
                lives(sort: ${dollarSign}sort, where: ${dollarSign}where, start: ${dollarSign}start, limit: ${dollarSign}limit) {
                    ...LiveListItem
                    __typename
                  }
                }
                
                fragment LiveListItem on Live {
                  id
                  title
                  startDate
                  endDate
                  description
                  priceInfo
                  stage {
                    id
                    name
                    youtubeChannelLink
                    defaultLivePoster {
                      formats
                      __typename
                    }
                    useSlug
                    slug
                    __typename
                  }
                  musicians {
                    ...LiveInMusician
                    __typename
                  }
                  liveStreaming {
                    id
                    videoId
                    streamingLink
                    streamingType
                    __typename
                  }
                  ...LivePoster
                  festival {
                    ...FestivalDetail
                    __typename
                  }
                  relatedLives(sort: "startDate") {
                    id
                    title
                    startDate
                    endDate
                    musicians {
                      id
                      name
                      useSlug
                      slug
                      __typename
                    }
                    posters {
                      id
                      formats
                      mime
                      url
                      __typename
                    }
                    __typename
                  }
                  articles(sort: "publishDate:DESC", limit: 12, where: {isRemoved: false}) {
                    ...ArticleListItem
                    __typename
                  }
                  priceInfo
                  purchaseTicketLink
                  isCanceled
                  eventPageLink
                  __typename
                }
                
                fragment LiveInMusician on Musician {
                  id
                  name
                  profileImage {
                    formats
                    __typename
                  }
                  useSlug
                  slug
                  __typename
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
                
                fragment FestivalDetail on Festival {
                  id
                  festivalItems(sort: "date:asc") {
                    ...FestivalItem
                    __typename
                  }
                  __typename
                }
                
                fragment FestivalItem on FestivalItem {
                  id
                  date
                  stageByFestival {
                    id
                    name
                    isLiveHall
                    youtubeChannelLink
                    useSlug
                    slug
                    __typename
                  }
                  stageAliasName
                  stageDescription
                  enableTimeTable
                  description
                  relatedLive {
                    ...FestivalItemLive
                    __typename
                  }
                  festivalItemTimeTables(sort: "liveTime:asc") {
                    id
                    liveTime
                    musician {
                      id
                      name
                      nameEn
                      nameJp
                      profileImage {
                        formats
                        __typename
                      }
                      useSlug
                      slug
                      __typename
                    }
                    festivalProgram {
                      programName
                      __typename
                    }
                    __typename
                  }
                  liveStreaming {
                    id
                    videoId
                    streamingType
                    streamingLink
                    __typename
                  }
                  __typename
                }
                
                fragment FestivalItemLive on Live {
                  id
                  title
                  startDate
                  endDate
                  isCanceled
                  ...LivePoster
                  __typename
                }
                
                fragment ArticleListItem on Article {
                  id
                  title
                  link
                  publishDate
                  lives {
                    id
                    title
                    __typename
                  }
                  musicians {
                    id
                    name
                    profileImage {
                      id
                      formats
                      __typename
                    }
                    useSlug
                    slug
                    __typename
                  }
                  isRemoved
                  __typename
                }
                """.trimIndent()
            )
            .build()
    }

    fun getLives(page: Int, size: Int): Page<Live> {
        return liveRepository.findAll(PageRequest.of(page, size))
    }

    fun getLive(id: Long): Live {
        return liveRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 공연 정보가 없습니다.") }
    }
}