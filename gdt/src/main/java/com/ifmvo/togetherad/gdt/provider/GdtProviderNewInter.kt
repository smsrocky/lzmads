package com.ifmvo.togetherad.gdt.provider

import android.app.Activity
import com.ifmvo.togetherad.core.listener.FullVideoListener
import com.ifmvo.togetherad.core.listener.NewInterListener
import com.ifmvo.togetherad.gdt.TogetherAdGdt
import com.qq.e.ads.cfg.VideoOption
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener
import com.qq.e.comm.util.AdError


/**
 * 全屏视频广告
 * 广点通全屏插屏广告
 *
 */
abstract class GdtProviderNewInter : GdtProviderBanner() {

    private var NewInterAd: UnifiedInterstitialAD? = null

    override fun requestNewInterAd(activity: Activity, adProviderType: String, alias: String, listener: NewInterListener) {

        callbackNewInterStartRequest(adProviderType, alias, listener)

        NewInterAd = UnifiedInterstitialAD(activity,TogetherAdGdt.idMapGDT[alias],object :UnifiedInterstitialADListener{
            override fun onADReceive() {
                TogetherAdGdt.downloadConfirmListener?.let {
                    NewInterAd?.setDownloadConfirmListener(it)
                }
                callbackNewInterLoaded(adProviderType, alias, listener)
            }

            override fun onVideoCached() {
                callbackNewInterCached(adProviderType, listener)
            }

            override fun onNoAD(adError: AdError?) {
                callbackNewInterFailed(adProviderType, alias, listener, adError?.errorCode, adError?.errorMsg)
            }

            override fun onADOpened() {
            }

            override fun onADExposure() {
                callbackNewInterShow(adProviderType, listener)
            }

            override fun onADClicked() {
                callbackNewInterClicked(adProviderType, listener)
            }

            override fun onADLeftApplication() {
            }

            override fun onADClosed() {
                callbackNewInterClosed(adProviderType, listener)
            }

            override fun onRenderSuccess() {
            }

            override fun onRenderFail() {
            }

        })

        val option = VideoOption.Builder()
                .setAutoPlayMuted(GdtProvider.NewInter.autoPlayMuted)
                .setAutoPlayPolicy(GdtProvider.NewInter.autoPlayPolicy)
                .build()
        NewInterAd?.setVideoOption(option)
        NewInterAd?.setVideoPlayPolicy(GdtProvider.NewInter.videoPlayPolicy)
        NewInterAd?.setMaxVideoDuration(GdtProvider.NewInter.maxVideoDuration)
        NewInterAd?.setMaxVideoDuration(GdtProvider.NewInter.minVideoDuration)
        NewInterAd?.setMediaListener(object :UnifiedInterstitialMediaListener {
            override fun onVideoPageOpen() {}
            override fun onVideoLoading() {}
            override fun onVideoReady(p0: Long) {}
            override fun onVideoInit() {}
            override fun onVideoPause() {}
            override fun onVideoPageClose() {}
            override fun onVideoStart() {}
            override fun onVideoComplete() {
                callbackNewInterComplete(adProviderType, listener)
            }
            override fun onVideoError(adError: AdError?) {}
        })
        NewInterAd?.loadFullScreenAD()
    }

    override fun showNewInterAd(activity: Activity): Boolean {
        if (NewInterAd?.isValid != true) {
            return false
        }
        NewInterAd?.showFullScreenAD(activity)
        return true
    }
}