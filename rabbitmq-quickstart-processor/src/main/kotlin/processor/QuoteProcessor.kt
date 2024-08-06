package processor

import io.smallrye.mutiny.Multi
import io.vertx.core.json.JsonObject
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import model.Quote
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Outgoing
import org.jetbrains.annotations.Blocking


/**
@author Yu-Jing
@create 2024-08-05-下午 05:05
 */

@ApplicationScoped
class QuoteProcessor @Inject constructor(
){

    @Incoming("order-channel") // get the item from  the "requests" channel
    @Outgoing("quotes")
    @Blocking
    fun process(json: JsonObject): Quote {
        val quote = json.mapTo(Quote::class.java)
        // simulate some hard-working task
        Thread.sleep(1000)

        // send result
        val random = (Math.random()*100).toInt()
        println("I complete the request: $quote")
        return Quote(quote.id, random)
    }
}