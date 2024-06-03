package org.sereinfish.cat.frame.plugin.dependencie.listener

import org.eclipse.aether.AbstractRepositoryListener
import org.eclipse.aether.RepositoryEvent
import org.sereinfish.cat.frame.utils.logger


class ConsoleRepositoryListener: AbstractRepositoryListener() {
    private val logger = logger()

    override fun artifactDeployed(event: RepositoryEvent) {
        logger.debug("已部署: " + event.artifact)
    }

    override fun artifactDeploying(event: RepositoryEvent) {
        logger.debug("正在部署: " + event.artifact)
    }

    override fun artifactResolved(event: RepositoryEvent) {
        logger.debug("已解析: " + event.artifact)
    }

    override fun artifactDownloading(event: RepositoryEvent) {
        logger.debug("正在下载: " + event.artifact)
    }

    override fun artifactDownloaded(event: RepositoryEvent) {
        logger.debug("已下载: " + event.artifact)
    }

    override fun artifactResolving(event: RepositoryEvent) {
        logger.debug("正在解析: " + event.artifact)
    }
}