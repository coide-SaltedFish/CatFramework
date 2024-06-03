package org.sereinfish.cat.frame.plugin.dependencie

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.graph.DependencyNode
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.Proxy
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.util.artifact.JavaScopes
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator
import org.eclipse.aether.util.repository.DefaultProxySelector
import org.sereinfish.cat.frame.CatFrameConfig
import org.sereinfish.cat.frame.config.Config
import org.sereinfish.cat.frame.config.getClassOrElse
import org.sereinfish.cat.frame.context.getOrNull
import org.sereinfish.cat.frame.plugin.dependencie.listener.ConsoleRepositoryListener
import org.sereinfish.cat.frame.plugin.dependencie.listener.ConsoleTransferListener
import org.sereinfish.cat.frame.utils.logger
import org.sereinfish.cat.frame.utils.nonNull
import java.io.File


object DependenciesUtils {
    private val logger = logger()

    // 加载代理
    private val proxyHost = CatFrameConfig.getOrNull<String>("dependencies.host")
    private val proxyPort = CatFrameConfig.getOrNull<Int>("dependencies.port")
    private val proxy = if (proxyHost.nonNull() && proxyPort.nonNull()){
        Proxy(Proxy.TYPE_HTTP, proxyHost, proxyPort!!)
    }else null

    private val repositorySystem: RepositorySystem = MavenRepositorySystemUtils.newServiceLocator().apply {
        addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
        addService(TransporterFactory::class.java, FileTransporterFactory::class.java)
        addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
    }.getService(RepositorySystem::class.java)

    private val session = MavenRepositorySystemUtils.newSession().apply {
        localRepositoryManager = repositorySystem.newLocalRepositoryManager(this, LocalRepository(CatFrameConfig.libsPath))

        // 设置代理
        proxy?.let {
            logger.info("Proxy $it")
            proxySelector = DefaultProxySelector().add(it, null)
        }

        transferListener = ConsoleTransferListener()
        repositoryListener = ConsoleRepositoryListener()
    }

    fun loadDependencies(config: Config): List<File> {
        // 仓库地址列表
        val repositories: List<String> = config.getClassOrElse<MutableList<String>>("dependencies.repositories"){
            mutableListOf()
        }.apply {
            if (!contains("https://repo.maven.apache.org/maven2/"))
                add("https://repo.maven.apache.org/maven2/")
        }
        // 依赖地址列表
        val implementations: List<String> = config.getClassOrElse("dependencies.implementations") { listOf() }
        val centrals = repositories.map { RemoteRepository.Builder("central", "default", it).build() }

        val files = ArrayList<File>()
        // 尝试解析下载
        implementations.forEach { implementation ->
            val artifact: Artifact = DefaultArtifact(implementation)
            val dependency = Dependency(artifact, JavaScopes.COMPILE)
            val collectRequest = CollectRequest(dependency, centrals)
            val node: DependencyNode = repositorySystem.collectDependencies(session, collectRequest).root
            val dependencyRequest = DependencyRequest(node, null)
            repositorySystem.resolveDependencies(session, dependencyRequest)
            val nlg = PreorderNodeListGenerator()
            node.accept(nlg)

            files.addAll(nlg.getArtifacts(false).map { it.file })
        }

        return files
    }

    /**
     * 获取插件依赖
     */
    fun getPluginDependencies(config: Config): List<DependencyPluginInfo> {
        return config.getOrNull<List<Any>>("dependencies.plugins")?.let {
            it.map {
                when(it){
                    is String -> DependencyPluginInfo(it)
                    is Map<*, *> -> {
                        var optional = false
                        var id = ""
                        it.forEach { key, value ->
                            when(key) {
                                "optional" -> optional = value as Boolean
                                "id" -> id = value as String
                            }
                        }
                        if (id.isEmpty())
                            error("依赖的插件id不能为空")
                        DependencyPluginInfo(id, optional)
                    }

                    else -> {
                        error("未知的配置类型：${it::class.java}")
                    }
                }
            }
        } ?: listOf()
    }
}