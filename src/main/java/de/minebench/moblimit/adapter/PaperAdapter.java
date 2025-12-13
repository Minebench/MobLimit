package de.minebench.moblimit.adapter;

/*
 * moblimit
 * Copyright (c) 2021 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import de.minebench.moblimit.MobLimit;
import de.minebench.moblimit.SpawningSettings;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Collection;

public class PaperAdapter implements Listener, PlatformAdapter {
    private MobLimit plugin;

    public PaperAdapter(MobLimit plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void entitySpawn(PreCreatureSpawnEvent event) {
        MobLimit.PRE_SPAWN_HANDLED.put(event.getReason(), event.getType());
        if (event.getReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            //global limit
            if (plugin.getGlobalLimit() >= 0 && event.getSpawnLocation().getWorld().getEntityCount() > plugin.getGlobalLimit()) {
                event.setCancelled(true);
                event.setShouldAbortSpawn(true);
                if (plugin.isDebug()) plugin.logDebug("Cancelled PreCreatureSpawnEvent " + event.getReason() + " for " + event.getType() + " at " + event.getSpawnLocation() + " due to global limit: " + event.getSpawnLocation().getWorld().getEntityCount() + " > " + plugin.getGlobalLimit());
                return;
            }

            //chunk limit
            if (plugin.getChunkLimit() >= 0 && event.getSpawnLocation().getChunk().getEntities().length > plugin.getChunkLimit()) {
                event.setCancelled(true);
                event.setShouldAbortSpawn(true);
                if (plugin.isDebug()) plugin.logDebug("Cancelled CreatureSpawnEvent " + event.getReason() + " for " + event.getType() + " at " + event.getSpawnLocation() + " due to chunk limit: " + event.getSpawnLocation().getChunk().getEntities().length + " > " + plugin.getChunkLimit());
                return;
            }
        }

        SpawningSettings settings = plugin.getSettings(event.getReason(), event.getType());
        if (settings != null) {
            if (settings.getCount() == 0) {
                event.setCancelled(true);
                event.setShouldAbortSpawn(true);
                if (plugin.isDebug()) plugin.logDebug("Cancelled PreCreatureSpawnEvent " + event.getReason() + " for " + event.getType() + " at " + event.getSpawnLocation() + " due to being limited to 0 in the reason settings");
            } else if (settings.getChunk() > -1 && event.getSpawnLocation().getChunk().getEntities().length >= settings.getChunk()) {
                event.setCancelled(true);
                event.setShouldAbortSpawn(true);
                if (plugin.isDebug()) plugin.logDebug("Cancelled PreCreatureSpawnEvent " + event.getReason() + " for " + event.getType() + " at " + event.getSpawnLocation() + " due to chunk limit: " + event.getSpawnLocation().getChunk().getEntities().length + " > " + plugin.getChunkLimit());
            } else if (settings.getCount() > -1 && settings.getRadius() > 0) {
                int radiusCount = getNearbyEntitiesByType(event.getSpawnLocation(), event.getType().getEntityClass(), settings.getRadius()).size();
                if (radiusCount > settings.getCount()) {
                    event.setCancelled(true);
                    event.setShouldAbortSpawn(true);
                    if (plugin.isDebug()) plugin.logDebug("Cancelled PreCreatureSpawnEvent " + event.getReason() + " for " + event.getType() + " at " + event.getSpawnLocation() + " due to radius " + settings.getRadius() + " limit: " + radiusCount + " > " + settings.getCount());
                }
            }
        }
    }

    @Override
    public Collection<? extends Entity> getNearbyEntitiesByType(Location location, Class<? extends Entity> entityClass, int radius) {
        return location.getNearbyEntitiesByType(entityClass, radius);
    }
}
