package awaitzhang.julyfire

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan

@SpringBootApplication
@ServletComponentScan // 启用Druid的监控
@MapperScan("awaitzhang.julyfire.mapper")
class JulyFireApplication

fun main(args: Array<String>) {
	runApplication<JulyFireApplication>(*args)
}
