package com.shaalevikas.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.shaalevikas.app.data.model.Pledge
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PledgeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val pledgesCol = db.collection("pledges")
    private val needsCol = db.collection("needs")
    private val usersCol = db.collection("users")

    suspend fun submitPledge(pledge: Pledge): Result<Unit> {
        return try {
            val existing = pledgesCol
                .whereEqualTo("needId", pledge.needId)
                .whereEqualTo("userId", pledge.userId)
                .get().await()
            if (!existing.isEmpty) {
                return Result.failure(Exception("You have already pledged for this need."))
            }
            val newPledge = pledge.copy(createdAt = Timestamp.now())
            pledgesCol.add(newPledge).await()

            db.runTransaction { tx ->
                val needRef = needsCol.document(pledge.needId)
                val userRef = usersCol.document(pledge.userId)
                val needSnap = tx.get(needRef)
                val userSnap = tx.get(userRef)
                val currentPledged = needSnap.getDouble("amountPledged") ?: 0.0
                tx.update(needRef, "amountPledged", currentPledged + pledge.amount)
                val userPledged = userSnap.getDouble("totalPledged") ?: 0.0
                tx.update(userRef, "totalPledged", userPledged + pledge.amount)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPledgesForNeed(needId: String): Flow<List<Pledge>> = callbackFlow {
        val sub = pledgesCol
            .whereEqualTo("needId", needId)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { it.toObject(Pledge::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    fun getPledgesForUser(userId: String): Flow<List<Pledge>> = callbackFlow {
        val sub = pledgesCol
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val list = snap?.documents?.mapNotNull { it.toObject(Pledge::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { sub.remove() }
    }
}
