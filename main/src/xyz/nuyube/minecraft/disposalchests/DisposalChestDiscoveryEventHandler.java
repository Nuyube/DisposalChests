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
 
            // If it IS named, and is named [Disposal], we need to check for permissions
            if (name != null && name.equals("[Disposal]")) {
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

        BlockState[] chunkTileEntities;

        chunkTileEntities = c.getTileEntities();

        for (BlockState blockState : chunkTileEntities) {

            // If the blockstate is a nameable container
            if (blockState instanceof Container && blockState instanceof Nameable) {

                String name;
                Nameable nameable;

                nameable = (Nameable) blockState;
                name = nameable.getCustomName();

                if (name != null && name.equals("[Disposal]")) {
                    try {
                        DisposalChest disposalChest;
                        Block block;

                        block = blockState.getBlock();
                        disposalChest = new DisposalChest(block);

                        DisposalChestManager manager;

                        manager = DisposalChestManager.getInstance();

                        if (Unload)
                            manager.removeChest(disposalChest);
                        else
                            manager.addChest(disposalChest);

                    } catch (Exception e) {
                        plugin.getLogger().severe(e.getMessage());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk;

        chunk = event.getChunk();

        if (!RecentlyLoadedChunks.Contains(chunk)) {
            chunkHandler(chunk, false);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk;

        chunk = event.getChunk();

        DisposalChestManager.getInstance().removeChestsFromChunk(chunk);
        RecentlyLoadedChunks.Add(chunk);
    }
}