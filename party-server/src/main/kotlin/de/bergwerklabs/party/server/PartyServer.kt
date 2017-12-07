package de.bergwerklabs.party.server

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.AtlantisParty
import de.bergwerklabs.atlantis.api.party.packages.PartyChangeOwnerPacket
import de.bergwerklabs.atlantis.api.party.packages.PartyDisbandPacket
import de.bergwerklabs.atlantis.api.party.packages.PartySavePacket
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateRequestPacket
import de.bergwerklabs.atlantis.api.party.packages.info.PartyInfoRequestPacket
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyClientInviteRequestPacket
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyClientInviteResponsePacket
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteResponsePacket
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdatePacket
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.party.server.listener.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.timerTask

var server: PartyServer? = null

val currentParties = HashMap<UUID, AtlantisParty>()

val pendingInvites = ConcurrentHashMap<UUID, Long>()

val packageService = AtlantisPackageService(
        PartyUpdatePacket::class.java,
        PartyChangeOwnerPacket::class.java,
        PartyCreateRequestPacket::class.java,
        PartySavePacket::class.java,
        PartyDisbandPacket::class.java,
        PartyInfoRequestPacket::class.java,
        PartyServerInviteResponsePacket::class.java,
        PartyClientInviteRequestPacket::class.java,
        PartyClientInviteResponsePacket::class.java)

/**
 * Created by Yannic Rieger on 06.09.2017.
 *
 * @author Yannic Rieger
 */
class PartyServer {
    
    init {
        server = this
    }
    
    companion object {
        private val logger = AtlantisLogger.getLogger(PartyServer::class.java)
        @JvmStatic
        fun main(args: Array<String>) {
            logger.info("Starting party server instance...")
            logger.info("Adding listeners...")
    
            packageService.addListener(PartyUpdatePacket::class.java,              { pkg -> PartyUpdatePackageListener().onResponse(pkg) })
            packageService.addListener(PartyChangeOwnerPacket::class.java,         { pkg -> PartyOwnerChangedListener().onResponse(pkg) })
            packageService.addListener(PartyCreateRequestPacket::class.java,       { pkg -> PartyCreateRequestListener().onResponse(pkg) })
            packageService.addListener(PartySavePacket::class.java,                { pkg -> PartySavePackageListener().onResponse(pkg) })
            packageService.addListener(PartyDisbandPacket::class.java,             { pkg -> PartyDisbandListener().onResponse(pkg) })
            packageService.addListener(PartyInfoRequestPacket::class.java,         { pkg -> PartyInfoRequestListener().onResponse(pkg) })
            packageService.addListener(PartyClientInviteRequestPacket::class.java, { pkg -> PartyInviteRequestListener().onResponse(pkg) })
    
            logger.info("Starting removal of pending party invites every 30 seconds.")
            
            Timer().scheduleAtFixedRate(timerTask {
                pendingInvites.entries.forEach { (player, invited) ->
                    // Remove pending invites after 30 seconds.
                    // TODO: make configurable
                    
                    if (invited + 1000 * 30 <= System.currentTimeMillis()) {
                        pendingInvites.remove(player)
                        logger.info("Invite for party for $player has been expired and removed.")
                    }
                }
            }, 20, 1000)
        }
    }
}