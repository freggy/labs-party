import de.bergwerklabs.atlantis.client.base.ConfiguredClientBootstrap

/**
 * Created by Yannic Rieger on 08.09.2017.
 *
 * Main class of the API.
 *
 * @author Yannic Rieger
 */
class Main {
    
    companion object {
        
        @JvmStatic
        fun main(args: Array<String>) {
            ConfiguredClientBootstrap.startConfiguredClients()
        }
    }
}