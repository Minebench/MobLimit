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

import de.minebench.moblimit.MobLimit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Collection;

public class SpigotAdapter implements PlatformAdapter {
    private final MobLimit plugin;

    public SpigotAdapter(MobLimit plugin) {
        this.plugin = plugin;
    }

    @Override
    public Collection<? extends Entity> getNearbyEntitiesByType(Location location, Class<? extends Entity> entityClass, int radius) {
        return location.getWorld().getNearbyEntities(location, radius, radius, radius, e -> entityClass.isAssignableFrom(e.getClass()));
    }
}
