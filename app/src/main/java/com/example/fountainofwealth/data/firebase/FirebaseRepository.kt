package com.example.fountainofwealth.data.firebase

import com.example.fountainofwealth.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun currentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()

        val uid = currentUserId() ?: return

        firestore.collection("users")
            .document(uid)
            .set(mapOf("budget" to 0.0))
            .await()
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getBudget(): Double {
        val uid = currentUserId() ?: return 0.0

        val document = firestore.collection("users")
            .document(uid)
            .get()
            .await()

        return document.getDouble("budget") ?: 0.0
    }

    suspend fun updateBudget(budget: Double) {
        val uid = currentUserId() ?: return

        firestore.collection("users")
            .document(uid)
            .set(
                mapOf("budget" to budget),
                SetOptions.merge()
            )
            .await()
    }

    suspend fun getTransactions(): List<Transaction> {
        val uid = currentUserId() ?: return emptyList()

        return firestore.collection("users")
            .document(uid)
            .collection("transactions")
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(Transaction::class.java)
            }
            .sortedByDescending { transaction ->
                transaction.timestamp
            }
    }

    suspend fun addTransaction(transaction: Transaction) {
        val uid = currentUserId() ?: return

        val document = firestore.collection("users")
            .document(uid)
            .collection("transactions")
            .document()

        document.set(
            transaction.copy(id = document.id)
        ).await()
    }

    suspend fun deleteTransaction(transactionId: String) {
        val uid = currentUserId() ?: return

        firestore.collection("users")
            .document(uid)
            .collection("transactions")
            .document(transactionId)
            .delete()
            .await()
    }
}