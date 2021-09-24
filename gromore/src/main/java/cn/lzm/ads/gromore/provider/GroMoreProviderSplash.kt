package cn.lzm.ads.gromore.provider

import android.app.Activity
import android.os.CountDownTimer
import android.view.ViewGroup
import com.bytedance.msdk.api.*
import com.bytedance.msdk.api.splash.TTSplashAd
import com.bytedance.msdk.api.splash.TTSplashAdListener
import com.bytedance.msdk.api.splash.TTSplashAdLoadCallback
import com.ifmvo.togetherad.core.listener.SplashListener
import com.ifmvo.togetherad.csj.TogetherAdCsj
import com.ifmvo.togetherad.csj.provider.CsjProvider
import kotlin.math.roundToInt

/**
 * 快看开屏广告实现类
 *
 */
abstract class GroMoreProviderSplash : GroMoreProviderReward() {
    private var mListener: SplashListener? = null
    private var mAdProviderType: String? = null
    private var mAlias: String? = null

    private var mTimer: CountDownTimer? = null
    private var mSplashAd: TTSplashAd? = null

    // 百度开屏广告点击跳转落地页后倒计时不暂停，即使在看落地页，倒计时结束后仍然会强制跳转，需要特殊处理：
    // 检测到广告被点击，且走了activity的onPaused证明跳转到了落地页，这时候onAdDismiss回调中不进行跳转，而是在activity的onResume中跳转。
    private var isBaiduSplashAd = false
    private var baiduSplashAdClicked = false

    override fun isBaiduSplash(): Boolean {
        return isBaiduSplash() && baiduSplashAdClicked
    }

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

        /**
         * 注：每次加载开屏广告的时候需要新建一个TTSplashAd，否则可能会出现广告填充问题
         * （ 例如：mTTSplashAd = new TTSplashAd(this, mAdUnitId);）
         */
        mSplashAd = TTSplashAd(activity, TogetherAdCsj.idMapCsj[alias])
        mSplashAd?.setTTAdSplashListener(object : TTSplashAdListener {
            override fun onAdClicked() {
                baiduSplashAdClicked = true
                callbackSplashClicked(adProviderType, listener)
            }

            override fun onAdShow() {
                callbackSplashExposure(adProviderType, listener)
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             * @param adError showFail的具体原因
             */
            override fun onAdShowFail(adError: AdError) {
                var errCode = 0
                var errMsg = ""
                if (adError != null) {
                    errCode = adError.thirdSdkErrorCode
                    errMsg = adError.thirdSdkErrorMessage
                }
                callbackSplashFailed(adProviderType, alias, listener, errCode, errMsg)
            }

            override fun onAdSkip() {
                GroMoreProvider.Splash.customSkipView = null
                callbackSplashDismiss(adProviderType, listener)
            }

            override fun onAdDismiss() {
                CsjProvider.Splash.customSkipView = null
                callbackSplashDismiss(adProviderType, listener)
            }
        })

        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        val adSlot = AdSlot.Builder()
            .setImageAdSize(
                GroMoreProvider.Splash.imageAcceptedSizeWidth,
                GroMoreProvider.Splash.imageAcceptedSizeHeight
            ) // 既适用于原生类型，也适用于模版类型。
            .setSplashButtonType(TTAdConstant.SPLASH_BUTTON_TYPE_FULL_SCREEN)
            .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP)
            .build()
        //自定义兜底方案 选择使用
        var ttNetworkRequestInfo: TTNetworkRequestInfo? = null
        //穿山甲兜底，参数分别是appId和adn代码位。注意第二个参数是代码位，而不是广告位。
        //穿山甲兜底，参数分别是appId和adn代码位。注意第二个参数是代码位，而不是广告位。
//        ttNetworkRequestInfo = PangleNetworkRequestInfo("appId", "代码位") //gdt兜底
//        ttNetworkRequestInfo = new GdtNetworkRequestInfo("1101152570", "8863364436303842593"); //ks兜底
//        ttNetworkRequestInfo = new KsNetworkRequestInfo("90009", "4000000042"); //百度兜底
//        ttNetworkRequestInfo = new BaiduNetworkRequestInfo("e866cfb0", "2058622");
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mSplashAd?.loadAd(adSlot, ttNetworkRequestInfo, object : TTSplashAdLoadCallback {
            override fun onSplashAdLoadFail(adError: AdError) {
                var errCode = 0
                var errMsg = ""
                if (adError != null) {
                    errCode = adError.thirdSdkErrorCode
                    errMsg = adError.thirdSdkErrorMessage
                }
                callbackSplashFailed(adProviderType, alias, listener, errCode, errMsg)
            }

            override fun onSplashAdLoadSuccess() {
                if (mSplashAd != null) {
                    callbackSplashLoaded(adProviderType, alias, listener)
                    isBaiduSplashAd =
                        mSplashAd?.getAdNetworkPlatformId() == NetworkPlatformConst.SDK_NAME_BAIDU
                } else {
                    callbackSplashFailed(
                        adProviderType,
                        alias,
                        listener,
                        null,
                        "请求成功，但是返回的广告为null"
                    )
                }
            }

            override fun onAdLoadTimeout() {
                callbackSplashFailed(adProviderType, alias, listener, null, "请求超时了")
            }
        }, GroMoreProvider.Splash.maxFetchDelay)
    }

    override fun showSplashAd(container: ViewGroup): Boolean {
        if (mSplashAd == null) return false
        container.removeAllViews()
        mSplashAd?.showAd(container)

        val customSkipView = CsjProvider.Splash.customSkipView
        val skipView = customSkipView?.onCreateSkipView(container.context)

        if (customSkipView != null) {
            skipView?.run {
                container.addView(this, customSkipView.getLayoutParams())
                setOnClickListener {
                    mTimer?.cancel()
                    if (mAdProviderType != null && mListener != null) {
                        CsjProvider.Splash.customSkipView = null
                        callbackSplashDismiss(mAdProviderType!!, mListener!!)
                    }
                }
            }

            //开始倒计时
            mTimer?.cancel()
            mTimer = object : CountDownTimer(5000, 1000) {
                override fun onFinish() {
                    if (mAdProviderType != null && mListener != null) {
                        CsjProvider.Splash.customSkipView = null
                        callbackSplashDismiss(mAdProviderType!!, mListener!!)
                    }
                }

                override fun onTick(millisUntilFinished: Long) {
                    val second = (millisUntilFinished / 1000f).roundToInt()
                    customSkipView.handleTime(second)
                }
            }
            mTimer?.start()
        }
        return true
    }

    override fun loadAndShowSplashAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        container: ViewGroup,
        listener: SplashListener
    ) {
        mAlias = alias
        mListener = listener
        mAdProviderType = adProviderType
        callbackSplashStartRequest(adProviderType, alias, listener)
        /**
         * 注：每次加载开屏广告的时候需要新建一个TTSplashAd，否则可能会出现广告填充问题
         * （ 例如：mTTSplashAd = new TTSplashAd(this, mAdUnitId);）
         */
        mSplashAd = TTSplashAd(activity, TogetherAdCsj.idMapCsj[alias])
        mSplashAd?.setTTAdSplashListener(object : TTSplashAdListener {
            override fun onAdClicked() {
                baiduSplashAdClicked = true
                callbackSplashClicked(adProviderType, listener)
            }

            override fun onAdShow() {
                callbackSplashExposure(adProviderType, listener)
            }

            /**
             * show失败回调。如果show时发现无可用广告（比如广告过期），会触发该回调。
             * 开发者应该结合自己的广告加载、展示流程，在该回调里进行重新加载。
             * @param adError showFail的具体原因
             */
            override fun onAdShowFail(adError: AdError) {
                var errCode = 0
                var errMsg = ""
                if (adError != null) {
                    errCode = adError.thirdSdkErrorCode
                    errMsg = adError.thirdSdkErrorMessage
                }
                callbackSplashFailed(adProviderType, alias, listener, errCode, errMsg)
            }

            override fun onAdSkip() {
                GroMoreProvider.Splash.customSkipView = null
                callbackSplashDismiss(adProviderType, listener)
            }

            override fun onAdDismiss() {
                CsjProvider.Splash.customSkipView = null
                callbackSplashDismiss(adProviderType, listener)
            }
        })

        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        val adSlot = AdSlot.Builder()
            .setImageAdSize(
                GroMoreProvider.Splash.imageAcceptedSizeWidth,
                GroMoreProvider.Splash.imageAcceptedSizeHeight
            ) // 既适用于原生类型，也适用于模版类型。
            .setSplashButtonType(TTAdConstant.SPLASH_BUTTON_TYPE_FULL_SCREEN)
            .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP)
            .build()
        //自定义兜底方案 选择使用
        var ttNetworkRequestInfo: TTNetworkRequestInfo? = null
        //穿山甲兜底，参数分别是appId和adn代码位。注意第二个参数是代码位，而不是广告位。
        //穿山甲兜底，参数分别是appId和adn代码位。注意第二个参数是代码位，而不是广告位。
//        ttNetworkRequestInfo = PangleNetworkRequestInfo("appId", "代码位") //gdt兜底
//        ttNetworkRequestInfo = new GdtNetworkRequestInfo("1101152570", "8863364436303842593"); //ks兜底
//        ttNetworkRequestInfo = new KsNetworkRequestInfo("90009", "4000000042"); //百度兜底
//        ttNetworkRequestInfo = new BaiduNetworkRequestInfo("e866cfb0", "2058622");
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mSplashAd?.loadAd(adSlot, ttNetworkRequestInfo, object : TTSplashAdLoadCallback {
            override fun onSplashAdLoadFail(adError: AdError) {
                var errCode = 0
                var errMsg = ""
                if (adError != null) {
                    errCode = adError.thirdSdkErrorCode
                    errMsg = adError.thirdSdkErrorMessage
                }
                callbackSplashFailed(adProviderType, alias, listener, errCode, errMsg)
            }

            override fun onSplashAdLoadSuccess() {
                if (mSplashAd != null) {
                    callbackSplashLoaded(adProviderType, alias, listener)
                    isBaiduSplashAd =
                        mSplashAd?.getAdNetworkPlatformId() == NetworkPlatformConst.SDK_NAME_BAIDU
                    showSplashAd(container)
                } else {
                    callbackSplashFailed(
                        adProviderType,
                        alias,
                        listener,
                        null,
                        "请求成功，但是返回的广告为null"
                    )
                }
            }

            override fun onAdLoadTimeout() {
                callbackSplashFailed(adProviderType, alias, listener, null, "请求超时了")
            }
        }, GroMoreProvider.Splash.maxFetchDelay)
    }
}