package idv.zijunliao.note

import android.content.Context
import android.content.res.Resources
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Extension data store for Context.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "idv.zijunliao.note.datastore")

/**
 * Extension unit convert for Number
 */
val Int.dp: Int get() = (Resources.getSystem().displayMetrics.density * this).toInt()
val Float.dp: Float get() = (Resources.getSystem().displayMetrics.density * this)
val Int.sp: Int get() = (Resources.getSystem().displayMetrics.scaledDensity * this).toInt()
val Float.sp: Float get() = (Resources.getSystem().displayMetrics.scaledDensity * this)

/**
 * Extension inline function for SearchView setOnQueryTextListener
 * only handle the query text after submit
 * doesn't handle the query text while change in current ui/ux design
 */
inline fun SearchView.onQueryTextChanged(crossinline action: (query: String) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            action(query)
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    })
}