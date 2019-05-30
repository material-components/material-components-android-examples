package com.materialstudies.reply.ui.nav

import com.google.android.material.shape.EdgeTreatment
import com.google.android.material.shape.ShapePath
import java.lang.IllegalArgumentException

/**
 * An edge treatment which draws a semicircle cutout at any point along the edge.
 *
 * @param cutoutMargin Additional width to be added to the [cutoutDiameter], resulting in a
 *      larger total cutout size.
 * @param cutoutRoundedCornerRadius The radius of the each of the corners where the semicircle
 *      meets the straight edge.
 * @param cutoutVerticalOffset The amount the cutout should be lifted up in relation to the circle's
 *      middle.
 * @param cutoutDiameter The diameter of the semicircle to be cutout.
 * @param cutoutHorizontalOffset The horizontal offset, from the middle of the edge, where the
 *      cutout should be drawn.
 */
class SemiCircleEdgeCutoutTreatment(
        private var cutoutMargin: Float = 0F,
        private var cutoutRoundedCornerRadius: Float = 0F,
        private var cutoutVerticalOffset: Float = 0F,
        private var cutoutDiameter: Float = 0F,
        private var cutoutHorizontalOffset: Float = 0F
) : EdgeTreatment() {

    companion object {
        private const val ARC_QUARTER = 90
        private const val ARC_HALF = 180
        private const val ANGLE_UP = 270
        private const val ANGLE_LEFT = 180
    }

    init {
        if (cutoutVerticalOffset < 0) {
            throw IllegalArgumentException("cradleVertialOffset must be positive")
        }
    }

    override fun getEdgePath(
            length: Float,
            center: Float,
            interpolation: Float,
            shapePath: ShapePath
    ) {
        if (cutoutDiameter == 0f) {
            // There is no cutout to draw.
            shapePath.lineTo(length, 0f)
            return
        }

        val cradleDiameter = cutoutMargin * 2 + cutoutDiameter
        val cradleRadius = cradleDiameter / 2f
        val roundedCornerOffset = interpolation * cutoutRoundedCornerRadius
        val middle = length / 2f + cutoutHorizontalOffset

        val verticalOffset = interpolation * cutoutVerticalOffset +
                (1 - interpolation) * cradleRadius
        val verticalOffsetRatio = verticalOffset / cradleRadius

        if (verticalOffsetRatio >= 1.0f) {
            // Vertical offset is so high that there's no curve to draw in the edge. The circle is
            // actually above the edge, so just draw a straight line.
            shapePath.lineTo(length, 0f)
            return
        }

        // Calculate the path of the cutout by calculating the location of two adjacent circles. One
        // circle is for the rounded corner. If the rounded corner circle radius is 0 the corner
        // will not be rounded. The other circle is the cutout.

        // Calculate the X distance between the center of the two adjacent circles using pythagorean
        // theorem.
        val distanceBetweenCenters = cradleRadius + roundedCornerOffset
        val distanceBetweenCentersSquared = distanceBetweenCenters * distanceBetweenCenters
        val distanceY = verticalOffset + roundedCornerOffset
        val distanceX = Math.sqrt(
                (distanceBetweenCentersSquared - distanceY * distanceY).toDouble()
        ).toFloat()

        // Calculate the x position of the rounded corner circles.
        val leftRoundedCornerCircleX = middle - distanceX
        val rightRoundedCornerCircleX = middle + distanceX

        // Calculate the arc between the center of the two circles.
        val cornerRadiusArcLength = Math.toDegrees(
                Math.atan((distanceX / distanceY).toDouble())
        ).toFloat()
        val cutoutArcOffset = ARC_QUARTER - cornerRadiusArcLength

        // Draw the starting line up to the left rounded corner.
        shapePath.lineTo(leftRoundedCornerCircleX - roundedCornerOffset, 0f)

        // Draw the arc for the left rounded corner circle. The bounding box is the area around the
        // circle's center which is at (leftRoundedCornerCircleX, roundedCornerOffset).
        shapePath.addArc(
                leftRoundedCornerCircleX - roundedCornerOffset,
                0f,
                leftRoundedCornerCircleX + roundedCornerOffset,
                roundedCornerOffset * 2,
                ANGLE_UP.toFloat(),
                cornerRadiusArcLength)

        // Draw the cutout circle.
        shapePath.addArc(
                middle - cradleRadius,
                -cradleRadius - verticalOffset,
                middle + cradleRadius,
                cradleRadius - verticalOffset,
                ANGLE_LEFT - cutoutArcOffset,
                cutoutArcOffset * 2 - ARC_HALF)

        // Draw an arc for the right rounded corner circle. The bounding box is the area around the
        // circle's center which is at (rightRoundedCornerCircleX, roundedCornerOffset).
        shapePath.addArc(
                rightRoundedCornerCircleX - roundedCornerOffset,
                0f,
                rightRoundedCornerCircleX + roundedCornerOffset,
                roundedCornerOffset * 2,
                ANGLE_UP - cornerRadiusArcLength,
                cornerRadiusArcLength)

        // Draw the ending line after the right rounded corner.
        shapePath.lineTo(length, 0f)
    }

}

