package cn.lzm.ads.mintegral.provider

import android.app.Activity
import android.view.ViewGroup
import cn.lzm.ads.mintegral.TogetherAdMintegral
import com.ifmvo.togetherad.core.listener.SplashListener
import com.mbridge.msdk.out.MBSplashHandler
import com.mbridge.msdk.out.MBSplashLoadListener
import com.mbridge.msdk.out.MBSplashShowListener
import com.mbridge.msdk.out.MBridgeIds

/**
 * 快看开屏广告实现类
 *
 */
abstract class MintegralProviderSplash : MintegralProviderReward() {
    private var mListener: SplashListener? = null
    private var mAdProviderType: String? = null
    private var mAlias:String? = null

    private var mContainer: ViewGroup? = null
//    private var mbSplashHandler:MBSplashHandler? = null

    override fun loadOnlySplashAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        listener: SplashListener
    ) {
        mAlias = alias
        mListener = listener
        mAdProviderType = adProviderType

        callbackSplashStartRequest(adProviderType, alias, listener)
        var templateAndUnit = TogetherAdMintegral.idMapMintegral[alias]!!.split("_")
        MintegralProvider.Splash.mbSplashHandler = MBSplashHandler(
            activity,
            templateAndUnit[0],
            templateAndUnit[1],
            true,
            MintegralProvider.Splash.countDownS
        )
        MintegralProvider.Splash.mbSplashHandler?.setLoadTimeOut(MintegralProvider.Splash.maxFetchDelay)
        MintegralProvider.Splash.mbSplashHandler?.setSplashLoadListener(object : MBSplashLoadListener {
            override fun onLoadSuccessed(mbRidgeIds: MBridgeIds?, code: Int) {
                callbackSplashLoaded(adProviderType, alias, listener)
            }

            override fun onLoadFailed(mbRidgeIds: MBridgeIds?, message: String?, code: Int) {
                MintegralProvider.Splash.mbSplashHandler = null
                callbackSplashFailed(adProviderType, alias, listener, code, message)
            }

        })
        MintegralProvider.Splash.mbSplashHandler?.setSplashShowListener(object : MBSplashShowListener {
            override fun onShowSuccessed(mbRidgeIds: MBridgeIds?) {
                callbackSplashExposure(adProviderType, listener)
            }

            override fun onShowFailed(mbRidgeIds: MBridgeIds?, message: String?) {
                callbackSplashFailed(adProviderType, alias, listener, -1, message)
            }

            override fun onAdClicked(mbRidgeIds: MBridgeIds?) {
                callbackSplashClicked(adProviderType, listener)
            }

            override fun onDismiss(mbRidgeIds: MBridgeIds?, code: Int) {
                callbackSplashDismiss(adProviderType, listener)
            }

            override fun onAdTick(mbRidgeIds: MBridgeIds?, time: Long) {
            }

        })
        MintegralProvider.Splash.mbSplashHandler?.preLoad()
    }

    override fun showSplashAd(container: ViewGroup): Boolean {
        if (MintegralProvider.Splash.mbSplashHandler == null) return false
        mContainer = container
        container.removeAllViews()
        if (MintegralProvider.Splash.mbSplashHandler!!.isReady) {
            MintegralProvider.Splash.mbSplashHandler!!.show(container)
            return true
        }
        return false
    }

    override fun loadAndShowSplashAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        container: ViewGroup,
        listener: SplashListener
    ) {
        mListener = listener
        mAdProviderType = adProviderType

        callbackSplashStartRequest(adProviderType, alias, listener)
        var templateAndUnit = TogetherAdMintegral.idMapMintegral[alias]!!.split("_")
        MintegralProvider.Splash.mbSplashHandler = MBSplashHandler(
            activity,
            templateAndUnit[0],
            templateAndUnit[1],
            true,
            MintegralProvider.Splash.countDownS
        )
        MintegralProvider.Splash.mbSplashHandler?.setLoadTimeOut(MintegralProvider.Splash.maxFetchDelay)
        MintegralProvider.Splash.mbSplashHandler?.setSplashLoadListener(object : MBSplashLoadListener {
            override fun onLoadSuccessed(mbRidgeIds: MBridgeIds?, code: Int) {
                callbackSplashLoaded(adProviderType, alias, listener)
            }

            override fun onLoadFailed(mbRidgeIds: MBridgeIds?, message: String?, code: Int) {
                MintegralProvider.Splash.mbSplashHandler = null
                callbackSplashFailed(adProviderType, alias, listener, code, message)
            }

        })
        MintegralProvider.Splash.mbSplashHandler?.setSplashShowListener(object : MBSplashShowListener {
            override fun onShowSuccessed(mbRidgeIds: MBridgeIds?) {
                callbackSplashExposure(adProviderType, listener)
            }

            override fun onShowFailed(mbRidgeIds: MBridgeIds?, message: String?) {
                callbackSplashFailed(adProviderType, alias, listener, -1, message)
            }

            override fun onAdClicked(mbRidgeIds: MBridgeIds?) {
                callbackSplashClicked(adProviderType, listener)
            }

            override fun onDismiss(mbRidgeIds: MBridgeIds?, code: Int) {
                callbackSplashDismiss(adProviderType, listener)
            }

            override fun onAdTick(mbRidgeIds: MBridgeIds?, time: Long) {
            }

        })
        MintegralProvider.Splash.mbSplashHandler?.loadAndShow(container)
    }

}