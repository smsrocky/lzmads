package cn.lzm.ads.ks.povider

import android.app.Activity
import android.view.ViewGroup
import com.ifmvo.togetherad.core.listener.*
import com.ifmvo.togetherad.core.provider.BaseAdProvider
import com.kwad.sdk.api.KsAdSDK
import com.kwad.sdk.api.KsLoadManager
import com.kwad.sdk.api.SdkConfig

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
        TODO("Not yet implemented")
    }

    override fun destroyBannerAd() {
        TODO("Not yet implemented")
    }

    override fun requestInterAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        listener: InterListener
    ) {
        TODO("Not yet implemented")
    }

    override fun showInterAd(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun destroyInterAd() {
        TODO("Not yet implemented")
    }

    override fun getNativeAdList(
        activity: Activity,
        adProviderType: String,
        alias: String,
        maxCount: Int,
        listener: NativeListener
    ) {
        TODO("Not yet implemented")
    }

    override fun nativeAdIsBelongTheProvider(adObject: Any): Boolean {
        TODO("Not yet implemented")
    }

    override fun resumeNativeAd(adObject: Any) {
        TODO("Not yet implemented")
    }

    override fun pauseNativeAd(adObject: Any) {
        TODO("Not yet implemented")
    }

    override fun destroyNativeAd(adObject: Any) {
        TODO("Not yet implemented")
    }

    override fun getNativeExpressAdList(
        activity: Activity,
        adProviderType: String,
        alias: String,
        adCount: Int,
        listener: NativeExpressListener
    ) {
        TODO("Not yet implemented")
    }

    override fun destroyNativeExpressAd(adObject: Any) {
        TODO("Not yet implemented")
    }

    override fun nativeExpressAdIsBelongTheProvider(adObject: Any): Boolean {
        TODO("Not yet implemented")
    }

    override fun getNativeExpress2AdList(
        activity: Activity,
        adProviderType: String,
        alias: String,
        adCount: Int,
        listener: NativeExpress2Listener
    ) {
        TODO("Not yet implemented")
    }

    override fun destroyNativeExpress2Ad(adObject: Any) {
        TODO("Not yet implemented")
    }

    override fun nativeExpress2AdIsBelongTheProvider(adObject: Any): Boolean {
        TODO("Not yet implemented")
    }

    override fun requestRewardAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        listener: RewardListener
    ) {
        TODO("Not yet implemented")
    }

    override fun showRewardAd(activity: Activity): Boolean {
        TODO("Not yet implemented")
    }

}