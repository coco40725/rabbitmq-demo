package demo

import io.smallrye.mutiny.Multi
import io.vertx.core.json.JsonObject
import jakarta.inject.Inject
import jakarta.ws.rs.Produces
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.reactive.messaging.Channel

/**
@author Yu-Jing
@create 2024-08-06-上午 11:15
 */
@Path("/y")
class YResource @Inject constructor(
    @Channel("collect-input") private val resultStream: Multi<JsonObject>,
){
    private val orderMap = mutableMapOf<String, Order>()

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    fun consume(): Multi<Order?>? {
        return resultStream
            .map { it.mapTo(Order::class.java) }
            .filter { order ->
                val id = order.id
                val mapOrder = orderMap[id]
                if (mapOrder == null) {
                    orderMap[id] = order
                    return@filter false
                } else {
                    val aIsFinished = order.isAFinished || mapOrder.isAFinished
                    val bIsFinished = order.isBFinished || mapOrder.isBFinished
                    return@filter aIsFinished && bIsFinished
                }
            }.map { Order(it.id, true, true) }
    }
}