import demo.Order
import io.smallrye.mutiny.Multi
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.reactive.messaging.*
import org.jboss.logging.Logger
import java.time.Duration
import java.util.*


@ApplicationScoped
class XProducer @Inject constructor(
    @Channel("x") private val emitter: Emitter<Order>
){

    /**
     *  開啟數據流 定期往裡面塞 item --> item 會不斷地被 exchange 處理
     */
    @Outgoing("x")
    fun send(): Multi<Message<Order>>? {
        val metadata = Metadata.of(OutgoingRabbitMQMetadata.Builder().withRoutingKey("all").build())
        LOGGER.infof("send")

        return Multi.createFrom().ticks().every(Duration.ofMillis(5000))
            .map{ _ ->
                val uuid = UUID.randomUUID()
                val order = Order(uuid.toString(), false, false)
                Message.of(order, metadata)
            }
    }


    /**
     * 主動性的發一次性事件過去exchange
     *
     */
    fun sendByEmitter(){
        val uuid = UUID.randomUUID()
        val order = Order(uuid.toString(), false, false)
        val metadata = Metadata.of(OutgoingRabbitMQMetadata.Builder().withRoutingKey("all").build())
        LOGGER.infof("sendByEmitter")
        emitter.send(Message.of(order, metadata))
    }


    companion object {
        private val LOGGER = Logger.getLogger(XProducer::class.java)
    }
}