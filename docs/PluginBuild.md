# 构建自己的插件

###### **_*注意，构建自己的插件需要您具有一定的Kotlin编程水平_**

## 在Idea中构建

### 1. 在idea中新建一个空工程

### 2. 在`build.gradle.kts`中加入下面的内容

```kotlin
tasks.jar {
    manifest {
        attributes["CatPluginId"] = "填入插件的ID"
    }
}
```

如果使用其他方式打包插件，也请在Jar包`MANIFEST.MF`文件内添加`CatPluginId: my.plugin.id`字段
`

### 3. 添加你的插件配置（可选）

在插件`resources`文件夹中添加`config.yml`文件
默认情况下你可以配置基本的插件信息和[依赖信息](./docs/Dependency.md)

下面是一个基本的插件信息

```yaml
plugin:
  name: "这是插件名称"
  description: "这是插件描述"
  author: "这是插件作者"
  mainClass: "主类路径"
```

### 4. 编写插件启动类（可选）

新建一个类（可以是`object`类），并且实现`org.sereinfish.cat.frame.plugin.Plugin`接口，然后在配置文件中配置`mainClass`指向该类，完成插件启动类配置

**推荐使用**`object`类构建插件启动类，在插件启动时会在该类内部放入一些插件信息，方便使用

### 5. 构建插件并且使用框架加载

如未使用其他打包插件，则可直接使用idea默认`build`方式构建Jar包，构建完成后将Jar包移动到框架根目录下的`Plugin`文件夹内，运行框架即可。如未找到`Plugin`文件夹，请先运行一遍框架完成初始化