package xyz.nuyube.minecraft.disposalchests;

import org.bukkit.block.Block;
import org.bukkit.block.Container;

class DisposalChest {
  DisposalChestLocation location;
  //Location spigotLocation;
  Block block;
  Container container;

  public DisposalChest(Block b) throws Exception {
    if (!(b.getState() instanceof Container)) {
      throw new Exception("The specified block is not a container!");
    } else {
      container = (Container) b.getState();
      block = b;
      location = new DisposalChestLocation(b.getLocation());
      //spigotLocation = b.getLocation();
    }
  }

  public DisposalChestLocation getLocation() {
    return location;
  } 

  public Block getBlock() {
    return block;
  }

  public Container getContainer() {
    return container;
  }
}