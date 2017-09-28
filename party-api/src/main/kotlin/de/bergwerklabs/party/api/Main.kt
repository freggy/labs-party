package de.bergwerklabs.party.api

import java.util.*

/**
 * Created by Yannic Rieger on 08.09.2017.
 *
 * de.bergwerklabs.party.api.Main class of the API.
 *
 * @author Yannic Rieger
 */
class Main {
    
    companion object {
        
        @JvmStatic
        fun main(args: Array<String>) {
            val party = PartyApi.createParty(UUID.randomUUID(), listOf(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID(), UUID.randomUUID()))
            
        }
    }
}