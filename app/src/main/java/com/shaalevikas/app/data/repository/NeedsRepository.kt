package com.shaalevikas.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shaalevikas.app.data.model.Need
import com.shaalevikas.app.data.model.NeedStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NeedsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val needsCol = db.collection("needs")

    fun getActiveNeeds(): Flow<List<Need>> = callbackFlow {
        val sub = needsCol
            .whereEqualTo("status", NeedStatus.ACTIVE.name)
            .orderBy("urgency", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { it.toObject(Need::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    fun getFulfilledNeeds(): Flow<List<Need>> = callbackFlow {
        val sub = needsCol
            .whereEqualTo("status", NeedStatus.FULFILLED.name)
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { it.toObject(Need::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    suspend fun getNeedById(id: String): Need? {
        return try {
            needsCol.document(id).get().await().toObject(Need::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addNeed(need: Need): Result<String> {
        return try {
            val doc = needsCol.add(need.copy(createdAt = Timestamp.now())).await()
            Result.success(doc.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNeed(need: Need): Result<Unit> {
        return try {
            needsCol.document(need.id).set(need).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNeed(id: String): Result<Unit> {
        return try {
            needsCol.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markFulfilled(needId: String, beforeUrl: String, afterUrl: String): Result<Unit> {
        return try {
            needsCol.document(needId).update(
                mapOf(
                    "status" to NeedStatus.FULFILLED.name,
                    "completedAt" to Timestamp.now(),
                    "beforePhotoUrl" to beforeUrl,
                    "afterPhotoUrl" to afterUrl
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
