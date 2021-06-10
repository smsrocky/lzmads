package cn.lzm.ads.ks.povider

import android.app.Activity
import cn.lzm.ads.ks.TogetherAdKs
import com.ifmvo.togetherad.core.listener.RewardListener
import com.kwad.sdk.api.*

abstract class KsProviderReward : KsProviderFullVideo() {
    private var rewardVideoAd: KsRewardVideoAd? = null

    override fun requestRewardAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        listener: RewardListener
    ) {
        callbackRewardStartRequest(adProviderType, alias, listener)
        val scene = KsScene.Builder(TogetherAdKs.idMapKs[alias]!!.toLong())
            .screenOrientation(SdkConfig.SCREEN_ORIENTATION_PORTRAIT).build()
        // 请求的期望屏幕方向传递为1，表示期望为竖屏
        KsAdSDK.getLoadManager()
            .loadRewardVideoAd(scene, object : KsLoadManager.RewardVideoAdListener {
                override fun onError(code: Int, msg: String) {
                    callbackRewardFailed(adProviderType, alias, listener, code, msg)
                    rewardVideoAd = null
                }

                override fun onRequestResult(adNumber: Int) {
                }

                override fun onRewardVideoAdLoad(adList: List<KsRewardVideoAd>?) {
                    if (adList != null && adList.size > 0) {
                        rewardVideoAd = adList[0]
                        rewardVideoAd?.setRewardAdInteractionListener(object :KsRewardVideoAd.RewardAdInteractionListener {
                            override fun onAdClicked() {
                                callbackRewardClicked(adProviderType, listener)
                            }

                            override fun onPageDismiss() {
                                callbackRewardClosed(adProviderType, listener)
                                rewardVideoAd = null
                            }

                            override fun onVideoPlayError(p0: Int, p1: Int) {
                            }

                            override fun onVideoPlayEnd() {
                                callbackRewardVideoComplete(adProviderType, listener)
                            }

                            override fun onVideoPlayStart() {
                                callbackRewardShow(adProviderType, listener)
                                callbackRewardExpose(adProviderType, listener)
                            }

                            override fun onRewardVerify() {
                                callbackRewardVerify(adProviderType, listener)
                            }

                        })
                        callbackRewardLoaded(adProviderType, alias, listener)
                    }
                }
            })
    }

    override fun showRewardAd(activity: Activity): Boolean {
        val videoPlayConfig = KsVideoPlayConfig.Builder()
            .showLandscape(false) // 横屏播放
            .build()
        if (rewardVideoAd != null && rewardVideoAd!!.isAdEnable) {
            rewardVideoAd?.showRewardVideoAd(activity, videoPlayConfig)
        }
        return true
    }
}