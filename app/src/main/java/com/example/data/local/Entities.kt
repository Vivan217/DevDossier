package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["github_username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val github_username: String,
    val display_name: String?,
    val avatar_url: String?,
    val bio: String?,
    val plan: String = "free", // "free" or "pro"
    val created_at: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "portfolios",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["slug"], unique = true)
    ]
)
data class PortfolioEntity(
    @PrimaryKey val id: String,
    val user_id: String,
    val slug: String,
    val theme: String = "modern-dark", // "modern-dark", "midnight-neon", "cosmic-slate"
    val tagline: String?,
    val is_published: Boolean = false,
    val custom_domain: String? = null,
    val last_synced_at: Long? = null,
    val created_at: Long = System.currentTimeMillis(),
    val updated_at: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "repos",
    foreignKeys = [
        ForeignKey(
            entity = PortfolioEntity::class,
            parentColumns = ["id"],
            childColumns = ["portfolio_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["portfolio_id"])]
)
data class RepoEntity(
    @PrimaryKey val id: String,
    val portfolio_id: String,
    val repo_name: String,
    val description: String?,
    val stars: Int = 0,
    val language: String?,
    val url: String,
    val is_visible: Boolean = true,
    val sort_order: Int = 0
)

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"], unique = true)]
)
data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val user_id: String,
    val razorpay_subscription_id: String?,
    val razorpay_payment_id: String?,
    val status: String, // "active", "cancelled", "past_due", "captured"
    val started_at: Long = System.currentTimeMillis(),
    val current_period_end: Long? = null
)
