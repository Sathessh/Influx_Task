package satheesh.influxtask.data


import com.google.gson.annotations.SerializedName


data class DatesItem(
    @SerializedName("date")
    val date: String = "",
    @SerializedName("movies")
    val timings: List<MoviewItem>?
)

data class MoviewItem(
    @SerializedName("moviewName")
    val moviewName: String = "",
    @SerializedName("timings")
    val timings: List<TimingsItem>?
)


data class TimingsItem(
    @SerializedName("timing")
    val timing: String = "",
    @SerializedName("seats")
    val seats: List<SeatsItem>?, var expand: Int = 1
)


data class SeatsItem(
    @SerializedName("rowName")
    val rowName: String = "",
    @SerializedName("seats")
    val seats: List<SeatsItem>?
)


