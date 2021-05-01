package xyz.nuyube.minecraft.disposalchests;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class WarningItemRemovalDenialEventHandler implements Listener {
    
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

}