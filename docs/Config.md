# 配置文件

#### 框架配置文件：

`./config.yml`

#### 插件加载配置文件

按以下路径进行加载，优先级由低到高

- 由插件配置指定的插件主类 `PluginMain.class`
- `resources/config.yml`
- `./data/config/{PluginId}/config.yml`

## 支持解析格式

- json
- yml

## 接口

### 获取配置

- #### 获取单个属性
  - get()
  - getOrElse<T>()
  - getOrNull<T>()
  - by config.value<T>()
  - by config.valueOrLazy<T>()
- #### 获取实体类
  - getClass<T>()
  - getClassOrNull<T>()
  - getClassOrElse()
  - by config.clazz<T>()
  - by config.classOrLaze<T>()

### 写入配置
  - save()

## 引用路径

```yaml
abc:
  a: 123
  b: "b"
```
使用例子：
```kotlin
data class Data(
    val a: Int,
    val b: String
)

fun func(config: Config){
    val any: Any? = config.get("abc")
    val a: Int = config.getOrElse("abc.a")
    
    val data = config.getClass<Data>("abc")
  
    config.save("abc", data)
}
```