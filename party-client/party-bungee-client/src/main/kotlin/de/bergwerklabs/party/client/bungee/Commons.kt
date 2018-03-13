package de.bergwerklabs.party.client.bungee

import de.bergwerklabs.api.cache.pojo.PlayerNameToUuidMapping
import de.bergwerklabs.atlantis.client.base.playerdata.PlayerdataSet
import de.bergwerklabs.atlantis.client.base.playerdata.SettingsFlag
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
    
    val color = partyBungeeClient!!.bridge.getGroupPrefix(response.playerUuid.uuid)
    val name = response.playerUuid.name
    
    when (response.status) {
        PartyInviteStatus.ACCEPTED -> messenger.message(
            "§a✚§r $color$name §bist der Party beigetreten.", inviteSender
        )
        PartyInviteStatus.DENIED -> messenger.message(
            "§c✖§r $color$name §bhat die Einaldung abgelehnt.", inviteSender
        )
        PartyInviteStatus.ALREADY_PARTIED -> messenger.message(
            "$color$name §cist bereits in einer Party", inviteSender
        )
        PartyInviteStatus.PARTY_NOT_PRESENT -> messenger.message(
            "§cDu musst erst eine Party erstellen", inviteSender
        )
        PartyInviteStatus.PARTY_FULL -> messenger.message("§cDie Party ist voll.", inviteSender)
    }
}

/**
 * Sends party invites to players.
 *
 * @param inviter      player who send the party invitation.
 * @param potentialIds array containing potential player names.
 * @param party        party to invite them to.
 */
internal fun sendPartyInvites(inviter: ProxiedPlayer, potentialIds: MutableList<String>, party: Party) {
    potentialIds.forEach { pId ->
        if (!pId.equals(inviter.name, true)) {
            if (isOnline(pId)) {
                partyBungeeClient!!.messenger.message("§7Die Einladung wurde verschickt.", inviter)
                party.invite(
                    PlayerNameToUuidMapping(pId, null),
                    PlayerNameToUuidMapping(inviter.name, inviter.uniqueId),
                    Consumer { response: PartyInviteResponse -> handlePartyInviteResponse(response, inviter) })
            }
        }
        else partyBungeeClient!!.messenger.message("§c$pId ist nicht online.", inviter)
    }
}

internal fun handlePartyUpdate(
    party: Party,
    affected: PlayerNameToUuidMapping,
    memberMessage: String,
    affectedMessage: String
) {
    val color = ChatColor.getByChar(partyBungeeClient!!.bridge.getGroupPrefix(affected.uuid)[1])
    val name = affected.name
    
    partyBungeeClient!!.proxy.getPlayer(affected.uuid)?.let {
        partyBungeeClient!!.messenger.message(affectedMessage, it)
    }
    
    val set = HashSet(party.getMembers())
    set.add(party.getPartyOwner())
    
    set
        .filter { memberUuid -> memberUuid != affected.uuid }
        .map { memberUuid -> partyBungeeClient!!.proxy.getPlayer(memberUuid) }
        .filter { member -> Objects.nonNull(member) }
        .forEach { member ->
            partyBungeeClient!!.messenger.message(
                memberMessage.replace("{c}", color.toString()).replace("{p}", name), member
            )
        }
    
}

internal fun isOnline(nameOrId: String): Boolean {
    return true//PlayerResolver.getOnlinePlayerCacheEntry(nameOrId).isPresent
}
