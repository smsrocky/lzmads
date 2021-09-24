package cn.lzm.ads.gromore.provider

import android.app.Activity
import cn.lzm.ads.gromore.TogetherAdGroMore
import cn.lzm.ads.gromore.util.VideoOptionUtil
import com.bytedance.msdk.api.*
import com.bytedance.msdk.api.fullVideo.TTFullVideoAd
import com.bytedance.msdk.api.fullVideo.TTFullVideoAdListener
import com.bytedance.msdk.api.fullVideo.TTFullVideoAdLoadCallback
import com.ifmvo.togetherad.core.listener.FullVideoListener
import com.ifmvo.togetherad.core.provider.BaseAdProvider
import java.util.*

/**
 * 快手全屏視頻類
 *
 */
abstract class GroMoreProviderFullVideo :BaseAdProvider() {
    private var mActivity:Activity? = null
    private var mListener:FullVideoListener? = null
    private var mAdProviderType:String? = null
    private var mAlias:String? = null
    private var retried:Boolean = false //失败后,重试一次
    private var loadSuccess:Boolean = false

    private var mTTFullVideoAd: TTFullVideoAd? = null
    private val mSettingConfigCallback = TTSettingConfigCallback {
        loadFullVideoAd(mActivity!!,mAlias!!,mAdProviderType!!,mListener!!)
    }

    override fun destroyFullVideoAd() {
        super.destroyFullVideoAd()
        //注销config回调
        TTMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback)
        mTTFullVideoAd?.destroy()
        mActivity = null
    }

    fun loadFullVideoAd(activity: Activity,
                        adProviderType: String,
                        alias: String,
                        listener: FullVideoListener) {
        callbackFullVideoStartRequest(adProviderType, alias, listener)
        /**
         * 注：每次加载全屏视频广告的时候需要新建一个TTFullVideoAd，否则可能会出现广告填充问题
         * （ 例如：mTTFullVideoAd = new TTFullVideoAd(this, adUnitId);）
         */
        mTTFullVideoAd = TTFullVideoAd(activity, TogetherAdGroMore.idMapGroMore[alias])
        //声音控制
        val videoOption: TTVideoOption = VideoOptionUtil.getTTVideoOption()
        //创建广告请求参数AdSlot,具体参数含义参考文档
        val adSlotBuilder = AdSlot.Builder()
            .setTTVideoOption(videoOption) //设置声音控制
            .setUserID(UUID.randomUUID().toString()) //用户id,必传参数
            .setMediaExtra("media_extra") //附加参数，可选
            .setOrientation(GroMoreProvider.FullVideo.orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL;
        //请求广告
        mTTFullVideoAd?.loadFullAd(adSlotBuilder.build(), object : TTFullVideoAdLoadCallback {
            override fun onFullVideoLoadFail(adError: AdError) {
                var errCode = 0
                var errMsg = ""
                if (adError != null) {
                    errCode = adError.thirdSdkErrorCode
                    errMsg = adError.thirdSdkErrorMessage
                }
                loadSuccess = false
                callbackFullVideoFailed(adProviderType, alias, listener, errCode, errMsg)
            }

            override fun onFullVideoAdLoad() {
                loadSuccess = true
                callbackFullVideoLoaded(adProviderType, alias, listener)
            }

            override fun onFullVideoCached() {
                loadSuccess = true
                callbackFullVideoCached(adProviderType, listener)
            }
        })
    }

    override fun requestFullVideoAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        listener: FullVideoListener
    ) {
        mListener = listener
        mAlias = alias
        mAdProviderType = adProviderType
        mActivity = activity
        if (TTMediationAdSdk.configLoadSuccess()) {
            loadFullVideoAd(activity,adProviderType,alias,listener)
        } else {
            TTMediationAdSdk.registerConfigCallback(mSettingConfigCallback) //不能使用内部类，否则在ondestory中无法移除该回调
        }
    }

    override fun showFullVideoAd(activity: Activity): Boolean {
        if (loadSuccess && mTTFullVideoAd != null && mTTFullVideoAd!!.isReady) {
            //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
            //该方法直接展示广告，如果展示失败了（如过期），会回调onVideoError()
            //展示广告，并传入广告展示的场景
            mTTFullVideoAd?.showFullAd(activity, object : TTFullVideoAdListener {
                override fun onFullVideoAdShow() {
                    callbackFullVideoShow(mAdProviderType!!, mListener!!)
                }
                /**
                 * show失败回调。如果show时发现无可用广告（比如广告过期或者isReady=false），会触发该回调。
                 * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
                 * @param adError showFail的具体原因
                 */
                override fun onFullVideoAdShowFail(adError: AdError) {
                    var errCode = 0
                    var errMsg = ""
                    if (adError != null) {
                        errCode = adError.thirdSdkErrorCode
                        errMsg = adError.thirdSdkErrorMessage
                    }
                    // 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载
                    if (!retried) {
                        retried = true
                        loadFullVideoAd(mActivity!!,mAlias!!,mAdProviderType!!,mListener!!)
                    } else {
                        callbackFullVideoFailed(mAdProviderType!!, mAlias!!, mListener!!, errCode, errMsg)
                    }
                }

                override fun onFullVideoAdClick() {
                    callbackFullVideoClicked(mAdProviderType!!, mListener!!)
                }
                override fun onFullVideoAdClosed() {
                    callbackFullVideoClosed(mAdProviderType!!, mListener!!)
                }
                override fun onVideoComplete() {
                    callbackFullVideoComplete(mAdProviderType!!, mListener!!)
                }

                /**
                 * 1、视频播放失败的回调 - Mintegral GDT Admob广告不存在该回调；
                 */
                override fun onVideoError() {
                    callbackFullVideoFailed(mAdProviderType!!, mAlias!!, mListener!!, -1001, "视频播放失败")
                }

                override fun onSkippedVideo() {
                }
            })
        }
        return true
    }
}