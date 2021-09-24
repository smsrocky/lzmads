package com.ifmvo.togetherad.csj.provider

import android.app.Activity
import com.bytedance.sdk.openadsdk.*
import com.ifmvo.togetherad.core.listener.FullVideoListener
import com.ifmvo.togetherad.core.listener.NewInterListener
import com.ifmvo.togetherad.csj.TogetherAdCsj

/**
 *
 * 穿山甲新插屏全屏广告
 *
 */
abstract class CsjProviderNewInter : CsjProviderBanner() {
    private var mFllScreenVideoAd: TTFullScreenVideoAd? = null
    override fun requestNewInterAd(activity: Activity, adProviderType: String, alias: String, listener: NewInterListener) {
        callbackNewInterStartRequest(adProviderType, alias, listener)

        val adSlotBuilder = AdSlot.Builder()
        adSlotBuilder.setCodeId(TogetherAdCsj.idMapCsj[alias])
        //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可且仅是模板渲染的代码位ID使用，非模板渲染代码位切勿使用
        if (CsjProvider.NewInterVideo.isExpress) {
            adSlotBuilder.setExpressViewAcceptedSize(500f, 500f)
        }
        adSlotBuilder.setSupportDeepLink(CsjProvider.NewInterVideo.supportDeepLink)
        adSlotBuilder.setOrientation(CsjProvider.NewInterVideo.orientation)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL

        TogetherAdCsj.mTTAdManager.createAdNative(activity).loadFullScreenVideoAd(adSlotBuilder.build(), object : TTAdNative.FullScreenVideoAdListener {
            override fun onFullScreenVideoAdLoad(fullScreenVideoAd: TTFullScreenVideoAd?) {
                mFllScreenVideoAd = fullScreenVideoAd

                mFllScreenVideoAd?.setFullScreenVideoAdInteractionListener(object : TTFullScreenVideoAd.FullScreenVideoAdInteractionListener {
                    override fun onSkippedVideo() {}

                    override fun onAdShow() {
                        callbackNewInterShow(adProviderType, listener)
                    }

                    override fun onAdVideoBarClick() {
                        callbackNewInterClicked(adProviderType, listener)
                    }

                    override fun onVideoComplete() {
                        callbackNewInterComplete(adProviderType, listener)
                    }

                    override fun onAdClose() {
                        callbackNewInterClosed(adProviderType, listener)
                    }
                })

                callbackNewInterLoaded(adProviderType, alias, listener)
            }

            override fun onFullScreenVideoCached() {
                callbackNewInterCached(adProviderType, listener)
            }

            /**
             * 新sdk提供
             */
            override fun onFullScreenVideoCached(p0: TTFullScreenVideoAd?) {

            }

            override fun onError(errorCode: Int, errorMsg: String?) {
                callbackNewInterFailed(adProviderType, alias, listener, errorCode, errorMsg)
            }
        })
    }

    override fun showNewInterAd(activity: Activity): Boolean {
        val ritScenes = CsjProvider.NewInterVideo.ritScenes
        if (ritScenes != null) {
            mFllScreenVideoAd?.showFullScreenVideoAd(activity, ritScenes, null)
        } else {
            mFllScreenVideoAd?.showFullScreenVideoAd(activity)
        }
        mFllScreenVideoAd = null
        return true
    }
}