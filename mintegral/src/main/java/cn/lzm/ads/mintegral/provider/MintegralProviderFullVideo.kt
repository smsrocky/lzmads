package cn.lzm.ads.mintegral.provider

import android.app.Activity
import com.ifmvo.togetherad.core.listener.FullVideoListener
import com.ifmvo.togetherad.core.provider.BaseAdProvider

/**
 * 快手全屏視頻類
 *
 */
abstract class MintegralProviderFullVideo :BaseAdProvider() {

    override fun requestFullVideoAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        listener: FullVideoListener
    ) {
    }

    override fun showFullVideoAd(activity: Activity): Boolean {return false}
}