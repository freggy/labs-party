package de.bergwerklabs.party.server

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.AtlantisParty
import de.bergwerklabs.atlantis.api.party.packages.PartyChangeOwnerPackage
import de.bergwerklabs.atlantis.api.party.packages.PartyDisbandPackage
import de.bergwerklabs.atlantis.api.party.packages.PartySavePackage
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.info.PartyInfoRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyClientInviteRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyClientInviteResponsePackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteResponsePackage
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdatePackage
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.party.server.listener.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.timerTask

var server: PartyServer? = null

val currentParties = HashMap<UUID, AtlantisParty>()

val pendingInvites = HashMap<UUID, CopyOnWriteArrayList<InviteInfo>>()

val packageService = AtlantisPackageService(PartyUpdatePackage::class.java,
        PartyChangeOwnerPackage::class.java,
        PartyCreateRequestPackage::class.java,
        PartySavePackage::class.java,
        PartyDisbandPackage::class.java,
        PartyInfoRequestPackage::class.java,
        PartyServerInviteRequestPackage::class.java,
        PartyClientInviteRequestPackage::class.java,
        PartyClientInviteResponsePackage::class.java)

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
    
            packageService.addListener(PartyUpdatePackage::class.java,              { pkg -> PartyUpdatePackageListener().onResponse(pkg) })
            packageService.addListener(PartyChangeOwnerPackage::class.java,         { pkg -> PartyOwnerChangedListener().onResponse(pkg) })
            packageService.addListener(PartyCreateRequestPackage::class.java,       { pkg -> PartyCreateRequestListener().onResponse(pkg) })
            packageService.addListener(PartySavePackage::class.java,                { pkg -> PartySavePackageListener().onResponse(pkg) })
            packageService.addListener(PartyDisbandPackage::class.java,             { pkg -> PartyDisbandListener().onResponse(pkg) })
            packageService.addListener(PartyInfoRequestPackage::class.java,         { pkg -> PartyInfoRequestListener().onResponse(pkg) })
            packageService.addListener(PartyClientInviteRequestPackage::class.java, { pkg -> PartyInviteRequestListener().onResponse(pkg) })
    
            logger.info("Starting removal of pending party invites every 30 seconds.")
            /*
            Timer().scheduleAtFixedRate(timerTask {
                pendingInvites.entries.forEach { (partyId, infos) ->
                    // Remove pending invites after 30 seconds.
                    // TODO: make configurable
                    infos.forEach { info ->
                        if ((System.currentTimeMillis() - info.invited) <= 1000 * 30) {
                            infos.remove(info)
                            logger.info("Invite for party $partyId for ${info.player} has been expired and removed.")
                        }
                    }
                }
            }, 20, 1000) */
        }
    }
}