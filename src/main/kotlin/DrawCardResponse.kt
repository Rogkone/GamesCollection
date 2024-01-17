import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request

data class DrawCardResponse(
    val success: Boolean,
    @SerializedName("deck_id") val deckID: String,
    val cards: List<Card>,
    val remaining: Int
) {
    companion object {
        fun createAsync(): DrawCardResponse? {
            return drawCardFromDeckAsync()
        }
        fun createAsync(count: Int = 1): DrawCardResponse? {
            return drawCardFromDeckAsync(count)
        }
        fun createAsync(count: Int = 1, deckId: String): DrawCardResponse? {
            return drawCardFromDeckAsync(count, deckId)
        }
        private fun drawCardFromDeckAsync(count: Int = 1, deckId: String = "new"): DrawCardResponse? {
            val client = OkHttpClient()
            try {
                val apiUrl = "https://deckofcardsapi.com/api/deck/$deckId/draw/?count=$count"
                val request = Request.Builder()
                    .url(apiUrl)
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    return Gson().fromJson(responseBody, DrawCardResponse::class.java)
                } else {
                    println("API request failed with status code: ${response.code}")
                    return null
                }
            } catch (ex: Exception) {
                println("An error occurred: ${ex.message}")
                return null
            }
        }

        fun shuffelDeck(deckId: String): DrawCardResponse? {
            val client = OkHttpClient()
            try {
                val apiUrl = "https://www.deckofcardsapi.com/api/deck/$deckId/shuffle/"
                val request = Request.Builder()
                    .url(apiUrl)
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    return Gson().fromJson(responseBody, DrawCardResponse::class.java)
                } else {
                    println("API request failed with status code: ${response.code}")
                    return null
                }
            } catch (ex: Exception) {
                println("An error occurred: ${ex.message}")
                return null
            }
        }


    }
}


