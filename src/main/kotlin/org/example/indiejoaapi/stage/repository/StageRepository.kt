package org.example.indiejoaapi.stage.repository

import org.example.indiejoaapi.stage.entity.Stage
import org.springframework.data.jpa.repository.JpaRepository

interface StageRepository : JpaRepository<Stage, Long> {
    fun findByIndieStreetId(indieStreetId: Long): Stage?
}