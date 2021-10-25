package com.ifmvo.togetherad.core.custom.native_.imageloader

import android.content.Context
import org.jetbrains.annotations.NotNull
import android.widget.ImageView

/**
 * 加载图片的接口
 *
 */
interface AdImageLoader {
    fun loadImage(@NotNull context: Context, @NotNull imageView: ImageView, @NotNull imgUrl: String)
}