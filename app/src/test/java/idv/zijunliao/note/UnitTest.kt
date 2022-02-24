package idv.zijunliao.note

import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import org.hamcrest.Matchers.*
import java.text.SimpleDateFormat
import java.util.*

class UnitTest {

    @Test
    fun `Find all match query by regex`() {
        val query = "Lorem"
        val input = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
        val match = query.toRegex(RegexOption.IGNORE_CASE).findAll(input).toList()
        MatcherAssert.assertThat(match.size, equalTo(4))
    }

    @Test
    fun `Time formation by java SimpleDateFormat`() {
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = 1645675647318
        MatcherAssert.assertThat(formatter.format(calendar.time), equalTo("Feb 24, 2022"))
    }
}