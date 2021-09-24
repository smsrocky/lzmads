package cn.lzm.ads.gromore.provider

import android.app.Activity
import cn.lzm.ads.gromore.TogetherAdGroMore
import com.bytedance.msdk.api.*
import com.bytedance.msdk.api.reward.RewardItem
import com.bytedance.msdk.api.reward.TTRewardAd
import com.bytedance.msdk.api.reward.TTRewardedAdListener
import com.bytedance.msdk.api.reward.TTRewardedAdLoadCallback
import com.ifmvo.togetherad.core.listener.RewardListener
import java.util.*

abstract class GroMoreProviderReward : GroMoreProviderFullVideo() {
    private var mttRewardAd: TTRewardAd? = null
    private var mActivity:Activity? = null
    private var mListener: RewardListener? = null
    private var mAdProviderType:String? = null
    private var mAlias:String? = null
    private var retried:Boolean = false //失败后,重试一次
    private var loadSuccess:Boolean = false

    private var mSettingConfigCallback = TTSettingConfigCallback {
        loadRewardAd(mActivity!!, mAdProviderType!!, mAlias!!,mListener!!)
    }

    override fun destroyRewardVideoAd() {
        super.destroyRewardVideoAd()
        TTMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback)
        mttRewardAd?.destroy()
    }

    fun loadRewardAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        listener: RewardListener
    ) {
        callbackRewardStartRequest(adProviderType, alias, listener)
        /**
         * 注：每次加载激励视频广告的时候需要新建一个TTRewardAd，否则可能会出现广告填充问题
         * （ 例如：mttRewardAd = new TTRewardAd(this, adUnitId);）
         */
        mttRewardAd = TTRewardAd(activity, TogetherAdGroMore.idMapGroMore[alias])
        val videoOption = TTVideoOption.Builder()
            .setMuted(GroMoreProvider.Reward.isMuted) //对所有SDK的激励广告生效，除需要在平台配置的SDK，如穿山甲SDK
            .setAdmobAppVolume(0f) //配合Admob的声音大小设置[0-1]
            .build()
        val customData: MutableMap<String, String> = HashMap()
        customData[AdSlot.CUSTOM_DATA_KEY_PANGLE] = "pangle media_extra"
        customData[AdSlot.CUSTOM_DATA_KEY_GDT] = "gdt custom data"
        customData[AdSlot.CUSTOM_DATA_KEY_KS] = "ks custom data"
        // 其他需要透传给adn的数据。
        //创建广告请求参数AdSlot,具体参数含义参考文档
        // 其他需要透传给adn的数据。

        //创建广告请求参数AdSlot,具体参数含义参考文档
        val adSlotBuilder = AdSlot.Builder()
            .setTTVideoOption(videoOption)
            .setRewardName("观影币") //奖励的名称
            .setRewardAmount(10) //奖励的数量
            .setUserID(UUID.randomUUID().toString()) //用户id,必传参数
            .setAdStyleType(AdSlot.TYPE_EXPRESS_AD) // 确保该聚合广告位下所有gdt代码位都是同一种类型（模版或非模版）。
            .setCustomData(customData) // 激励视频开启服务端验证时，透传给adn的自定义数据。
            .setOrientation(GroMoreProvider.Reward.orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
        //请求广告
        mttRewardAd?.loadRewardAd(adSlotBuilder.build(), object : TTRewardedAdLoadCallback {
            override fun onRewardVideoLoadFail(adError: AdError) {
                loadSuccess = false
                var errCode = 0
                var errMsg = ""
                if (adError != null) {
                    errCode = adError.thirdSdkErrorCode
                    errMsg = adError.thirdSdkErrorMessage
                }
                callbackRewardFailed(adProviderType, alias, listener, errCode, errMsg)
            }

            override fun onRewardVideoAdLoad() {
                loadSuccess = true
                callbackRewardLoaded(adProviderType, alias, listener)
            }

            override fun onRewardVideoCached() {
                loadSuccess = true
                callbackRewardVideoCached(adProviderType, listener)
            }
        })
    }

    override fun requestRewardAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        listener: RewardListener
    ) {
        /**
         * 判断当前是否存在config 配置 ，如果存在直接加载广告 ，如果不存在则注册config加载回调
         */
        if (TTMediationAdSdk.configLoadSuccess()) {
            loadRewardAd(activity, adProviderType,alias,listener)
        } else {
            TTMediationAdSdk.registerConfigCallback(mSettingConfigCallback) //不用使用内部类，否则在ondestory中无法移除该回调
        }
    }

    override fun showRewardAd(activity: Activity): Boolean {
        if (loadSuccess && mttRewardAd != null && mttRewardAd!!.isReady) {
            //在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
            //该方法直接展示广告，如果展示失败了（如过期），会回调onVideoError()
            //展示广告，并传入广告展示的场景
            mttRewardAd!!.showRewardAd(activity, object : TTRewardedAdListener {
                /**
                 * 广告的展示回调 每个广告仅回调一次
                 */
                override fun onRewardedAdShow() {
                    callbackRewardShow(mAdProviderType!!,mListener!!)
                    callbackRewardExpose(mAdProviderType!!,mListener!!)
                }

                /**
                 * show失败回调。如果show时发现无可用广告（比如广告过期或者isReady=false），会触发该回调。
                 * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
                 * @param adError showFail的具体原因
                 */
                override fun onRewardedAdShowFail(adError: AdError) {
                    var errCode = 0
                    var errMsg = ""
                    if (adError != null) {
                        errCode = adError.thirdSdkErrorCode
                        errMsg = adError.thirdSdkErrorMessage
                    }
                    if (!retried) {
                        retried = true
                        loadRewardAd(mActivity!!, mAdProviderType!!, mAlias!!,mListener!!)
                    } else {
                        callbackRewardFailed(mAdProviderType!!, mAlias!!, mListener!!, errCode, errMsg)
                    }
                }

                /**
                 * 注意Admob的激励视频不会回调该方法
                 */
                override fun onRewardClick() {
                    callbackRewardClicked(mAdProviderType!!,mListener!!)
                }

                /**
                 * 广告关闭的回调
                 */
                override fun onRewardedAdClosed() {
                    callbackRewardClosed(mAdProviderType!!, mListener!!)
                    mttRewardAd = null
                }

                /**
                 * 视频播放完毕的回调 Admob广告不存在该回调
                 */
                override fun onVideoComplete() {
                    callbackRewardVideoComplete(mAdProviderType!!, mListener!!)
                }

                /** 1、视频播放失败的回调*/
                override fun onVideoError() {
                    callbackRewardFailed(mAdProviderType!!, mAlias!!, mListener!!, -1001, "视频播放失败")
                }

                /** 激励视频播放完毕，验证是否有效发放奖励的回调 */
                override fun onRewardVerify(rewardItem: RewardItem) {
                    val customData = rewardItem.customData
                    if (customData != null) {
                        val adnName = customData[RewardItem.KEY_ADN_NAME] as String?
                        when (adnName) {
                            RewardItem.KEY_GDT -> {}
                            RewardItem.KEY_PANGLE->{}
                            RewardItem.KEY_BAIDU->{}
                            RewardItem.KEY_KS->{}
                            RewardItem.KEY_MINTEGRAL->{}
                        }
                    }
                }

                /**
                 * - Mintegral GDT Admob广告不存在该回调
                 */
                override fun onSkippedVideo() {}
            })
        } else {
            return false
        }
        return true
    }
}