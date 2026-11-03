# MindSoul 空间天气 · PICO

成都市脑云矩阵科技有限公司 · MindSoul 提供。

保留 PICO 系统房间的 Mixed Stage，三个系统窗口联动，新增独立 32 通道脑机看板。八种天气：龙卷风、晴天、阴天、雨天、雪天、风、乌云、雷暴。默认演示阴天云层；场景窗口可切换天气、调整强度、暂停、开启环境音和雷电。

## 运行
需要兼容 Spatial SDK 6.1 的 PICO OS / 模拟器，Android API 35、ARM64。使用 Java 21 执行 `./gradlew assembleDebug testDebugUnitTest`。通过 PICO CLI 安装 app/build/outputs/apk/debug/app-debug.apk，启动 com.example.spatialweather.p10282026/.platform.LaunchActivity。

## 脑机接口
点击主窗口「脑机接口 · 32 通道看板」。目前提供明确标记的模拟波形；真实输入未接设备，不伪装设备在线。BrainLink 等桥接层可调用 BciSession.submit(channels, receivedAt)，输入恰好 32 个微伏 Float 和 Android SystemClock.elapsedRealtime() 接收时间；2 秒未收到新帧显示陈旧状态。disconnect() 清空连接数据。实际 BrainLink 协议、通道能力与采样率仍需根据硬件 SDK 接入；并不宣称 BrainLink 自带 32 通道。

四种情绪目前是手动自述体验选择，没有训练好的情绪解码模型，不用于判断抑郁症。可联动推荐天气，也可手动覆盖。缺少设备、模型时不会输出伪造的情绪概率。

## 素材和验收范围
详见 THIRD-PARTY-WEATHER.md 与 ASSET-MANIFEST.json，区分真实上游素材、原生效果重建和暂未启用资源。
已通过 Debug 构建和 25 项单元测试，并在 PICO 模拟器安装启动。截图用于视觉核验；尚未做头显真机帧率、长时间体验和全部天气逐项验收。

## 登录 / 注册
主窗口点击「登录 / 注册」打开独立 PICO 系统窗口，默认邮箱密码登录，可切换邮箱验证码登录和邮箱注册（验证码 + 设置密码）。账号 API 尚未配置，不会伪造注册、登录或邮件发送成功。关闭弹窗仍可体验天气。
在 res/values/strings.xml 配置 mindsoul_account_api 为 HTTPS 服务根地址；协议见 MINDSOUL_ACCOUNT_API.md。不落盘保存密码、验证码或 token，会话仅在进程内保留。

## 风感树与雷电
进入空间天气默认显示一棵 StylizedTrees 原模型树。切换天气即可观察摆动变化，龙卷风最强；强度和暂停同时控制树。任何天气下开启「雷电闪光」立即显示 SpektrLightning 算法适配的多股电弧与光晕，之后每 2 秒触发一次，关闭开关立即停止；默认关闭闪光。树的原始网格已使用，Unity Shader Graph、HDR Bloom 并未原样运行。

## 今日与日历
主窗口顶部展示今日日期、星期、农历、节日/节气与当前城市实时温度。日期跟随设备时钟并按北京时间跨日刷新；温度来自 Open-Meteo，加载失败不编造数值。点击「查看日历 / 节日」打开独立月历，可翻月、选择日期、回到今天。节日不代表法定放假或调休安排。算法来自 MIT 开源 lunar-java，代码和许可证随工程提供。
