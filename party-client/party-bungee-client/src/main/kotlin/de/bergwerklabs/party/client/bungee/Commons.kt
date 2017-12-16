package de.bergwerklabs.party.client.bungee

import de.bergwerklabs.atlantis.client.base.resolve.PlayerResolver
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.wrapper.PartyInviteResponse
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashSet

/**
 * Handles the response of a party invitation.
 *
 * @param response     response of the invitation.
 * @param inviteSender player who sent the party invitation.
 */
internal fun handlePartyInviteResponse(response: PartyInviteResponse, inviteSender: ProxiedPlayer) {
    val messenger = partyBungeeClient!!.messenger
    
    PlayerResolver.resolveUuidToName(response.playerUuid).ifPresent({
        val color = partyBungeeClient!!.zBridge.getRankColor(response.playerUuid).toString()
        
        when (response.status) {
            PartyInviteStatus.ACCEPTED          -> messenger.message("§a✚§r $color$it §bist der Party beigetreten.", inviteSender)
            PartyInviteStatus.DENIED            -> messenger.message("§c✖§r $color$it §bhat die Einaldung abgelehnt.", inviteSender)
            PartyInviteStatus.ALREADY_PARTIED   -> messenger.message("$color$it §cist bereits in einer Party", inviteSender)
            PartyInviteStatus.PARTY_NOT_PRESENT -> messenger.message("§cDu musst erst eine Party erstellen", inviteSender)
            PartyInviteStatus.PARTY_FULL        -> messenger.message("§cDie Party ist voll.", inviteSender)
        }
    })
}

/**
 * Sends party invites to players.
 *
 * @param inviter      player who send the party invitation.
 * @param potentialIds array containing potential player names.
 * @param party        party to invite them to.
 */
internal fun sendPartyInvites(inviter: ProxiedPlayer, potentialIds: Array<out String>?, party: Party) {
    potentialIds?.forEach { pId ->
        if (!pId.equals(inviter.name, true)) {
            if (isOnline(pId)) {
                // If the invited player is on the same server as the party client we can use the Bukkit method to resolve the name to a UUID.
                // It returns null if the player is not on the server.
                val invited = partyBungeeClient!!.proxy.getPlayer(pId)
                partyBungeeClient!!.messenger.message("§7Die Einladung wurde verschickt.", inviter)
                if (invited == null) {
                    PlayerResolver.resolveNameToUuid(pId).ifPresent({ id ->
                        party.invite(id, inviter.uniqueId, Consumer { response: PartyInviteResponse -> handlePartyInviteResponse(response, inviter) })
                    })
                }
                else party.invite(invited.uniqueId, inviter.uniqueId, Consumer { response: PartyInviteResponse -> handlePartyInviteResponse(response, inviter) })
            }
            else partyBungeeClient!!.messenger.message("§c$pId ist nicht online.", inviter)
        }
    }
}

internal fun handlePartyUpdate(party: Party, affected: UUID, memberMessage: String, affectedMessage: String) {
    val color = partyBungeeClient!!.zBridge.getRankColor(affected).toString()
    val name = PlayerResolver.resolveUuidToName(affected)
    
    partyBungeeClient!!.proxy.getPlayer(affected)?.let {
        partyBungeeClient!!.messenger.message(affectedMessage, it)
    }
    
    val set = HashSet(party.getMembers())
    set.add(party.getPartyOwner())
    
    set
            .filter  { memberUuid -> memberUuid != affected }
            .map     { memberUuid -> partyBungeeClient!!.proxy.getPlayer(memberUuid) }
            .filter  { member -> Objects.nonNull(member) }
            .forEach { member ->  partyBungeeClient!!.messenger.message(memberMessage.replace("{c}", color).replace("{p}", name.orElse(":(")), member) }
    
}

internal fun isOnline(nameOrId: String): Boolean {
    return PlayerResolver.getOnlinePlayerCacheEntry(nameOrId).isPresent
}
