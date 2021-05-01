package xyz.nuyube.minecraft.disposalchests;

import org.bukkit.Location;

class DisposalChestLocation {
  double x;
  double z;
  String world;

  public DisposalChestLocation(double X, double Z, String World) {
    x = X;
    z = Z;
    world = World;
  }

  public DisposalChestLocation(Location l) {
    x = l.getX();
    z = l.getY();
    world = l.getWorld().getName();
  }
}