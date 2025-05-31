//package rut.uvp.auth.infrastructure
//
//import jakarta.servlet.FilterChain
//import jakarta.servlet.http.HttpServletRequest
//import jakarta.servlet.http.HttpServletResponse
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.security.core.authority.SimpleGrantedAuthority
//import org.springframework.security.core.context.SecurityContextHolder
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
//import org.springframework.stereotype.Component
//import org.springframework.web.filter.OncePerRequestFilter
//import rut.uvp.auth.infrastructure.repository.UserRepositoryJpa
//import rut.uvp.auth.util.JwtUtil
//import rut.uvp.auth.util.asDomain
//import kotlin.jvm.optionals.getOrNull
//
//@Component
//internal class JwtAuthenticationFilter(
//    private val jwtUtil: JwtUtil,
//    private val userRepository: UserRepositoryJpa,
//) : OncePerRequestFilter() {
//
//    override fun doFilterInternal(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        filterChain: FilterChain
//    ) {
//        runCatching {
//            val authHeader = request.getHeader("Authorization")
//            requireNotNull(authHeader)
//            require(authHeader.startsWith("Bearer "))
//
//            val token = authHeader.substring(7)
//            require(jwtUtil.validateToken(token))
//
//            val userId = jwtUtil.extractUserId(token)
//            requireNotNull(userId)
//            require(SecurityContextHolder.getContext().authentication == null)
//
//            val user = userRepository.findById(userId).getOrNull()
//            requireNotNull(user)
//
//            val auth = UsernamePasswordAuthenticationToken(
//                user.asDomain(),
//                null,
//                listOf(SimpleGrantedAuthority("ROLE_USER"))
//            )
//            auth.details = WebAuthenticationDetailsSource().buildDetails(request)
//            SecurityContextHolder.getContext().authentication = auth
//        }
//
//        filterChain.doFilter(request, response)
//    }
//}