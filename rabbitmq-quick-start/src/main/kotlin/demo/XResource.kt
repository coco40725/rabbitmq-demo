package demo

import XProducer
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.eclipse.microprofile.reactive.messaging.Message


/**
@author Yu-Jing
@create 2024-08-06-上午 09:46
 */
@Path("/x")
class XResource @Inject constructor(
    private val xProducer: XProducer
) {

    @GET
    @Path("/generate")
    fun generate() {
        xProducer.send()?.subscribe()?.with { message: Message<Order> -> println(message) }
    }
    @GET
    @Path("/send")
    fun send() {
        xProducer.sendByEmitter()
    }
}