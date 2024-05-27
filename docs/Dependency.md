## 依赖加载

**所有插件共用一个依赖类加载器**

- 解析[配置](Config.md)文件
    ```yml
    dependencies:
      repositories:
        - "https://repo.maven.apache.org/maven2/"
      implementations:
        - "com.squareup.okhttp3:okhttp:4.12.0"
      plugins:
        - "org.example.plugin"
    ```

- ### 一般依赖库
- 解析仓库链接，可为空，默认为 `https://repo.maven.apache.org/maven2/`
- 逐级解析所需依赖添加到依赖下载器
- 下载依赖
- 依赖添加到插件类加载器
- ### 插件依赖
- 解析插件依赖
- 检查对应插件是否存在
- 调整此插件加载顺序到依赖的插件之后
- 添加依赖插件路径到插件类加载器