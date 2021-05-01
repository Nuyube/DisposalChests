package xyz.nuyube.minecraft.disposalchests;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Nameable;
import org.bukkit.block.Container;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

public class DisposalChestManager extends BukkitRunnable {

  static Plugin plugin;
  static ArrayList<DisposalChest> Chests = new ArrayList<DisposalChest>();
  private static DisposalChestManager instance;

  public static DisposalChestManager getInstance() {
    if (instance == null) {
      instance = new DisposalChestManager();
    }
    return instance;
  }

  private DisposalChestManager() {

  }

  static void init(Plugin p) {
    plugin = p;
  }

  public boolean hasChestAtLocation(Location l) {
    for (DisposalChest chest : Chests) {
      DisposalChestLocation chestLocation;

      chestLocation = chest.getLocation();

      Location cL;

      cL = chestLocation.getLocation();

      if (l.getWorld().getName() == chestLocation.world && l.getBlockX() == cL.getBlockX()
          && l.getBlockY() == cL.getBlockY() && l.getBlockZ() == cL.getBlockZ())
        return true;

    }
    return false;
  }

  public void addChest(DisposalChest chest) {
    plugin.getLogger().info("Adding new Disposal Chest at " + chest.getLocation().toString());
    Chests.add(chest);
  }

  public void removeChestsFromChunk(Chunk c) {
    int X = c.getX();
    int Z = c.getZ();
    for (int i = 0; i < Chests.size(); i++) {
      DisposalChest dc = Chests.get(i);
      DisposalChestLocation L = dc.getLocation();
      int oX = X * 16;
      int oZ = Z * 16;
      if (L.x >= oX && L.x <= oX + 16.0 && L.z >= oZ && L.z <= oZ + 16.0) {
        removeChest(dc);
      }
    }
  }

  public void removeChest(DisposalChest chest) {
    plugin.getLogger().info("Removing Disposal Chest from " + chest.getLocation().toString());
    Chests.remove(chest);
  }

  public void run() {
    // Create our warning item
    ItemStack itemStack;
    ItemMeta itemMeta;
    List<String> lore;

    itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
    itemMeta = itemStack.getItemMeta();
    lore = new ArrayList<String>();

    lore.add("THIS CHEST IS A DISPOSAL CHEST.");
    lore.add("ALL ITEMS PLACED INTO IT ARE FORFEIT.");

    itemMeta.setDisplayName("Warning!");
    itemMeta.setLore(lore);

    itemStack.setItemMeta(itemMeta);

    // Destroy all items in Disposal Chests.
    for (int i = 0; i < Chests.size(); i++) {
      DisposalChest chest = Chests.get(i);
      try {
        // Verify that the container is actually named [Disposal].
        Nameable n = (Nameable) chest.getBlock().getState();
        if (!n.getCustomName().equals("[Disposal]")) {
          removeChest(chest);
          continue;
        }

        Container container;
        Inventory inventory;
        ItemStack[] items;

        container = chest.getContainer();
        inventory = container.getSnapshotInventory();

        inventory.clear();

        items = new ItemStack[container.getInventory().getSize()];
        items[items.length / 2] = itemStack;

        inventory.setStorageContents(items);
        // Since we used getSnapshotInventory(), we have to update the container.
        container.update(true, false);
      } catch (Exception e) {
        plugin.getLogger().info("Removing a chest because of " + e.getMessage());
        removeChest(chest);
      }
    }
  }
}
