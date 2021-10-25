package com.ifmvo.togetherad.core.custom.native_

import android.view.ViewGroup
import com.ifmvo.togetherad.core.listener.NativeViewListener
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * 原生信息流展示
 *
 */
abstract class BaseNativeView {
    abstract fun showNative(@NotNull adProviderType: String, @NotNull adObject: Any, @NotNull container: ViewGroup, @Nullable listener: NativeViewListener? = null)
}