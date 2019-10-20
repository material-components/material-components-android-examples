/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.materialstudies.owl.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader.TileMode.CLAMP
import androidx.annotation.StyleRes
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.ShapeAppearancePathProvider
import java.security.MessageDigest

/**
 * A Glide [Transformation] which applies a [ShapeAppearanceModel] to images.
 */
class ShapeAppearanceTransformation(
    @StyleRes private val shapeAppearanceId: Int
) : Transformation<Bitmap> {

    private var shapeAppearanceModel: ShapeAppearanceModel? = null

    @SuppressLint("RestrictedApi")
    override fun transform(
        context: Context,
        resource: Resource<Bitmap>,
        outWidth: Int,
        outHeight: Int
    ): Resource<Bitmap> {
        val model = shapeAppearanceModel ?: ShapeAppearanceModel.builder(
            context,
            shapeAppearanceId,
            0
        ).build()
            .also { shapeAppearanceModel = it }
        val bitmap = createBitmap(outWidth, outHeight)
        bitmap.applyCanvas {
            val path = Path().apply {
                val bounds = RectF(0f, 0f, outWidth.toFloat(), outHeight.toFloat())
                ShapeAppearancePathProvider().calculatePath(model, 1f, bounds, this)
            }
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = BitmapShader(resource.get(), CLAMP, CLAMP)
            }
            drawPath(path, paint)
        }
        return BitmapResource(bitmap, Glide.get(context).bitmapPool)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(javaClass.canonicalName!!.toByteArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShapeAppearanceTransformation

        if (shapeAppearanceId != other.shapeAppearanceId) return false
        if (shapeAppearanceModel != other.shapeAppearanceModel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shapeAppearanceId
        result = 31 * result + (shapeAppearanceModel?.hashCode() ?: 0)
        return result
    }

}
