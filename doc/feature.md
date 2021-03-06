## 特色功能

### 1. 主流SDK随意搭配组合

实际项目中，往往会接入多家广告SDK，以实现收益最大化的目的。

``TogetherAd``帮助开发者将其集成在一起，开发者可以任选组合进行搭配使用

### 2. 支持权重配置

因为各个平台分发广告的量以及价格都是不一样的，所以需要动态配置请求的比例。

例如：有三家广告平台 A、B、C，你认为 A 的单价和收入都是最高的，想要多展示一点。

那么可以配置他们的权重：A：B：C = 2：1：1

``TogetherAd`` 会根据配置的权重随机请求一家平台的广告，如果请求广告的总数是 40000 次。

那么每家平台请求的次数就会趋近于：A: 20000, B:10000, C:10000

### 3. 支持失败切换

如果某个平台的广告请求失败或没有量，会自动在其他广告中随机出一种再次请求，这样可以尽可能多的展示广告，使收益最大化

![](../img/TogetherAd.png)

![](../img/TogetherAd_Feature.png)