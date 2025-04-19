package org.jwj.novelbookservice.manager.amqp;

import lombok.RequiredArgsConstructor;
import org.jwj.novelcommon.constants.AmqpConsts;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 该项目似乎没有实现消费者的逻辑
 * TODO:在search服务中补充消费者逻辑
 */
@Component
@RequiredArgsConstructor
public class AmqpMsgManager {
    public final AmqpTemplate amqpTemplate;
    @Value("${spring.amqp.enabled:false}")
    private boolean amqpEnabled;

    public void sendBookChangeMsg(Long bookId) {
        if (amqpEnabled) {
            sendAmqpMessage(amqpTemplate, AmqpConsts.BookChangeMq.EXCHANGE_NAME, null, bookId);
        }
    }

    private void sendAmqpMessage(AmqpTemplate amqpTemplate, String exchangeName, String routingKey,
                                 Object message) {
        // 如果在事务中则在事务执行完成后再发送，否则可以直接发送
        if(TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            amqpTemplate.convertAndSend(exchangeName, routingKey, message);
                        }
                    });
            return;
        }
        amqpTemplate.convertAndSend(exchangeName, routingKey, message);
    }
}
