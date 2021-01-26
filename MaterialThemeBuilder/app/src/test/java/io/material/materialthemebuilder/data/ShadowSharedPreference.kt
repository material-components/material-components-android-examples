package io.material.materialthemebuilder.data

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import java.util.concurrent.ConcurrentHashMap

class ShadowSharedPreference : SharedPreferences {

    var editor: Editor? = null

    var changeListeners: MutableList<OnSharedPreferenceChangeListener> = ArrayList()
    var concurrentMap: ConcurrentHashMap<String?, Any?> = ConcurrentHashMap()

    init {
        editor = ShadowEditor(object : EditorCall {
            override fun apply(map: Map<String?, Any?>?, removeList: List<String?>?, commitClear: Boolean) {

                if (map == null || removeList == null) {
                    return
                }

                // clear
                if (commitClear) {
                    concurrentMap.clear()
                }

                // remove the element
                for (key in removeList) {
                    concurrentMap.remove(key)
                    for (listener in changeListeners) {
                        listener.onSharedPreferenceChanged(this@ShadowSharedPreference, key)
                    }
                }

                // Add an element
                val keys: Set<String?> = map.keys

                // Change before and after comparison
                for (key in keys) {
                    val lastValue = concurrentMap[key]
                    val value = map[key]
                    if (lastValue == null && value != null || lastValue != null && value == null || lastValue != value) {
                        //CHANGE VALUE BEFORE
                        concurrentMap[key] = value
                        for (listener in changeListeners) {
                            listener.onSharedPreferenceChanged(this@ShadowSharedPreference, key)
                        }
                    }
                }
                concurrentMap.putAll(map)
            }

        })
    }

    override fun getAll(): Map<String?, *> {
        return HashMap(concurrentMap)
    }

    override fun getString(key: String?, defValue: String?): String? {
        return if (concurrentMap.containsKey(key)) {
            concurrentMap[key] as String?
        } else defValue
    }

    override fun getStringSet(key: String?, defValues: Set<String?>?): Set<String?>? {
        return if (concurrentMap.containsKey(key)) {
            concurrentMap[key] as Set<String?>
        } else defValues
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return if (concurrentMap.containsKey(key)) {
            concurrentMap[key] as Int
        } else defValue
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return if (concurrentMap.containsKey(key)) {
            concurrentMap[key] as Long
        } else defValue
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return if (concurrentMap.containsKey(key)) {
            concurrentMap[key] as Float
        } else defValue
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return if (concurrentMap.containsKey(key)) {
            concurrentMap[key] as Boolean
        } else defValue
    }

    override fun contains(key: String?): Boolean {
        return concurrentMap.containsKey(key)
    }

    override fun edit(): Editor? {
        return editor
    }

    // Monitor the change of the corresponding key value, only when the value corresponding to the key changes.
    override fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        changeListeners.add(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        changeListeners.remove(listener)
    }


    interface EditorCall {
        fun apply(map: Map<String?, Any?>?, removeList: List<String?>?, commitClear: Boolean)
    }


    class ShadowEditor(var editorCall: EditorCall) : Editor {
        var commitClear = false
        var map: MutableMap<String?, Any?> = ConcurrentHashMap()

        var removeList: MutableList<String?> = ArrayList()
        override fun putString(key: String, value: String?): ShadowEditor {
            map[key] = value
            return this
        }

        override fun putStringSet(key: String, values: Set<String>?): ShadowEditor {
            map[key] = HashSet(values)
            return this
        }

        override fun putInt(key: String, value: Int): ShadowEditor {
            map[key] = value
            return this
        }

        override fun putLong(key: String, value: Long): ShadowEditor {
            map[key] = value
            return this
        }

        override fun putFloat(key: String, value: Float): ShadowEditor {
            map[key] = value
            return this
        }

        override fun putBoolean(key: String, value: Boolean): ShadowEditor {
            map[key] = value
            return this
        }

        override fun remove(key: String): ShadowEditor {
            map.remove(key)
            removeList.add(key)
            return this
        }

        override fun clear(): ShadowEditor {
            commitClear = true
            map.clear()
            removeList.clear()
            return this
        }

        override fun commit(): Boolean {
            return try {
                apply()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        override fun apply() {
            editorCall.apply(map, removeList, commitClear)

            // Empty the cached data each time you submit
            map.clear()
            commitClear = false
            removeList.clear()
        }
    }

}