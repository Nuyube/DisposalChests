package xyz.nuyube.minecraft.disposalchests;

import org.bukkit.Chunk;
import org.bukkit.Nameable;
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
          if (!Break) DisposalChestManager.getInstance().AddChest(
            new DisposalChest(b)
          ); else DisposalChestManager.getInstance().RemoveChest(new DisposalChest(b));
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
            if (!Unload) DisposalChestManager.getInstance().AddChest(dc); else {
              DisposalChestManager.getInstance().RemoveChest(dc);
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
    DisposalChestManager.getInstance().RemoveChestsFromChunk(event.getChunk());
    RecentlyLoadedChunks.Add(event.getChunk());
  }
}