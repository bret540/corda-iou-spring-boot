package com.iou.service

import com.example.api.SERVICE_NAMES
import com.example.flow.ExampleFlow.Initiator
import com.example.state.IOUState
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.utilities.contextLogger
import net.corda.core.utilities.getOrThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class Controller {
    companion object {
        private val logger = contextLogger()
    }

    @Autowired
    lateinit var rpcOps: CordaRPCOps

    @GetMapping("/peers")
    fun getPeers(): Map<String, List<CordaX500Name>> {
        val nodeInfo = rpcOps.networkMapSnapshot()
        val myLegalName: CordaX500Name = rpcOps.nodeInfo().legalIdentities.first().name
        return mapOf("peers" to nodeInfo
                .map { it.legalIdentities.first().name }
                //filter out myself, notary and eventual network map started by driver
                .filter { it.organisation !in (SERVICE_NAMES + myLegalName.organisation) })
    }

    @GetMapping("/networksnapshot")
    fun networkSnapshot() = rpcOps.networkMapSnapshot().toString()

    @GetMapping("/ious")
    fun getIOUs() = rpcOps.vaultQueryBy<IOUState>().states

    @PutMapping("/iou")
    fun createIOU(@RequestParam("iouValue") iouValue: Int, @RequestParam("partyName") partyName: String?): ResponseEntity<String> {
        if (iouValue <= 0 ) {
            return ResponseEntity.status(BAD_REQUEST).body("Query parameter 'iouValue' must be non-negative.\n")
        }
        if (partyName == null) {
            return ResponseEntity.status(BAD_REQUEST).body("Query parameter 'partyName' missing or has wrong format.\n")
        }
        val otherParty = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse(partyName)) ?: return ResponseEntity.status(BAD_REQUEST).body("Party named $partyName cannot be found.\n")

        return try {
            val flowHandle = rpcOps.startTrackedFlow(::Initiator, iouValue, otherParty)
            val subscribe = flowHandle.progress.subscribe()
            val signedTransaction = flowHandle.returnValue.getOrThrow()
            subscribe.unsubscribe()
            logger.debug(signedTransaction.toString())
            ResponseEntity.status(CREATED).body("Transaction id ${signedTransaction.id} committed to ledger.\n")

        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.status(BAD_REQUEST).body(ex.message!!)
        }
    }
}
