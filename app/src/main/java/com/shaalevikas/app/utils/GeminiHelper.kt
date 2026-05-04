package com.shaalevikas.app.utils

import com.google.ai.client.generativeai.GenerativeModel
import com.shaalevikas.app.BuildConfig

object GeminiHelper {
    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    suspend fun generateNeedDescription(title: String, category: String): String {
        return try {
            val prompt = """
                You are assisting a school headmaster in rural India to describe a school infrastructure need.
                Generate a clear, concise 3-4 sentence description for the following need:
                Title: $title
                Category: $category
                The description should be factual, empathetic, and motivate alumni to contribute.
                Do not include any markdown formatting. Just plain text.
            """.trimIndent()
            val response = model.generateContent(prompt)
            response.text?.trim() ?: "Unable to generate description."
        } catch (e: Exception) {
            "Unable to generate description: ${e.message}"
        }
    }

    suspend fun estimateCost(title: String, category: String): Double {
        return try {
            val prompt = """
                Estimate the cost in Indian Rupees (INR) for the following school infrastructure need in a rural government school in India.
                Title: $title
                Category: $category
                Respond with ONLY a numeric value (no currency symbol, no commas, no text). Example: 15000
            """.trimIndent()
            val response = model.generateContent(prompt)
            val text = response.text?.trim()?.replace(",", "")?.replace("₹", "") ?: "0"
            text.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    suspend fun generateImpactSummary(title: String, category: String, amount: Double): String {
        return try {
            val prompt = """
                Write a 2-sentence impact summary for alumni who funded a school need.
                Title: $title
                Category: $category
                Amount Funded: ₹$amount
                Make it celebratory and show the real impact on students.
                No markdown formatting, plain text only.
            """.trimIndent()
            val response = model.generateContent(prompt)
            response.text?.trim() ?: "Thank you for your contribution!"
        } catch (e: Exception) {
            "Thank you for your contribution!"
        }
    }
}
