package xyz.nuyube.minecraft.disposalchests;

import java.time.LocalDateTime;

import org.bukkit.Chunk;
import org.bukkit.World;

class ChunkLocation {
  int x;
  int z;
  World world;
  LocalDateTime unloaded;

  public ChunkLocation(Chunk c) {
    x = c.getX();
    z = c.getZ();
    world = c.getWorld();
    unloaded = LocalDateTime.now();
  }
}