package xyz.nuyube.minecraft.disposalchests;

import org.bukkit.Chunk;
import org.bukkit.Nameable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player; 
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
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
        blockHandler(event, event.getPlayer().hasPermission("disposalchests.use"));
    }

    private void blockHandler(BlockEvent blockEvent, boolean hasPermission) {
        Block block;
        boolean Break;

        block = blockEvent.getBlock();
        Break = blockEvent instanceof BlockBreakEvent;

        if (block.getState() instanceof Container) {
            // Get the container
            Container container;
            String name;

            container = (Container) block.getState();
            name = container.getCustomName();

            // If the item isn't named, we don't care.
            if (name == null) {
                return;
            }
            // If it IS named, and is named [Disposal], we need to check for permissions
            if (name.equals("[Disposal]")) {
                try {
                    DisposalChestManager manager;

                    manager = DisposalChestManager.getInstance();
                    // If the user does not have permission to modify disposal chests, cancel the
                    // event.
                    if (Break) {
                        BlockBreakEvent event;
                        Player player;

                        event = (BlockBreakEvent) blockEvent;
                        player = event.getPlayer();

                        if (!player.hasPermission("disposalchests.destroy")) {
                            event.setCancelled(true);
                            return;
                        } else {

                            DisposalChest dChest;

                            dChest = new DisposalChest(block);

                            manager.removeChest(dChest);
                        }
                    } else {
                        BlockPlaceEvent event;
                        Player player;

                        event = (BlockPlaceEvent) blockEvent;
                        player = event.getPlayer();

                        if (!player.hasPermission("disposalchests.create")) {
                            event.setCancelled(true);
                            return;
                        } else {
                            DisposalChest dChest;

                            dChest = new DisposalChest(block);

                            manager.addChest(dChest);
                        }
                    } 
                } catch (Exception e) {
                    plugin.getLogger().severe(e.getMessage());
                }
            } 
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        blockHandler(event, event.getPlayer().hasPermission("disposalchests.use"));
    }

    private void chunkHandler(Chunk c, boolean Unload) {
        BlockState[] x = c.getTileEntities();
        for (BlockState BS : x) {
            if (BS instanceof Container && BS instanceof Nameable) {
                Nameable n = (Nameable) BS;
                if (n.getCustomName() == null)
                    continue;
                if (n.getCustomName().equals("[Disposal]")) {
                    try {
                        DisposalChest dc = new DisposalChest(BS.getBlock());
                        plugin.getLogger().info("Modifying chests due to ChunkHandler.");
                        if (!Unload)
                            DisposalChestManager.getInstance().addChest(dc);
                        else {
                            DisposalChestManager.getInstance().removeChest(dc);
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
        if (RecentlyLoadedChunks.Contains(event.getChunk())) {
            return;
        }
        chunkHandler(event.getChunk(), false);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        DisposalChestManager.getInstance().removeChestsFromChunk(event.getChunk());
        RecentlyLoadedChunks.Add(event.getChunk());
    }
}