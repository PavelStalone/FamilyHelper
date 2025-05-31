package rut.uvp.auth.config

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

internal class BearerTokenServerAuthenticationConverter : ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> =
        Mono.justOrEmpty(extractToken(exchange))
            .map { token -> UsernamePasswordAuthenticationToken(token, token) as Authentication }

    private fun extractToken(exchange: ServerWebExchange): String? {
        val header = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (header != null && header.startsWith("Bearer ", ignoreCase = true)) {
            return header.substring(7)
        }
        // fallback для GET /sse?token=…
        return exchange.request.queryParams.getFirst("token")
    }
}
