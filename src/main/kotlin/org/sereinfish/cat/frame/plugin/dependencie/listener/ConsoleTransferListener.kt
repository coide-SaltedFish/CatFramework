package org.sereinfish.cat.frame.plugin.dependencie.listener

import org.eclipse.aether.transfer.AbstractTransferListener
import org.eclipse.aether.transfer.TransferEvent
import org.sereinfish.cat.frame.utils.logger


class ConsoleTransferListener: AbstractTransferListener() {
    private val logger = logger()

    override fun transferStarted(event: TransferEvent) {
        logger.info("开始下载: " + event.resource.repositoryUrl + event.resource.resourceName)
    }

    override fun transferProgressed(event: TransferEvent) {
        // 打印下载进度
        val totalLength = event.resource.contentLength
        if (totalLength >= 0) {
            val percentage = (100 * event.transferredBytes / totalLength).toInt()
            logger.debug("${event.resource.resourceName} > 下载进度: $percentage%")
        }
    }

    override fun transferSucceeded(event: TransferEvent) {
        logger.info("下载完成: " + event.resource.repositoryUrl + event.resource.resourceName)
    }

    override fun transferFailed(event: TransferEvent) {
        logger.info("下载失败: " + event.resource.repositoryUrl + event.resource.resourceName)
    }
}