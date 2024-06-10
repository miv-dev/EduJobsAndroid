package ru.edu.jobs.data.mappers

import ru.edu.jobs.data.dto_model.ProfileDto
import ru.edu.jobs.data.dto_model.ServiceDto
import ru.edu.jobs.data.dto_model.DepartmentDto
import ru.edu.jobs.data.dto_model.UserDto
import ru.edu.jobs.domain.entity.Profile
import ru.edu.jobs.domain.entity.Role
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.entity.Department
import ru.edu.jobs.domain.entity.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun ServiceDto.toService() = Service(
    id = id,

    name = serviceName,
    description = serviceDetails,
    dateCreated = dateCreated,
    deadline = deadline?.toLocalDate(),
    user = user.toUser()
)

fun Service.toServiceDto() = ServiceDto(
    id = id,
    serviceName = name,
    serviceDetails = description,
    dateCreated = dateCreated,
    deadline = deadline?.toStringDate(),
    user = user.toUserDto(),
)

fun DepartmentDto.toUniversity() = Department(
    id = id,
    name = name,
    description = description,
    website = website
)

fun UserDto.toUser() = User(
    id = id,
    email = email,
    username = username,
    role = when (userType) {
        "employer" -> Role.Employer
        "specialist" -> Role.Specialist
        "admin" -> Role.Admin
        else -> null
    },
    department = department?.toUniversity(),
    profile = profile?.toProfile() ?: Profile(-1, null, null, null, null)
)

fun ProfileDto.toProfile() = Profile(
    id = id,
    avatar = avatar,
    lastName = lastName,
    firstName = firstName,
    phone = phoneNumber
)

fun Profile.toProfileDto() = ProfileDto(
    id = id,
    avatar = avatar,
    lastName = lastName,
    firstName = firstName,
    phoneNumber = phone
)

fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    return LocalDate.parse(this, formatter)
}

fun LocalDate.toStringDate(): String {
    val formatter = DateTimeFormatter.ISO_DATE
    return this.format(formatter)
}
