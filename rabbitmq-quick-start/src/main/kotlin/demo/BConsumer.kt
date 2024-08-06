package demo

import io.smallrye.reactive.messaging.annotations.Blocking
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata
import io.vertx.core.json.JsonObject
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.reactive.messaging.*
import org.jboss.logging.Logger
import java.util.concurrent.CompletionStage

/**
@author Yu-Jing
@create 2024-08-06-上午 09:51
 */
@ApplicationScoped
class BConsumer @Inject constructor(
    @Channel("collect-out") private val emitter: Emitter<Order>
) {

    /**
     * 不能寫 Message<Order> ! 他沒辦法自動 deserialization
     * The rabbitmq connector doesn't handle object mapping deserialization.
     * A bean sent to the broker is serialized to Json automatically and the content_type is set to application/json.
     * When that message is received, looking at its content_type it is deserialized as io.vertx.core.json.JsonObject
     * https://github.com/quarkusio/quarkus/issues/32811
     */
    @Incoming("b")
    @Blocking("rm-pool")
    fun consume(message: Message<JsonObject>): CompletionStage<Void>? {
        LOGGER.infof("B start consume: %s", message)
        val obj = message.payload
        // 手動 deserialization
        val order = obj.mapTo(Order::class.java)
        val newOrder = Order(
            order.id,
            order.isAFinished,
            true
        )
        val metadata = Metadata.of(OutgoingRabbitMQMetadata.Builder().withRoutingKey("collect").build())
        val newMessage = Message.of(newOrder, metadata)
        try {
            Thread.sleep(5000)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
        LOGGER.infof("B finish")
        emitter.send(newMessage)
        return message.ack()
    }

    companion object {
        private val LOGGER = Logger.getLogger(AConsumer::class.java)
    }
}