package sse

import io.smallrye.mutiny.Multi
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import model.Quote
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

/**
@author Yu-Jing
@create 2024-08-05-下午 06:11
 */
@Path("/sse")
class SSEResource {

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    fun send(): Multi<Quote> {
        println("I get here!!!") // 只有初始化會執行一次這一段，之後都是直接監聽 return
        return Multi.createFrom().ticks().every(Duration.ofSeconds(5))
            .map { Quote("quote_${Date().time}",  (Math.random() * 100).toInt()) }
    }
}