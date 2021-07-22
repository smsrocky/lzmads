package cn.lzm.ads.mintegral.provider

import android.app.Activity
import android.view.ViewGroup
import com.ifmvo.togetherad.core.custom.splashSkip.BaseSplashSkipView
import com.ifmvo.togetherad.core.listener.*
import com.mbridge.msdk.out.MBSplashHandler

open class MintegralProvider : MintegralProviderSplash() {
    object Splash {
        var mbSplashHandler: MBSplashHandler? = null
        //超时时间
        var maxFetchDelay:Long = 4
        var countDownS = 5
        //自定义按钮
        var customSkipView: BaseSplashSkipView? = null
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
        return true
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
        return true
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
        return true
    }

}