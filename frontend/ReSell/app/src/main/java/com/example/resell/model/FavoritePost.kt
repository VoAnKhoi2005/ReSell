import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoritePost(
    @Json(name = "cart_id") val cardId: String,
    @Json(name = "post_id") val postId: String
)
