package de.bergwerklabs.party.client.bukkit.command

import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.api.wrapper.PartyCreateStatus
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import de.bergwerklabs.party.client.bukkit.bukkitClient
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

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
class PartyCreateCommand : ChildCommand {
    
    override fun getName(): String = "create"
    
    private lateinit var player: Player
    
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender is Player) {
            player = sender
            val result = PartyApi.createParty(sender.uniqueId)
            when {
                result.status == PartyCreateStatus.SUCCESS -> {
                    bukkitClient!!.messenger.message("§aParty wurde erflogreich erstellt.", sender)
                    bukkitClient!!.messenger.message(result.party.get().toString(), sender)
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
        return true
    }
    
    /**
     * Sends party invites to players.
     *
     * @param potentialIds array containing potential player names.
     * @param party        party to invite them to.
     * @return             [List] containing the [UUID]s of the players.
     */
    private fun sendPartyInvites(potentialIds: Array<out String>?, party: Party): List<UUID> {
        potentialIds?.forEach { pId ->
            // If the invited player is on the same server as the party client we can use the Bukkit method to resolve the name to a UUID.
            // It returns null if the player is not on the server.
            val uuid = Bukkit.getServer().getPlayer(pId)?.uniqueId
            if (uuid == null) {
                this.handleStatus(party.invite(pId))
            }
            else this.handleStatus(party.invite(uuid))
        }
        return listOf()
    }
    
    /**
     * Executes actions based on the [PartyInviteStatus].
     *
     * @param inviteStatus status of the invitation.
     */
    private fun handleStatus(inviteStatus: PartyInviteStatus) {
        when (inviteStatus) {
            PartyInviteStatus.ACCEPTED -> bukkitClient!!.messenger.message("${ChatColor.GREEN}ACCEPTED", player)
            PartyInviteStatus.DENIED   -> bukkitClient!!.messenger.message("${ChatColor.RED}DENIED", player)
            PartyInviteStatus.EXPIRED  -> bukkitClient!!.messenger.message("${ChatColor.LIGHT_PURPLE}EXPIRED", player)
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