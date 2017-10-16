package de.bergwerklabs.party.client.bukkit.common

import de.bergwerklabs.commons.spigot.chat.MessageUtil
import de.bergwerklabs.party.api.wrapper.PartyInviteResponse
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import de.bergwerklabs.party.client.bukkit.bukkitClient
import mkremins.fanciful.FancyMessage
import org.bukkit.ChatColor
import org.bukkit.entity.Player

internal fun handlePartyInviteResponse(response: PartyInviteResponse, inviteSender: Player) {
    when (response.status) {
        PartyInviteStatus.ACCEPTED          -> {
        
        }
        PartyInviteStatus.DENIED            -> bukkitClient!!.messenger.message("${ChatColor.RED}DENIED", inviteSender)
        PartyInviteStatus.EXPIRED           -> bukkitClient!!.messenger.message("${ChatColor.LIGHT_PURPLE}EXPIRED", inviteSender)
        PartyInviteStatus.PARTY_FULL        -> TODO()
        PartyInviteStatus.PARTY_NOT_PRESENT -> TODO()
    }
}

