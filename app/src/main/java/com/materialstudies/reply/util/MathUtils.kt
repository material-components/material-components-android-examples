package com.materialstudies.reply.util

object MathUtils {

    /**
     * Ensure [value] is no less than [min] and no greater than [max]
     */
    fun constrained(value: Float, min: Float, max: Float): Float {
        return Math.min(Math.max(value, min), max)
    }

    /**
     * Given [value] and, a Float within the range [inputMin] to [inputMax], linearly
     * scale and return that range's value to the new range [outputMin] to [outputMax].
     *
     * For example, a 5 on the range of 1 to 10 would return 50 on the range of 0 to 100.
     *
     * @param value the original number on the range [inputMin] to [inputMax]
     * @param inputMin The lower bounds for range which contains [value]
     * @param inputMax The upper bounds for the range which contains [value]
     * @param outputMin The lower bounds for the range which will contain the returned value
     * @param outputMax The upper bounds for the range which will contain the returned value
     *
     * @return A value between the range [outputMin] to [outputMax] which is equivalent to where
     *  [value] lies on the range [inputMin] to [inputMax].
     */
    fun normalize(
        value: Float,
        inputMin: Float,
        inputMax: Float,
        outputMin: Float,
        outputMax: Float
    ): Float {
        if (value < inputMin) {
            return outputMin
        } else if (value > inputMax) {
            return outputMax
        }

        return outputMin * (1 - (value - inputMin) / (inputMax - inputMin)) +
            outputMax * ((value - inputMin) / (inputMax - inputMin))
    }
}



