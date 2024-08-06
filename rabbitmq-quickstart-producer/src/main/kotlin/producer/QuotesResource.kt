package producer

import io.smallrye.mutiny.Multi
import io.smallrye.reactive.messaging.OutgoingMessageMetadata
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata
import io.vertx.core.json.JsonObject
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import model.Quote
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Message
import org.eclipse.microprofile.reactive.messaging.Metadata
import java.util.*


/**
@author Yu-Jing
@create 2024-08-05-下午 04:50
 */

@Path("/quotes")
class QuotesResource @Inject constructor(
    @Channel("order-channel")
    var quoteRequestEmitter: Emitter<Quote>,

    @Channel("quotes")
    var quotes: Multi<Quote>,
) {

    private val rotingKey = listOf("key", "key1")

    /**
     * Endpoint retrieving the "quotes" queue and sending the items to a server sent event.
     */
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    fun stream(): Multi<Quote> {
        return quotes
    }

    /**
     * Endpoint to generate a new quote request id and send it to "quote-requests" RabbitMQ exchange using the emitter.
     */
    @POST
    @Path("/request")
    @Produces(MediaType.TEXT_PLAIN)
    fun createRequest(): String {
        val uuid = UUID.randomUUID()

        // 產生 隨機  routing key
        val key = "all"

        val metaData = OutgoingRabbitMQMetadata.Builder()
            .withRoutingKey(key)
            .build()

        val name = "${uuid}-${key}"
        val quote = Quote(name, 0)
        val message = Message.of(quote, Metadata.of(metaData))
        quoteRequestEmitter.send(message)
        return name
    }
}