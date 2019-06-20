package com.materialstudies.reply

import com.materialstudies.reply.data.EmailAttachment
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun lists_equalsShouldCompareContent() {

        val listOne = listOf(
            EmailAttachment(R.drawable.paris_1, "photo one"),
            EmailAttachment(R.drawable.paris_2, "photo two")
        )

        val listTwo = listOf(
            EmailAttachment(R.drawable.paris_1, "photo one"),
            EmailAttachment(R.drawable.paris_2, "photo two")
        )

        assert(listOne == listTwo)
    }
}
