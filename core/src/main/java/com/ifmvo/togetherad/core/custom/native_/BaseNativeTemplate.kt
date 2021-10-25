package com.ifmvo.togetherad.core.custom.native_

abstract class BaseNativeTemplate {
    abstract fun getNativeView(adProviderType: String): BaseNativeView?
}