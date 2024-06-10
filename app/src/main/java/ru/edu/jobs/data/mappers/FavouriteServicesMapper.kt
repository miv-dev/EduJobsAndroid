package ru.edu.jobs.data.mappers

import ru.edu.jobs.data.local.model.ServiceDBModel
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.entity.ServiceShort

fun Service.toDbModel() = ServiceDBModel(id, name, description)
fun ServiceShort.toDbModel() = ServiceDBModel(id, name, description)

fun ServiceDBModel.toEntity() = ServiceShort(id, name, description)

fun List<ServiceDBModel>.toEntities() = map { it.toEntity() }