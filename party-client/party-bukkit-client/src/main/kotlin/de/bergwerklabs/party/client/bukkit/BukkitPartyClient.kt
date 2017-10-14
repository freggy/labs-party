package de.bergwerklabs.party.client.bukkit

import de.bergwerklabs.atlantis.api.party.packages.invite.PartyClientInviteRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteRequestPackage
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.commons.spigot.chat.messenger.PluginMessenger
import de.bergwerklabs.party.client.bukkit.command.PartyChatCommand
import de.bergwerklabs.party.client.bukkit.command.PartyCreateCommand
import de.bergwerklabs.party.client.bukkit.command.PartyParentCommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

var bukkitClient: BukkitPartyClient? = null

/**
 * Created by Yannic Rieger on 30.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class BukkitPartyClient : JavaPlugin() {
    
    val messenger = PluginMessenger("Party")
    
    override fun onEnable() {
        bukkitClient = this
        this.getCommand("party").executor = PartyParentCommand("party", PartyCreateCommand())
        this.getCommand("p").executor = PartyChatCommand()
        
        
        AtlantisPackageUtil.addListener(PartyClientInviteRequestPackage::class.java, { pkg ->
            val player = Bukkit.getPlayer(pkg.playerUuid)
            if (player != null) {
                // TODO: send message
            }
        })
        
        AtlantisPackageUtil.addListener(PartyServerInviteRequestPackage::class.java, { pkg ->
            Bukkit.getPlayer(pkg.playerUuid)?.sendMessage("DU HAST EINEN INVITE BEKOMMEN YAAAY!")
        })
    }
    
    override fun onDisable() {}
}