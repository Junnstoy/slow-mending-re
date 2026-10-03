# 26.3 适配验证

验证日期：2026-10-03。

| 项目 | 结果 |
| --- | --- |
| Java | Temurin 25.0.4.1 |
| 服务端 | Paper 26.3 build 143，提交 ff3655a |
| Paper API | 26.3.build.143-beta |
| Maven | 3.9.9 |
| 单元测试 | 17 项通过，0 失败 / 0 错误 |
| 隔离服务端回归 | 46 项断言通过 |
| 插件启用 / 禁用 | 成功，无插件异常 |

## 测试覆盖

- 次数合法范围、特殊值、负数扣减和溢出。
- 新物品及已有自定义 Lore 的物品首次只扣一次。
- 旧 Lore 迁移、旧前缀、PDC 优先级、修改显示前缀及物品字节序列化。
- 原有丰富 Lore 保留，破损名称及补充次数后的名称恢复。
- 事件预先取消、零修补量、全局开关、无限次数与禁止修补。
- 默认按尝试扣次、可选成功扣次、显式 bypass 权限。
- 空手右键、副手忽略、DENY 交互、不合法目标、卡片只扣一张。
- 新卡不依赖 Lore、完整旧卡兼容、残缺卡拒绝、PDC 类型错误。
- 发卡堆叠拆分、保留主手、满背包整次拒绝。
- 无效配置拒绝且保留当前设置，拓展卡功能可重载开关且不重复注册。

服务端测试使用真实 Paper 的物品、PDC、序列化、背包和事件分发实现；玩家及经验球通过接口测试替身提供。它验证插件在真实 26.3 API 下的加载与逻辑，不包含真实客户端拾取经验球、长期多人负载或其他插件组合测试。

## 单元测试与构建

```sh
mvn --batch-mode clean install
mvn --batch-mode -f integration-tests/pom.xml package
```

## 重现隔离服务端测试

`integration-tests` 是开发测试插件，会在执行后自动停止服务端，仅用于新建的临时测试目录。正式服只安装主插件 JAR。

1. 在新的测试目录放入官方 Paper 26.3 build 143 的服务端 JAR。
2. 将 `target/slow-mending-re-2.2.0.jar` 和 `integration-tests/target/slow-mending-server-tests-2.2.0.jar` 放入该目录的 `plugins/`。
3. 按服务端要求设置 `eula.txt`。`server.properties` 设置 `server-ip=127.0.0.1`、未占用的端口、`online-mode=false`、`view-distance=2`、`simulation-distance=2`。
4. Java 25 执行 `java -Xms256M -Xmx1024M -Dterminal.jline=false -jar paper-26.3-143.jar --nogui`。
5. 检查 `slow-mending-test-result.txt` 为 `PASS 46 assertions`，日志含 `SERVER TESTS PASSED`。

隔离环境启动时 Mojang 服务发现可能因网络不可达产生警告；这与插件逻辑测试无关。GitHub Actions 执行单元测试、构建主插件及编译服务端测试插件；不自动启动服务端测试。
