package xyz.nuyube.minecraft.disposalchests;

import java.util.logging.Logger;
import org.bukkit.scheduler.BukkitTask;

import de.jeff_media.updatechecker.UpdateChecker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DisposalChests extends JavaPlugin {

  static Logger PluginLogger = null;

  @Override
  public void onEnable() {
    // Start our plugin logger
    PluginLogger = getLogger();
    // Check for updates
    UpdateChecker.init(this, 91132).checkNow();
    // Register our sellxp command and its alias
    Bukkit.getPluginManager().registerEvents(new DisposalChestDiscoveryEventHandler(this), this);
    Bukkit.getPluginManager().registerEvents(new WarningItemRemovalDenialEventHandler(), this);
    DisposalChestManager.init(this);
    DeletionTask = DisposalChestManager.getInstance().runTaskTimer(this, 20, 20);
  }

  BukkitTask DeletionTask = null;

  @Override
  // We don't actually do anything on disable.
  public void onDisable() {
    PluginLogger.info("[Nuyube's DisposalChests] Disabled.");
    PluginLogger = null;
  }

}
