package org.example.indiejoaapi.artist.service

import okhttp3.FormBody
import org.example.indiejoaapi.artist.entity.Artist
import org.example.indiejoaapi.artist.repository.ArtistRepository
import org.example.indiejoaapi.common.service.ApiService
import org.springframework.boot.json.GsonJsonParser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ArtistService(
    private val artistRepository: ArtistRepository,
    private val apiService: ApiService
) {
    fun crawlAllArtists() {
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
        val musicians = (json["data"] as Map<*, *>)["musicians"] as List<Map<*, *>>?

        if (musicians.isNullOrEmpty()) {
            return 0
        }


        musicians.forEach { musician ->
            val indieStreetId = (musician["id"] as String).toLong()
            val name = musician["name"] as String? ?: ""
            val nameEn = musician["nameEn"] as String? ?: ""
            val nameJp = musician["nameJp"] as String? ?: ""
            val isSolo = musician["isSolo"] as Boolean
            val profileImage =
                (musician["profileImage"] as Map<*, *>?)?.let { it["formats"] as Map<*, *>? }
                    ?.let { it["thumbnail"] as Map<*, *>? }
                    ?.let { "https://indistreet.com/_next/image?url=https://indistreet-api.roto.codes${it["url"] as String}&w=2048&q=100" }
                    ?: ""
            val youtubeChannelLink = musician["youtubeChannelLink"] as String?
            val twitterLink = musician["twitterLink"] as String?

            val artist = artistRepository.findByIndieStreetId(indieStreetId)?.let {
                it.name = name
                it.nameEn = nameEn
                it.nameJp = nameJp
                it.isSolo = isSolo
                it.youtubeChannelLink = youtubeChannelLink ?: ""
                it.twitterLink = twitterLink ?: ""
                it.imageUrl = profileImage
                it
            } ?: Artist(
                id = 0,
                indieStreetId = indieStreetId,
                name = name,
                nameEn = nameEn,
                nameJp = nameJp,
                isSolo = isSolo,
                youtubeChannelLink = youtubeChannelLink ?: "",
                twitterLink = twitterLink ?: "",
                imageUrl = profileImage,
            )

            artistRepository.save(artist)
        }

        return musicians.size
    }

    private fun getRequestBody(index: Int): FormBody {
        val dollarSign = "$"
        return FormBody.Builder()
            .add("operationName", "musicianList")
            .add(
                "variables", """
                {
                    "limit": 100,
                    "start": $index,
                    "genres": [],
                    "sort": "id:ASC"
                }
            """.trimIndent()
            )
            .add(
                "query", """
                query musicianList(${dollarSign}sort: String, ${dollarSign}name: String, ${dollarSign}genres: [ID!], ${dollarSign}start: Int, ${dollarSign}limit: Int!) {
                    musicians(
                        sort: ${dollarSign}sort
                        where: {isRemoved: false, genres: {id_in: ${dollarSign}genres}, _or: [{name_contains: ${dollarSign}name}, {nameEn_contains: ${dollarSign}name}, {nameJp_contains: ${dollarSign}name}]}
                        start: ${dollarSign}start
                        limit: ${dollarSign}limit
                    ) {
                        ...MusicianListItem
                        __typename
                    }
                }
                
                fragment MusicianListItem on Musician {
                    id
                    name
                    nameEn
                    nameJp
                    isSolo
                    profileImage {
                        id
                        formats
                        __typename
                    }
                    members {
                        id
                        name
                        profileImage {
                            id
                            formats
                            __typename
                        }
                        __typename
                    }
                    youtubeChannelLink
                    twitterLink
                    useSlug
                    slug
                    __typename
                }
            """.trimIndent()
            )
            .build()
    }

    fun getArtists(page: Int, size: Int, name: String): Page<Artist> {
        return artistRepository.findByNameContainsOrderById(name, PageRequest.of(page, size))
    }

    fun updateArtist(
        artistId: Long,
        name: String,
        nameEn: String,
        nameJp: String,
        solo: Boolean,
        imageUrl: String,
        youtubeChannelLink: String,
        twitterLink: String,
        youtubeVideoLink: String
    ) {
        val artist = artistRepository.findById(artistId)
            .orElseThrow { IllegalArgumentException("Artist not found") }

        artist.name = name
        artist.nameEn = nameEn
        artist.nameJp = nameJp
        artist.isSolo = solo
        artist.imageUrl = imageUrl
        artist.youtubeChannelLink = youtubeChannelLink
        artist.twitterLink = twitterLink
        artist.youtubeVideoLink = youtubeVideoLink

        artistRepository.save(artist)
    }
}