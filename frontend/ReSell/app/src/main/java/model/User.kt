import model.Address
import model.Notification
import model.Post
import java.time.LocalDate
import java.time.LocalDateTime

data class User(
    val id: String,
    var username: String,
    var email: String,
    var phone: String,
    var password: String,
    var fullName: String,
    var citizenId: String,
    var birthday: LocalDate?,
    var gender: Boolean,
    var status: String,
    var reputation: Int,
    var banStart: LocalDateTime?,
    var banEnd: LocalDateTime?,
    val createdAt: LocalDateTime?,
    val notifications: List<Notification>,
    val addresses: List<Address>,
    val posts: List<Post>
)
