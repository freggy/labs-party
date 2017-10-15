package de.bergwerklabs.party.client.bukkit

import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteResponsePackage
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.commons.spigot.chat.messenger.PluginMessenger
import de.bergwerklabs.party.client.bukkit.command.PartyChatCommand
import de.bergwerklabs.party.client.bukkit.command.PartyCreateCommand
import de.bergwerklabs.party.client.bukkit.command.PartyParentCommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

internal var bukkitClient: BukkitPartyClient? = null
internal val packageService: AtlantisPackageService = AtlantisPackageService(PartyServerInviteRequestPackage::class.java,
                                                                             PartyServerInviteResponsePackage::class.java)


/**
 * Created by Yannic Rieger on 30.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class BukkitPartyClient : JavaPlugin() {
    
    val messenger = PluginMessenger("Party")
    val invitedFor = HashMap<UUID, PartyServerInviteRequestPackage>()
    
    override fun onEnable() {
        bukkitClient = this
        this.getCommand("party").executor = PartyParentCommand("party", PartyCreateCommand())
        this.getCommand("p").executor = PartyChatCommand()
    
    
        packageService.addListener(PartyServerInviteResponsePackage::class.java, { pkg ->
            val player = Bukkit.getPlayer(pkg.initalSender)
            if (player != null) {
                // TODO: send message
            }
        })
    
        packageService.addListener(PartyServerInviteRequestPackage::class.java, { pkg ->
            Bukkit.getPlayer(pkg.responder).let {
                it.sendMessage("DU HAST EINEN INVITE BEKOMMEN YAAAY!")
                invitedFor[it.uniqueId] = pkg
            }
        })
    }
    
    override fun onDisable() {}
}