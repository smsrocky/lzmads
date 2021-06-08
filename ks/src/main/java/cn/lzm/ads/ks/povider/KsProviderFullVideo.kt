package cn.lzm.ads.ks.povider

import android.app.Activity
import cn.lzm.ads.ks.TogetherAdKs
import com.ifmvo.togetherad.core.listener.FullVideoListener
import com.ifmvo.togetherad.core.provider.BaseAdProvider
import com.kwad.sdk.api.*

/**
 * 快手全屏視頻類
 *
 */
abstract class KsProviderFullVideo :BaseAdProvider() {
    private var mFullScreenVideoAd: KsFullScreenVideoAd? = null
    private var screenOrientation = SdkConfig.SCREEN_ORIENTATION_PORTRAIT //默认竖屏视频广告
    private var mListener:FullVideoListener? = null
    private var mAdProviderType:String? = null
    private var mAlias:String? = null

    override fun requestFullVideoAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        listener: FullVideoListener
    ) {
        mListener = listener
        mAlias = alias
        mAdProviderType = adProviderType
        callbackFullVideoStartRequest(adProviderType, alias, listener)

        // 构造 KsScene 的时候添加屏幕方向
        val scene = KsScene.Builder(TogetherAdKs.idMapKs[alias]!!.toLong())
            .screenOrientation(screenOrientation).build() // 此为测试posId，请联系快手平台申请正式posId
        KsAdSDK.getLoadManager().loadFullScreenVideoAd(scene,object :
            KsLoadManager.FullScreenVideoAdListener {
            override fun onError(code: Int, message: String?) {
                callbackFullVideoFailed(adProviderType, alias, listener, code, message)
            }

            override fun onRequestResult(result: Int) {
            }

            override fun onFullScreenVideoAdLoad(adList: MutableList<KsFullScreenVideoAd>?) {
                if (!adList.isNullOrEmpty()) {
                    callbackFullVideoLoaded(adProviderType, alias, listener)
                    mFullScreenVideoAd = adList[0]
                } else {
                    callbackFullVideoFailed(adProviderType, alias, listener, -1, "快手广告列表返回为空")
                }
            }

        })
    }

    override fun showFullVideoAd(activity: Activity): Boolean {
        if (mFullScreenVideoAd == null || mFullScreenVideoAd?.isAdEnable == false) {
            return false
        }
        mFullScreenVideoAd?.setFullScreenVideoAdInteractionListener(object :
            KsFullScreenVideoAd.FullScreenVideoAdInteractionListener {
            override fun onAdClicked() {
                callbackFullVideoClicked(mAdProviderType!!, mListener!!)
            }

            override fun onPageDismiss() {
                callbackFullVideoClosed(mAdProviderType!!, mListener!!)
            }

            override fun onVideoPlayError(code: Int, extra: Int) {
                callbackFullVideoFailed(mAdProviderType!!, mAlias!!, mListener!!, code, extra.toString())
            }

            override fun onVideoPlayEnd() {
                callbackFullVideoComplete(mAdProviderType!!, mListener!!)
            }

            override fun onVideoPlayStart() {
                callbackFullVideoShow(mAdProviderType!!, mListener!!)
            }

            override fun onSkippedVideo() {
            }

        })
        if (!activity.isFinishing) {
            val videoConfig = KsVideoPlayConfig.Builder()
                .videoSoundEnable(KsProvider.FullVideo.soundEnabled)
                .showLandscape(KsProvider.FullVideo.showLandscape)
                .build()
            mFullScreenVideoAd?.showFullScreenVideoAd(activity,videoConfig)
        }
        return true
    }

}