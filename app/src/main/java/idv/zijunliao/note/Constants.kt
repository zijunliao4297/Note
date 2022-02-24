package idv.zijunliao.note

import android.graphics.Color
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

/**
 * Constants variables for application.
 */
object Constants {

    /**
     * defined colors for note background.
     */
    val Blue300 = Color.parseColor("#64B5F6")
    val Indigo300 = Color.parseColor("#7986CB")
    val Teal300 = Color.parseColor("#4DB6AC")
    val Amber300 = Color.parseColor("#FFD54F")
    val Pink300 = Color.parseColor("#F06292")
    val Brown300 = Color.parseColor("#A1887F")
    val Green300 = Color.parseColor("#81C784")

    val COLOR = listOf(Blue300, Indigo300, Teal300, Amber300, Pink300, Brown300, Green300)

    /**
     * log tag for debug.
     */
    const val AppDebug = "AppDebug"

    /**
     * preferences key for data store.
     */
    val KEY_SUGGESTIONS = stringSetPreferencesKey("KEY_SUGGESTIONS")
    val KEY_FIRST_OPEN = booleanPreferencesKey("KEY_FIRST_OPEN")
    val KEY_SORT = intPreferencesKey("KEY_SORT")
    val KEY_ORDER = intPreferencesKey("KEY_ORDER")
    val KEY_SPAN_COUNT = intPreferencesKey("KEY_SPAN_COUNT")
}

