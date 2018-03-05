package de.bergwerklabs.party.client.bungee

import de.bergwerklabs.atlantis.client.base.playerdata.PlayerdataSet
import de.bergwerklabs.atlantis.client.base.playerdata.SettingsFlag
import de.bergwerklabs.atlantis.client.base.resolve.PlayerResolver
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.wrapper.PartyInviteResponse
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashSet

internal fun canSendInvite(toInvite: UUID): Boolean {
    val set = PlayerdataSet(toInvite) //AtlantisPlayerdataFactory().createInstance(toInvite) as AtlantisPlayerdataSet
    set.loadAndWait()
    return set.playerSettings.isSet(SettingsFlag.GLOBAL_PARTY_REQUESTS_ENABLED)
}


/**
 * Handles the response of a party invitation.
 *
 * @param response     response of the invitation.
 * @param inviteSender player who sent the party invitation.
 */
internal fun handlePartyInviteResponse(response: PartyInviteResponse, inviteSender: ProxiedPlayer) {
    val messenger = partyBungeeClient!!.messenger
    
    PlayerResolver.resolveUuidToName(response.playerUuid).ifPresent({
        val color = partyBungeeClient!!.bridge.getGroupPrefix(response.playerUuid)
        
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
internal fun sendPartyInvites(inviter: ProxiedPlayer, potentialIds: MutableList<String>, party: Party) {
    /*
    val remove = potentialIds
        .filter { cooldown[inviter.uniqueId]!!.any { info -> info.name.equals(it, true) } }
        .toList()
    
    remove.forEach {
        partyBungeeClient!!.messenger.message("Du musst 30 Sekunden warten, bevor du $it einladen kannst", inviter)
    }*/
    
    //potentialIds.removeAll(remove)
    
    potentialIds.forEach { pId ->
            if (!pId.equals(inviter.name, true)) {
                //cooldown[inviter.uniqueId]!!.add(CooldownInfo(pId, System.currentTimeMillis()))
                if (isOnline(pId)) {
                    PlayerResolver.resolveNameToUuid(pId).ifPresent({ id ->
                        if (canSendInvite(id)) {
                            partyBungeeClient!!.messenger.message("§7Die Einladung wurde verschickt.", inviter)
                            party.invite(id, inviter.uniqueId, Consumer { response: PartyInviteResponse -> handlePartyInviteResponse(response, inviter) })
                        }
                        else {
                            val color = ChatColor.translateAlternateColorCodes('&', partyBungeeClient!!.bridge.getGroupPrefix(id))
                            partyBungeeClient!!.messenger.message("$color$pId §cmöchte nicht eingeladen werden.", inviter)
                        }
                    })
                }
            }
            else partyBungeeClient!!.messenger.message("§c$pId ist nicht online.", inviter)
    }
}

internal fun handlePartyUpdate(party: Party, affected: UUID, memberMessage: String, affectedMessage: String) {
    val color = ChatColor.translateAlternateColorCodes('&', partyBungeeClient!!.bridge.getGroupPrefix(affected))
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
