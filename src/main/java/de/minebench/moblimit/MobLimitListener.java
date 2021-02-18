package de.minebench.moblimit;

/*
 * MobLimit
 * Copyright (c) 2020 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Mob;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class MobLimitListener implements Listener {

    private final MobLimit plugin;

    public MobLimitListener(MobLimit plugin) {
        this.plugin = plugin;
    }


    /**
     * removes all hostile and ambient mobs when loading a chunk
     *
     * @param event
     */
    @EventHandler
    public void chunkLoad(ChunkLoadEvent event) {
        if (!plugin.isPurging()) {
            return;
        } //purging disabled

        for (org.bukkit.entity.Entity e : event.getChunk().getEntities()) {
            //remove
            if (MobLimit.HOSTILE_MOBS.contains(e.getType().getEntityClass()) || MobLimit.AMBIENT_MOBS.contains(e.getType().getEntityClass())) {
                if (e.getCustomName() == null) {
                    //DEBUG
                    //plugin.getLogger().info("Removing "+e.getClass().toString());
                    e.remove();
                }
            } else {
                //DEBUG
                //plugin.getLogger().info("NOT removing "+e.getClass().toString());
            }
        }
    }

    /**
     * prevent mobs from naturally spawning when above global or chunklimit
     */
    @EventHandler(ignoreCancelled = true)
    public void entitySpawn(PreCreatureSpawnEvent event) {
        if (event.getReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            //global limit
            if (plugin.getGlobalLimit() >= 0 && event.getSpawnLocation().getWorld().getEntityCount() > plugin.getGlobalLimit()) {
                event.setCancelled(true);
                event.setShouldAbortSpawn(true);
                return;
            }

            //chunk limit
            if (plugin.getChunkLimit() >= 0 && event.getSpawnLocation().getChunk().getEntities().length > plugin.getChunkLimit()) {
                event.setCancelled(true);
                event.setShouldAbortSpawn(true);
                return;
            }
        }

        SpawningSettings settings = plugin.getSettings(event.getReason(), event.getType());
        if (settings != null && (settings.getCount() == 0
                || (settings.getChunk() > -1 && event.getSpawnLocation().getChunk().getEntities().length >= settings.getChunk())
                || (settings.getCount() > -1 && settings.getRadius() > 0 && event.getSpawnLocation().getNearbyEntitiesByType(event.getType().getEntityClass(), settings.getRadius()).size() > settings.getCount()))) {
            event.setCancelled(true);
            event.setShouldAbortSpawn(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void entitySpawn(CreatureSpawnEvent event) {
        // dumb mobs
        SpawningSettings spawningSettings = plugin.getSettings(event.getSpawnReason(), event.getEntity().getType());
        if (spawningSettings != null && spawningSettings.isDumb()) {
            if (event.getEntity() instanceof Mob) {
                ((Mob) event.getEntity()).setAware(false);
            } else {
                event.getEntity().setAI(false);
            }
        }
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NETHER_PORTAL && event.getEntity() instanceof PigZombie) {
            PigZombie pigZombie = (PigZombie) event.getEntity();
            pigZombie.setAngry(false);
            // Fix farms
            if (spawningSettings != null && spawningSettings.isDumb()) {
                Block down = event.getLocation().getBlock().getRelative(BlockFace.DOWN);
                int[][] relatives = {{0, 0}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
                for (int[] relative : relatives) {
                    Block rel = down.getRelative(relative[0], 0, relative[1]);
                    if (MobLimit.NONSOLID_SOLID.contains(rel.getType())) {
                        pigZombie.teleport(rel.getLocation().add(0.5, 0.5, 0.5));
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreeding(EntityBreedEvent event) {
        if (event.getBreeder() != null) {
            SpawningSettings settings = plugin.getSettings(CreatureSpawnEvent.SpawnReason.BREEDING, event.getEntity().getType());
            if (settings != null) {
                int count = -1;
                if (settings.getChunk() > -1 && event.getEntity().getLocation().getChunk().getEntities().length >= settings.getChunk()) {
                    count = settings.getChunk();
                } else if (settings.getCount() > -1 && settings.getRadius() > 0
                        && event.getEntity().getLocation().getNearbyEntitiesByType(event.getEntity().getType().getEntityClass(), settings.getRadius()).size() > settings.getCount()) {
                    count = settings.getCount();
                }
                if (count > -1) {
                    event.setCancelled(true);
                    event.setExperience(0);
                    event.getBreeder().sendMessage(plugin.getMessage(
                            event.getBreeder(), "aborted-breeding", "amount", String.valueOf(count)
                    ));
                } else if (settings.isDumb()) {
                    if (event.getEntity() instanceof Mob) {
                        ((Mob) event.getEntity()).setAware(false);
                    } else {
                        event.getEntity().setAI(false);
                    }
                }
            }
        }
    }
}
