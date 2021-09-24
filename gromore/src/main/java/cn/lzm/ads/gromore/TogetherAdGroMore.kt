package cn.lzm.ads.gromore

import android.content.Context
import android.provider.Settings
import cn.lzm.ads.gromore.provider.GroMoreProvider
import com.bytedance.msdk.api.*
import com.ifmvo.togetherad.core.TogetherAd
import com.ifmvo.togetherad.core.entity.AdProviderEntity
import org.jetbrains.annotations.NotNull
import java.lang.Exception
import java.util.*

/**
 * 初始化穿山甲
 *
 * Created by Matthew Chen on 2020-04-17.
 */
object TogetherAdGroMore {

    // 必须设置，穿山甲的广告位ID
    var idMapGroMore = mutableMapOf<String, String>()

    // 可选参数，需在初始化之前，设置是否使用texture播放视频：true使用、false不使用。默认为false不使用（使用的是surface）
    var useTextureView: Boolean = true

    // 可选参数，需在初始化之前，设置落地页主题，默认为TTAdConstant#TITLE_BAR_THEME_LIGHT
    var titleBarTheme: Int = TTAdConstant.TITLE_BAR_THEME_DARK

    // 可选参数，需在初始化之前，设置是否允许SDK弹出通知：true允许、false禁止。默认为true允许
    var allowShowNotify: Boolean = true

    // 可选参数，需在初始化之前，是否打开debug调试信息输出：true打开、false关闭。默认false关闭
    var debug: Boolean = false

    // 可选参数，需在初始化之前，设置是否为计费用户：true计费用户、false非计费用户。默认为false非计费用户。须征得用户同意才可传入该参数
    var isPaid: Boolean = false

    /**
     * 简单初始化
     */
    fun init(@NotNull context: Context, @NotNull adProviderType: String, @NotNull gromoreAdAppId: String, @NotNull appName: String) {
        init(context, adProviderType, gromoreAdAppId, appName, null, null)
    }

    /**
     * 自定义Provider初始化
     */
    fun init(@NotNull context: Context, @NotNull adProviderType: String, @NotNull gromoreAdAppId: String, @NotNull appName: String, providerClassPath: String? = null) {
        init(context, adProviderType, gromoreAdAppId, appName, null, providerClassPath)
    }

    /**
     * 广告位ID 初始化
     */
    fun init(@NotNull context: Context, @NotNull adProviderType: String, @NotNull gromoreAdAppId: String, @NotNull appName: String, gromoreIdMap: Map<String, String>? = null) {
        init(context, adProviderType, gromoreAdAppId, appName, gromoreIdMap, null)
    }

    /**
     * 自定义Provider + 广告位ID 一起初始化
     */
    fun init(@NotNull context: Context, @NotNull adProviderType: String, @NotNull gromoreAdAppId: String, @NotNull appName: String, gromoreIdMap: Map<String, String>? = null, providerClassPath: String?) {
        TogetherAd.addProvider(AdProviderEntity(adProviderType, if (providerClassPath?.isEmpty() != false) GroMoreProvider::class.java.name else providerClassPath))
        gromoreIdMap?.let { idMapGroMore.putAll(it) }
        doInit(context,gromoreAdAppId,appName)
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private fun doInit(context: Context, @NotNull gromoreAdAppId: String, @NotNull appName: String) {
        if (!sInit) {
            TTMediationAdSdk.initialize(
                context,
                buildConfig(context, gromoreAdAppId, appName)
            )
            sInit = true
        }
    }

    private fun buildConfig(context: Context, @NotNull gromoreAdAppId: String, @NotNull appName: String): TTAdConfig? {
        val userInfo = UserInfoForSegment()
        userInfo.userId = UUID.randomUUID().toString()
        userInfo.gender = UserInfoForSegment.GENDER_MALE
        userInfo.channel = "msdk channel"
        userInfo.subChannel = "msdk sub channel"
        userInfo.age = Random().nextInt(46) + 16
        userInfo.userValueGroup = "msdk demo user value group"
        val customInfos: MutableMap<String, String> = HashMap()
        customInfos["aaaa"] = "test111"
        customInfos["bbbb"] = "test222"
        userInfo.customInfos = customInfos

        return TTAdConfig.Builder()
            .appId(gromoreAdAppId) //必填 ，不能为空
            .appName(appName) //必填，不能为空
            .openAdnTest(false) //开启第三方ADN测试时需要设置为true，会每次重新拉去最新配置，release 包情况下必须关闭.默认false
            .isPanglePaid(isPaid) //是否为费用户
            .setPublisherDid(getAndroidId(context)) //用户自定义device_id
            .openDebugLog(debug) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
            .usePangleTextureView(useTextureView) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
            .setPangleTitleBarTheme(titleBarTheme)
            .allowPangleShowNotify(allowShowNotify) //是否允许sdk展示通知栏提示
            .allowPangleShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
            .setPangleDirectDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_MOBILE) //允许直接下载的网络状态集合
            .needPangleClearTaskReset() //特殊机型过滤，部分机型出现包解析失败问题（大部分是三星）。参数取android.os.Build.MODEL
            .setUserInfoForSegment(userInfo) // 设置流量分组的信息
            .setPrivacyConfig(privacyConfig)
            .build()
    }

    fun getAndroidId(context: Context): String? {
        var androidId: String? = null
        try {
            androidId =
                Settings.System.getString(context.contentResolver, Settings.System.ANDROID_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return androidId
    }

    private var sInit = false

    var privacyConfig: TTPrivacyConfig = object : TTPrivacyConfig() {
        override fun isLimitPersonalAds(): Boolean {
            return false
        }

        override fun isCanUseLocation(): Boolean {
            return false
        }

        override fun isCanUsePhoneState(): Boolean {
            return false
        }
    }
}