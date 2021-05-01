package xyz.nuyube.minecraft.disposalchests;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.bukkit.Chunk;

class RecentlyLoadedChunks {

static   ArrayList<ChunkLocation> chunky = new ArrayList<ChunkLocation>();

  public static void Add(Chunk c) {
    ChunkLocation location = new ChunkLocation(c);
    chunky.add(location);
  }

  public static boolean Contains(Chunk c) {
     Tick();
    for(ChunkLocation l : chunky) {
      if(l.x == c.getX() && l.z == c.getZ() && l.world == c.getWorld()) return true;
    }
    return false;
  }

  public static void Tick() {
    for (int i = 0; i < chunky.size(); i++) {
      if (chunky.get(i).unloaded.plusSeconds(4).isBefore(LocalDateTime.now())) {
        chunky.remove(i);
        i--;
      }
    }
  }
}