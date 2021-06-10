package cn.lzm.ads.ks.povider

import android.app.Activity
import android.view.ViewGroup
import com.ifmvo.togetherad.core.listener.*

open class KsProvider :KsProviderSplash() {

    object Splash {
        //超时时间
        var maxFetchDelay = 4000
    }

    object FullVideo {
        // 自动播放时为静音
        var soundEnabled = true

        //橫屏播放
        var showLandscape = false
    }

    override fun showBannerAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        container: ViewGroup,
        listener: BannerListener
    ) {
    }

    override fun destroyBannerAd() {
    }

    override fun requestInterAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        listener: InterListener
    ) {
    }

    override fun showInterAd(activity: Activity) {
    }

    override fun destroyInterAd() {
    }

    override fun getNativeAdList(
        activity: Activity,
        adProviderType: String,
        alias: String,
        maxCount: Int,
        listener: NativeListener
    ) {
    }

    override fun nativeAdIsBelongTheProvider(adObject: Any): Boolean {
        return false
    }

    override fun resumeNativeAd(adObject: Any) {
    }

    override fun pauseNativeAd(adObject: Any) {
    }

    override fun destroyNativeAd(adObject: Any) {
    }

    override fun getNativeExpressAdList(
        activity: Activity,
        adProviderType: String,
        alias: String,
        adCount: Int,
        listener: NativeExpressListener
    ) {
    }

    override fun destroyNativeExpressAd(adObject: Any) {
    }

    override fun nativeExpressAdIsBelongTheProvider(adObject: Any): Boolean {
        return false
    }

    override fun getNativeExpress2AdList(
        activity: Activity,
        adProviderType: String,
        alias: String,
        adCount: Int,
        listener: NativeExpress2Listener
    ) {
    }

    override fun destroyNativeExpress2Ad(adObject: Any) {
    }

    override fun nativeExpress2AdIsBelongTheProvider(adObject: Any): Boolean {
        return false
    }
}