package com.iou.service

import net.corda.client.rpc.CordaRPCClient
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.utilities.NetworkHostAndPort
import org.apache.activemq.artemis.api.core.ActiveMQNotConnectedException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ServicesApplication {
    @Value("\${corda.liberty.host}")
    lateinit var cordaLibertyHost:String

    @Value("\${corda.liberty.user}")
    lateinit var cordaLibertyUser:String

    @Value("\${corda.liberty.password}")
    lateinit var cordaLibertyPassword:String

    @Value("\${corda.swissre.host}")
    lateinit var cordaSwissreHost:String

    @Value("\${corda.swissre.user}")
    lateinit var cordaSwissreUser:String

    @Value("\${corda.swissre.password}")
    lateinit var cordaSwissrePassword:String

//    @Bean
//    fun swissreRpcClient(): CordaRPCOps {
//        log.info("Connecting to Corda on $cordaSwissreHost using username $cordaSwissreUser")
//        var maxRetries = 100
//        do {
//            try {
//                return CordaRPCClient(NetworkHostAndPort.parse(cordaSwissreHost)).start(cordaSwissreUser, cordaSwissrePassword).proxy
//            } catch (ex: ActiveMQNotConnectedException) {
//                if (maxRetries-- > 0) {
//                    Thread.sleep(1000)
//                } else {
//                    throw ex
//                }
//            }
//        } while (true)
//    }

    @Bean
    fun libertyRpcClient(): CordaRPCOps {
        log.info("Connecting to Corda on $cordaLibertyHost using username $cordaLibertyUser")
        try {
            return CordaRPCClient(NetworkHostAndPort.parse(cordaLibertyHost)).start(cordaLibertyUser, cordaLibertyPassword).proxy
        } catch (ex: ActiveMQNotConnectedException) {
            throw ex
        }
    }

//    @Bean
//    fun objectMapper(@Autowired cordaRPCOps: CordaRPCOps): ObjectMapper {
//        val mapper = JacksonSupport.createDefaultMapper(cordaRPCOps)
//        registerFinanceJSONMappers(mapper)
//        return mapper
//    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(ServicesApplication::class.java, *args)
        }
    }
}
