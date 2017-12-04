package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.commons.spigot.chat.MessageUtil
import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.api.wrapper.PartyCreateStatus
import de.bergwerklabs.party.api.wrapper.PartyInviteResponse
import de.bergwerklabs.party.client.bukkit.bukkitClient
import de.bergwerklabs.party.client.bukkit.common.handlePartyInviteResponse
import mkremins.fanciful.FancyMessage
import mkremins.fanciful.TextualComponent
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 * Created by Yannic Rieger on 03.10.2017.
 *
 * Creates a party.
 *
 * Command usage: /party create {members}
 * Example: /party create Fraklez Bw2801 Herul freggyy
 *
 * @author Yannic Rieger
 */
class PartyCreateCommand : BungeeCommand {
    
    override fun getUsage() = "/party create <member|members>"
    
    override fun getName(): String = "create"
    
    override fun getDescription() = "Lade einen oder mehrere Spieler zu einer Party ein."
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender is ProxiedPlayer) {
            val result = PartyApi.createParty(sender.uniqueId)
            when {
                result.status == PartyCreateStatus.SUCCESS -> {
                    bukkitClient!!.messenger.message("§aParty wurde erflogreich erstellt.", sender)
                    this.sendPartyInvites(args, result.party.get())
                }
                result.status == PartyCreateStatus.DENY_TOO_MANY_MEMBERS_DEFAULT -> {
                    errorMessage(sender, 4) // TODO: insert fitting value
                }
                result.status == PartyCreateStatus.DENY_TOO_MANY_MEMBERS_PREMIUM -> {
                    errorMessage(sender, 4) // TODO: insert fitting value
                }
            }
        }
    }
    
    /**
     * Sends party invites to players.
     *
     * @param potentialIds array containing potential player names.
     * @param party        party to invite them to.
     */
    private fun sendPartyInvites(potentialIds: Array<out String>?, party: Party, player: ProxiedPlayer) {
        potentialIds?.forEach { pId ->
            // If the invited player is on the same server as the party client we can use the Bukkit method to resolve the name to a UUID.
            // It returns null if the player is not on the server.
            val invited = Bukkit.getServer().getPlayer(pId)
            if (invited == null) {
                PlayerResolver.resolveNameToUuid(pId).ifPresent({ id ->
                    party.invite(id, player.uniqueId, Consumer { response: PartyInviteResponse -> handlePartyInviteResponse(response, player) })
                })
            }
            else party.invite(invited.uniqueId, player.uniqueId, Consumer { response: PartyInviteResponse -> handlePartyInviteResponse(response, player) })
        }
    }
    
    /**
     * Displays the error message and plays a sound.
     *
     * @param player player to play the sound to (Sound.NOTE_BASS, 1F, 100F).
     * @param count  maximum party player count.
     */
    private fun errorMessage(player: Player, count: Int) {
        bukkitClient!!.messenger.message("§cDu kannst maximal §b$count Spieler einladen.", player)
        player.playSound(player.eyeLocation, Sound.NOTE_BASS, 1F, 100F)
    }
}