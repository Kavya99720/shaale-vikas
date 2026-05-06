package com.shaalevikas.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

enum class NeedCategory {
    FURNITURE, INFRASTRUCTURE, STATIONERY, TOILETS, LIBRARY, SPORTS, TECHNOLOGY, OTHER
}

enum class NeedStatus {
    ACTIVE, FULFILLED
}

enum class UserRole {
    ADMIN, ALUMNI, GUEST
}

enum class BadgeTier {
    BRONZE, SILVER, GOLD, PLATINUM
}

data class Need(
    @DocumentId val id: String = "",
    val title: String = "",
    val category: String = NeedCategory.OTHER.name,
    val description: String = "",
    val photoUrl: String = "",
    val costEstimate: Double = 0.0,
    val amountPledged: Double = 0.0,
    val status: String = NeedStatus.ACTIVE.name,
    val createdAt: Timestamp? = null,
    val completedAt: Timestamp? = null,
    val beforePhotoUrl: String = "",
    val afterPhotoUrl: String = "",
    val urgency: Int = 1
) {
    @get:Exclude
    val progressPercent: Float
        get() = if (costEstimate > 0) (amountPledged / costEstimate * 100).toFloat().coerceIn(0f, 100f) else 0f
}

data class Pledge(
    @DocumentId val id: String = "",
    val needId: String = "",
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val amount: Double = 0.0,
    val createdAt: Timestamp? = null
)

data class User(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = UserRole.ALUMNI.name,
    val totalPledged: Double = 0.0,
    val createdAt: Timestamp? = null,
    // Some documents store joinedAt as a Long (epoch ms) instead of Timestamp
    // Keeping as Long? prevents crash when the type doesn't match
    val joinedAt: Long? = null
) {
    @get:Exclude
    val badgeTier: BadgeTier
        get() = when {
            totalPledged >= 50000 -> BadgeTier.PLATINUM
            totalPledged >= 10000 -> BadgeTier.GOLD
            totalPledged >= 2000 -> BadgeTier.SILVER
            else -> BadgeTier.BRONZE
        }
}

data class SchoolProfile(
    @DocumentId val id: String = "",
    val name: String = "",
    val established: String = "",
    val location: String = "",
    val studentCount: Int = 0,
    val about: String = "",
    val photoUrl: String = ""
)

data class GalleryItem(
    @DocumentId val id: String = "",
    val needId: String = "",
    val title: String = "",
    val beforePhotoUrl: String = "",
    val afterPhotoUrl: String = "",
    val completedAt: Timestamp? = null,
    val caption: String = ""
)
