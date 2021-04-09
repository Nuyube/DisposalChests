package xyz.nuyube.minecraft.disposalchests;

import java.util.ArrayList;
import java.util.List;


import org.bukkit.Nameable;
import org.bukkit.block.Container;
import org.bukkit.plugin.Plugin;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
public class DisposalChestManager {

  static Plugin plugin;
  static ArrayList<DisposalChest> Chests = new ArrayList<DisposalChest>();

  static void Init(Plugin p) {
    plugin = p;
  }

  public static void AddChest(DisposalChest chest) {
    plugin
      .getLogger()
      .info("Adding new Disposal Chest at " + chest.getLocation().toString());
    Chests.add(chest);
  }
public static void RemoveChestsFromChunk(Chunk c) {
  
  int X = c.getX();
  int Z = c.getZ();
  //plugin.getLogger().info("Running chunk unload task on CHUNK=("+X+","+Z+")");
  for(int i = 0; i < Chests.size(); i++) {
    DisposalChest dc = Chests.get(i);
    Location L = dc.getLocation();
    int oX = X * 16;
    int oZ = Z * 16;
    if(L.getX() >= oX && L.getX() <= oX + 16.0
      && L.getZ() >= oZ && L.getZ() <= oZ + 16.0) {
        plugin.getLogger().info("Removing chest at CHUNK=("+X+","+Z+")");
        RemoveChest(dc);
      }
  }
  
}
  public static void RemoveChest(DisposalChest chest) {
    plugin
      .getLogger()
      .info("Removing Disposal Chest from " + chest.getLocation().toString());
    Chests.remove(chest);
  }

  public static void Tick() {
    //plugin.getLogger().info("Chests.size = " + Chests.size());
    //Destroy all items in Disposal Chests.
    for(int i =0; i < Chests.size(); i++) {
      DisposalChest chest = Chests.get(i);
      if(!chest.getLocation().getChunk().isLoaded()) {
        plugin.getLogger().info("Unloading chest because chunk is not loaded.");
        RemoveChest(chest);
      }
      try {
        //Verify that the container is actually named [Disposal].
        Nameable n = (Nameable) chest.getBlock().getState();
        if (!n.getCustomName().equals("[Disposal]")) {
          RemoveChest(chest);
          continue;
        }
        Material M = Material.WRITABLE_BOOK;
        ItemStack X = new ItemStack(M);
        X.getItemMeta().setDisplayName("IMPORTANT NOTE");
        List<String> Lore = new ArrayList<String>();
        Lore.add("THIS CHEST IS A DISPOSAL CHEST.");
        Lore.add("ALL ITEMS PLACED INTO IT ARE FORFEIT.");
        X.getItemMeta().setLore(Lore);
        X.setAmount(50);
        
        Container c = chest.getContainer();
        Inventory xI = c.getInventory();
        ItemStack[] NewItems = new ItemStack[xI.getSize()];
        NewItems[NewItems.length / 2] = X;
        xI.setContents(NewItems); 
        
        c.update(true, false);
      } catch (Exception e) {
        plugin.getLogger().info("Removing a chest because of " +e.getMessage());
        RemoveChest(chest);
      }
    }
  }
}
