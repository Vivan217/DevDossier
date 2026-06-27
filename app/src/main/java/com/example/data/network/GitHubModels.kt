package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubUserResponse(
    @Json(name = "login") val login: String,
    @Json(name = "name") val name: String?,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "bio") val bio: String?,
    @Json(name = "public_repos") val publicReposCount: Int?,
    @Json(name = "followers") val followersCount: Int?
)

@JsonClass(generateAdapter = true)
data class GitHubRepoResponse(
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?,
    @Json(name = "stargazers_count") val starsCount: Int,
    @Json(name = "language") val language: String?,
    @Json(name = "html_url") val htmlUrl: String,
    @Json(name = "updated_at") val updatedAt: String
)
