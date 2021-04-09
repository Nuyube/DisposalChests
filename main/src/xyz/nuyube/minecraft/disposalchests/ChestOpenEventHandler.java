package xyz.nuyube.minecraft.disposalchests;
import org.bukkit.scheduler.BukkitRunnable;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.event.world.ChunkUnloadEvent; 

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
      if(event.getPlayer().hasPermission("disposalchests.use")) {
    BlockHandler(event.getBlock(), false);
}
else event.setCancelled(true);
  }

  private void BlockHandler(Block b, boolean Break) {
    if (b.getState() instanceof Container) {
      Container c = (Container) b.getState();
      plugin.getLogger().info("Block is container.");
      if(c.getCustomName() == null) {
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
      }
      else {
          
      plugin.getLogger().info("Name was " + c.getCustomName());
      }
    }
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    BlockHandler(event.getBlock(), true);
  }

  private void ChunkHandler(Chunk c, boolean Unload) {
    BlockState[] x = c.getTileEntities();
    for (BlockState BS : x) {
      if (BS instanceof Container && BS instanceof Nameable) { 
          Nameable n = (Nameable) BS; 
          if(n.getCustomName() == null) continue;
          if (n.getCustomName().equals("[Disposal]")) {
            try {
              DisposalChest dc = new DisposalChest(BS.getBlock());
              plugin.getLogger().info("Modifying chests due to ChunkHandler.");
              if (!Unload) DisposalChestManager.AddChest(
                dc
              ); else {
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
    ChunkHandler(event.getChunk(), false);
  }

  @EventHandler
  public void onChunkUnload(ChunkUnloadEvent event) {
    DisposalChestManager.RemoveChestsFromChunk(event.getChunk());
  }
}
