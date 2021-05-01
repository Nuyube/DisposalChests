package xyz.nuyube.minecraft.disposalchests;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import org.bukkit.Chunk;
import org.bukkit.Nameable;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

class ChunkLocation {

  int x;
  int z;
  World world;
  LocalDateTime unloaded;

  public ChunkLocation(Chunk c) {
    x = c.getX();
    z = c.getZ();
    world = c.getWorld();
    unloaded = LocalDateTime.now();
  }
}

class RecentlyLoadedChunks {

static   ArrayList<ChunkLocation> chunky = new ArrayList<ChunkLocation>();

  public static void Add(Chunk c) {
    ChunkLocation location = new ChunkLocation(c);
    chunky.add(location);
  }

  public static boolean Contains(Chunk c) {
     Tick();
    for(ChunkLocation l : chunky) {
      if(l.x == c.getX() && l.z == c.getZ() && l.world == c.getWorld()) return true;
    }
    return false;
  }

  public static void Tick() {
    for (int i = 0; i < chunky.size(); i++) {
      if (chunky.get(i).unloaded.plusSeconds(4).isBefore(LocalDateTime.now())) {
        chunky.remove(i);
        i--;
      }
    }
  }
}

class DisposalChestDeletionEventHandler extends BukkitRunnable {

  @Override
  public void run() {
    DisposalChestManager.Tick();
  }
}

class DisposalChestDiscoveryEventHandler implements Listener {

  public DisposalChestDiscoveryEventHandler(Plugin p) {
    plugin = p;
  }

  static Plugin plugin;

  @EventHandler
  public void onBlockPlaced(BlockPlaceEvent event) {
    if (event.getPlayer().hasPermission("disposalchests.use")) {
      blockHandler(event.getBlock(), false);
    }
  }

  private void blockHandler(Block b, boolean Break) {
    if (b.getState() instanceof Container) {
      Container c = (Container) b.getState();
      plugin.getLogger().info("Block is container.");
      if (c.getCustomName() == null) {
        plugin.getLogger().info("Block was not named.");
      }
      if (c.getCustomName().equals("[Disposal]")) {
        try {
          plugin.getLogger().info("Modifying chests due to BlockHandler.");
          if (!Break) DisposalChestManager.AddChest(
            new DisposalChest(b)
          ); else DisposalChestManager.RemoveChest(new DisposalChest(b));
        } catch (Exception e) {
          plugin.getLogger().severe(e.getMessage());
        }
      } else {
        plugin.getLogger().info("Name was " + c.getCustomName());
      }
    }
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    blockHandler(event.getBlock(), true);
  }

  private void chunkHandler(Chunk c, boolean Unload) {
    BlockState[] x = c.getTileEntities();
    for (BlockState BS : x) {
      if (BS instanceof Container && BS instanceof Nameable) {
        Nameable n = (Nameable) BS;
        if (n.getCustomName() == null) continue;
        if (n.getCustomName().equals("[Disposal]")) {
          try {
            DisposalChest dc = new DisposalChest(BS.getBlock());
            plugin.getLogger().info("Modifying chests due to ChunkHandler.");
            if (!Unload) DisposalChestManager.AddChest(dc); else {
              DisposalChestManager.RemoveChest(dc);
            }
          } catch (Exception e) {
            plugin.getLogger().severe(e.getMessage());
          }
        }
      }
    }
  }

  @EventHandler
  public void onChunkLoad(ChunkLoadEvent event) {
    if(RecentlyLoadedChunks.Contains(event.getChunk())) {
      return;
    }
    chunkHandler(event.getChunk(), false);
  }

  @EventHandler
  public void onChunkUnload(ChunkUnloadEvent event) {
    DisposalChestManager.RemoveChestsFromChunk(event.getChunk());
    RecentlyLoadedChunks.Add(event.getChunk());
  }
}
