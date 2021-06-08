package cn.lzm.ads.ks.povider

import android.app.Activity
import android.os.SystemClock
import android.view.ViewGroup
import cn.lzm.ads.ks.TogetherAdKs
import com.ifmvo.togetherad.core.listener.SplashListener
import com.ifmvo.togetherad.core.provider.BaseAdProvider
import com.kwad.sdk.api.KsAdSDK
import com.kwad.sdk.api.KsLoadManager
import com.kwad.sdk.api.KsScene
import com.kwad.sdk.api.KsSplashScreenAd

/**
 * 快看开屏广告实现类
 *
 */
abstract class KsProviderSplash : KsProviderFullVideo() {
    private var mListener: SplashListener? = null
    private var mAdProviderType: String? = null
    private var mAlias:String? = null

    private var splashAd: KsSplashScreenAd? = null
    private var mContainer: ViewGroup? = null

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
        var scene = KsScene.Builder(TogetherAdKs.idMapKs[alias]!!.toLong()).build()
        KsAdSDK.getLoadManager().loadSplashScreenAd(scene,
            object : KsLoadManager.SplashScreenAdListener {
                /**
                 * 广告请求错误回调
                 *
                 */
                override fun onError(code: Int, message: String?) {
                    splashAd = null
                    callbackSplashFailed(adProviderType, alias, listener, code, message)
                }

                /**
                 * 开屏广告填充个数
                 */
                override fun onRequestResult(adNumber: Int) {
                }

                /**
                 * 广告请求成功回调
                 *
                 */
                override fun onSplashScreenAdLoad(ksSplashScreenAd: KsSplashScreenAd?) {
                    if (ksSplashScreenAd == null) {
                        callbackSplashFailed(
                            adProviderType,
                            alias,
                            listener,
                            null,
                            "请求成功，但是返回的广告为null"
                        )
                        return
                    }
                    ksSplashScreenAd.getFragment(object :
                        KsSplashScreenAd.SplashScreenAdInteractionListener {
                        override fun onAdClicked() {
                            callbackSplashClicked(adProviderType, listener)
                        }

                        override fun onAdShowError(code: Int, message: String?) {
                            callbackSplashFailed(adProviderType, alias, listener, code, message)
                        }

                        override fun onAdShowEnd() {
                            callbackSplashDismiss(adProviderType, listener)
                        }

                        override fun onAdShowStart() {
                            callbackSplashExposure(adProviderType, listener)
                        }

                        override fun onSkippedAd() {
                            callbackSplashDismiss(adProviderType, listener)
                        }

                    })
                    splashAd = ksSplashScreenAd
                    callbackSplashLoaded(adProviderType, alias, listener)
                }
            })
    }

    override fun showSplashAd(container: ViewGroup): Boolean {
        if (splashAd == null) {
            return false
        }
        mContainer = container
        val view = splashAd?.getView(container.context,
            object : KsSplashScreenAd.SplashScreenAdInteractionListener {
                override fun onAdClicked() {
                    callbackSplashClicked(mAdProviderType!!, mListener!!)
                }

                override fun onAdShowError(code: Int, message: String?) {
                    callbackSplashFailed(mAdProviderType!!, mAlias!!, mListener!!, code, message)
                }

                override fun onAdShowEnd() {
                    callbackSplashDismiss(mAdProviderType!!, mListener!!)
                }

                override fun onAdShowStart() {
                    callbackSplashExposure(mAdProviderType!!, mListener!!)
                }

                override fun onSkippedAd() {
                    callbackSplashDismiss(mAdProviderType!!, mListener!!)
                }
            })
        container.removeAllViews()
        view?.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        container.addView(view)
        return true
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
        var scene = KsScene.Builder(TogetherAdKs.idMapKs[alias]!!.toLong()).build()
        KsAdSDK.getLoadManager().loadSplashScreenAd(scene,
            object : KsLoadManager.SplashScreenAdListener {
                /**
                 * 广告请求错误回调
                 *
                 */
                override fun onError(code: Int, message: String?) {
                    splashAd = null
                    callbackSplashFailed(adProviderType, alias, listener, code, message)
                }

                /**
                 * 开屏广告填充个数
                 */
                override fun onRequestResult(adNumber: Int) {
                }

                /**
                 * 广告请求成功回调
                 *
                 */
                override fun onSplashScreenAdLoad(ksSplashScreenAd: KsSplashScreenAd?) {
                    if (ksSplashScreenAd == null) {
                        callbackSplashFailed(
                            adProviderType,
                            alias,
                            listener,
                            null,
                            "请求成功，但是返回的广告为null"
                        )
                        return
                    }
                    splashAd = ksSplashScreenAd
                    val view = ksSplashScreenAd.getView(activity,
                        object : KsSplashScreenAd.SplashScreenAdInteractionListener {
                            override fun onAdClicked() {
                                callbackSplashClicked(adProviderType, listener)
                            }

                            override fun onAdShowError(code: Int, message: String?) {
                                callbackSplashFailed(adProviderType, alias, listener, code, message)
                            }

                            override fun onAdShowEnd() {
                                callbackSplashDismiss(adProviderType, listener)
                            }

                            override fun onAdShowStart() {
                                callbackSplashExposure(adProviderType, listener)
                            }

                            override fun onSkippedAd() {
                                callbackSplashDismiss(adProviderType, listener)
                            }
                        })
                    if (!activity.isFinishing) {
                        container.removeAllViews()
                        view.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        container.addView(view)
                    }
                    callbackSplashLoaded(adProviderType, alias, listener)
                }
            })

    }

}