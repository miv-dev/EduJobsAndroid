package ru.edu.jobs.data.mappers

import ru.edu.jobs.data.dto_model.DepartmentDto
import ru.edu.jobs.data.dto_model.UserDto
import ru.edu.jobs.domain.entity.Role
import ru.edu.jobs.domain.entity.Department
import ru.edu.jobs.domain.entity.User

fun User.toUserDto() = UserDto(
    id = id,
    username = username,
    email = email,
    userType = when (role) {
        Role.Specialist -> "specialist"
        Role.Employer -> "employer"
        Role.Admin -> "admin"
        null -> null
    },
    profile = profile.toProfileDto(),
    department = department?.toUniversityDto()
)

fun Department.toUniversityDto() = DepartmentDto(
    id, name, description, website
)
