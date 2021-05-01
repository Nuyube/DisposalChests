package xyz.nuyube.minecraft.disposalchests;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import org.bukkit.entity.Item;

class DisposalChestDiscoveryEventHandler implements Listener {

    public DisposalChestDiscoveryEventHandler(Plugin p) {
        plugin = p;
    }

    static Plugin plugin;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item;
        boolean isWarningStack;

        item = event.getCurrentItem();
        isWarningStack = itemStackIsWarningStack(item);

        if (isWarningStack) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        ItemStack item;
        boolean isWarningStack;

        item = event.getItem();
        isWarningStack = itemStackIsWarningStack(item);

        if (isWarningStack) {
            event.setCancelled(true);
            return;
        }

        Inventory inventory;
        InventoryHolder holder;

        inventory = event.getSource();
        holder = inventory.getHolder();

        if(holder instanceof Hopper) {
            Hopper hopper;
            hopper = (Hopper)holder;

            String name;
            name = hopper.getCustomName();

            
            if(name != null && name.equals("[Disposal]")) {
                event.setCancelled(true);
                return;
            }

        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        List<Item> droppedItems;

        droppedItems = event.getItems();

        for (int i = 0; i < droppedItems.size(); i++) {
            Item item;
            ItemStack itemStack;

            item = droppedItems.get(i);
            itemStack = item.getItemStack();

            if (itemStackIsWarningStack(itemStack)) {
                droppedItems.remove(item);
                i--;
            }
        }
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        ItemStack item;
        boolean isWarningStack;

        item = event.getItem();
        isWarningStack = itemStackIsWarningStack(item);

        if (isWarningStack) {
            event.setCancelled(true);
        }
    }

    private boolean itemStackIsWarningStack(ItemStack item) {
        ItemMeta meta;

        meta = item.getItemMeta();

        // Verify that this is the item we usually use
        if (item.getType() != Material.RED_STAINED_GLASS_PANE)
            return false;
        else if (item.getAmount() != 1)
            return false;
        else if (!meta.hasLore())
            return false;
        else if (!meta.hasDisplayName())
            return false;
        else
            return true;
    }

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