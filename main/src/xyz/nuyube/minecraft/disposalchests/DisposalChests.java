package xyz.nuyube.minecraft.disposalchests;

import java.util.logging.Logger;

import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class DisposalChests extends JavaPlugin {
 
  static Logger PluginLogger = null; 


  @Override
  public void onEnable() {
    //Start our plugin logger
    PluginLogger = getLogger();
    //Check for updates
    new UpdateChecker(this, 91103)
      .getVersion(
          version -> {
            if (
              this.getDescription().getVersion().equalsIgnoreCase(version)
            ) {} else {
              Bukkit
                .getConsoleSender()
                .sendMessage(
                  ChatColor.GREEN +
                  "[Nuyube's DisposalChests] There is a new update available!"
                );
            }
          }
        ); 

    //Register our sellxp command and its alias 
    Bukkit.getPluginManager().registerEvents(new DisposalChestDiscoveryEventHandler(this), this);
    DisposalChestManager.Init(this);
    DeletionTask = new DisposalChestDeletionEventHandler().runTaskTimer(this, 2, 2);
  }
BukkitTask DeletionTask = null;
  @Override
  //We don't actually do anything on disable.
  public void onDisable() {
    PluginLogger.info("[Nuyube's DisposalChests] Disabled."); 
    PluginLogger = null;
  }

}
