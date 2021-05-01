package xyz.nuyube.minecraft.disposalchests;

import org.bukkit.Bukkit;
import org.bukkit.Location;

class DisposalChestLocation {
  double x;
  double y;
  double z;
  String world;

  public DisposalChestLocation(double X,double Y, double Z, String World) {
    x = X;
    y= Y;
    z = Z;
    world = World;
  }

  public DisposalChestLocation(Location l) {
    x = l.getX();
    y = l.getY();
    z = l.getZ();
    world = l.getWorld().getName();
  }
  public Location getLocation() {
    return new Location(Bukkit.getWorld(world), x, y, z);
  }
}