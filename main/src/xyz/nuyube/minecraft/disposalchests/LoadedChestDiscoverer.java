package xyz.nuyube.minecraft.disposalchests;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.scheduler.BukkitRunnable;

class LoadedChestDiscoverer extends BukkitRunnable {
  @Override
  public void run() {
    List<World> worlds;

    worlds = Bukkit.getWorlds();

    for (World world : worlds) {
      Chunk[] loadedChunks;

      loadedChunks = world.getLoadedChunks();

      for (Chunk chunk : loadedChunks) {
        BlockState[] tileEntities;

        tileEntities = chunk.getTileEntities();

        for (BlockState blockState : tileEntities) {

          // The blockstate must be a nameable container
          if (!(blockState instanceof Container)) {
            continue;
          }
          if (!(blockState instanceof Nameable)) {
            continue;
          }

          Nameable nameable;
          
          nameable = (Nameable)blockState;

          String name;

          name = nameable.getCustomName();

          if(name == null) {
              continue;
          }
          else if (!name.equals("[Disposal]")) {
              continue;
          }

          Location location;
          boolean alreadyTracked;
          DisposalChestManager manager;

          manager = DisposalChestManager.getInstance();
          location = blockState.getLocation();
          alreadyTracked = manager.hasChestAtLocation(location);

          if (!alreadyTracked) {
            try {
              manager.addChest(new DisposalChest(blockState.getBlock()));
            } catch (Exception e) {
              // Block was not a container.
            }
          }

        }
      }
    }
  }
}