//package awaitzhang.common.config
//
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
//import org.springframework.security.config.http.SessionCreationPolicy
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.security.web.SecurityFilterChain
//import org.springframework.security.config.annotation.web.invoke
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
//
//@Configuration
//@EnableWebSecurity
//class SecurityConfig {
//    @Bean
//    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
//
//
//    @Bean
//    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
//        http {
//            authorizeHttpRequests {
//                authorize(anyRequest, permitAll) // 允许所有请求
//            }
//            csrf { disable() } // 禁用CSRF保护
//        }
//        return http.build()
//    }
//}