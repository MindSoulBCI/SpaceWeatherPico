# 天气素材来源与接入状态

提供方：成都市脑云矩阵科技有限公司 · MindSoul。

## Piece-of-Dynamic-Weather
https://github.com/HangyBoi/Piece-of-Dynamic-Weather
固定版本：bb7bce70b89684627ab23b0b731232bc1276af0a

Assets/Models 只有 tornadoMesh.blend 及其备份，不是每种天气都有独立模型。
已转换原始 TornadoBody_02、TornadoRing_02、TornadoRoot_02 三个网格为 weather/tornado.glb，加载到 WeatherScene；调整材质以适配 PICO，增加旋转、强度缩放和环绕粒子。依据用户参考图新增 tornado_ribbons.glb 分层风带与顶部涡环、tornado_wind.glb 横向风丝；它们是本工程的适配几何，不是上游提供的网格。
Assets/Visual Effects/Materials&Textures/rain.png 原图已纳入资源。雨幕试验网格 rain_layer.glb 暂不启用，避免不兼容材质形成黑色矩形；运行时雨线使用原生粒子几何。
Unity VFX Graph（雨、闪电、风、尘埃）与 C# 组件并未直接运行。PICO 版本以原生实体动画实现风、雨、雪、闪电，不能称为原版特效的完整移植。
仓库根目录未发现覆盖整个项目的明确许可证；此包用于用户指定的本地集成评估，公开发布前需确认作者对相关素材的授权。未引入无关第三方插件。

## DMSkybox
https://github.com/DMeville/DMSkybox
固定版本：eb506261f3fc3f83373cc10cb08b37fa50d3c708

Assets/DMSkybox/SkysphereMesh.obj 转换为 source_skysphere.glb。为保留 PICO 主空间房间，完整天空球默认不显示。
Textures/sf_noise_clouds_01.png 原图复制为 cloud_noise.png，并嵌入 cloud_layer.glb 的天空云幕。
Unity/ASE shader 未直接移植；原网格与纹理保留，渲染使用 glTF/PICO 材质。MIT 许可证见 Licenses/DMSkybox-MIT.md。

## 图标
mindsoul_weather_icon.jpg 使用用户提供的原图，作为 launcher icon / roundIcon。未更改图片内容。

## CloudSkybox（当前阴天与日常云层）
https://github.com/keijiro/CloudSkybox
固定版本：d64d11b16e1ca83895b0bf3b84a3ecae970e6802
MIT，见 Licenses/CloudSkybox-MIT.md。保留原始 CloudSkybox.shader 以及从 Assets/Test/Perlin.asset、Worley.asset 提取的原始 Alpha8 三维噪声数据（third_party/CloudSkybox）。tools/bake_cloudsky.py 适配其双噪声 SampleNoise、垂直密度衰减和 Beer 透射公式，生成日常/阴天两套 RGBA 云层与 GLB 天空曲面。运行时缓慢旋转。此为离线采样适配，不是 Unity shader 在 PICO 上实时运行；光照、时间变化与原版有差异。旧椭圆云团和 DMSkybox 云幕不再显示，保留上游资源与归属说明。

## SpektrLightning
https://github.com/keijiro/SpektrLightning
固定版本：779d7a27395a66b79a5f9044a2bae0d55cf11359。代码文件自带 MIT 授权，保留原 shader 与 SimplexNoise2D.cginc 的作者声明。tools/build_spektr_bolts.py 将原 shader 的双频 simplex 位移公式适配为四个时间采样的线束 GLB，PICO 运行时按闪电时序切换，附加半透明光晕。原 Unity HDR Bloom 没有移植，不能称为原效果完整复现。

## StylizedTrees
https://github.com/mozankatip/StylizedTrees
固定版本：58b417fdb348e1f5350901efa380a539dc8c2b3f。MIT，见 Licenses/StylizedTrees-MIT.md。采用原 Assets/Models/Tree_01_LOD1.fbx 的树干与六组树冠网格，归一化到 2.6 米，按原树冠中心拆分为 GLB；运行时整体放大 1.3 倍。tools/convert_stylized_tree.py 记录转换。
保留 Leaves.shadergraph 作为风动画来源；其 Wind Direction / Speed / Strength 控制适配为 WindMotion 的整体摇摆和六组树冠错相运动。没有运行 Unity 顶点风 shader、视角叶片法线或原始叶片遮罩；材质采用原生绿色分层简化。晴天微动、阴天轻摆、风和雷暴明显摆动、龙卷风最强。根部固定，暂停冻结，强度为零静止。

## lunar-java（日历与节日）
https://github.com/6tail/lunar-java
固定版本：0194eb4574f33ab056fe7cac62a9d8bf24272478。MIT，见 Licenses/lunar-java-MIT.md。原始 Java 源码保存在 third_party/lunar-java，通过 app 的 sourceSets 编译。使用公历、农历、主要节日和节气 API；没有使用黄历、占卜等内容，也没有把节日名称解释为法定放假安排。今日日期按 Asia/Shanghai 计算；月历使用周一为首日。
