package cn.lzm.ads.mintegral.provider

import android.app.Activity
import com.ifmvo.togetherad.core.listener.RewardListener

abstract class MintegralProviderReward : MintegralProviderFullVideo() {

    override fun requestRewardAd(
        activity: Activity,
        adProviderType: String,
        alias: String,
        listener: RewardListener
    ) {
    }

    override fun showRewardAd(activity: Activity): Boolean {
        return true
    }
}