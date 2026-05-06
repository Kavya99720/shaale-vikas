package com.shaalevikas.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shaalevikas.app.data.model.SchoolProfile
import com.shaalevikas.app.data.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCol = db.collection("users")
    private val schoolCol = db.collection("school")

    fun getLeaderboard(): Flow<List<User>> = callbackFlow {
        val sub = usersCol
            .limit(50)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents
                    ?.mapNotNull { it.toObject(User::class.java) }
                    ?.sortedByDescending { it.totalPledged }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun getSchoolProfile(): SchoolProfile? {
        return try {
            val snap = schoolCol.limit(1).get().await()
            snap.documents.firstOrNull()?.toObject(SchoolProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveSchoolProfile(profile: SchoolProfile): Result<Unit> {
        return try {
            val docId = if (profile.id.isNotEmpty()) profile.id else "main"
            schoolCol.document(docId).set(profile.copy(id = docId)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            usersCol.document(userId).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
