package de.minebench.moblimit;

/*
 * MobLimit
 * Copyright (c) 2020 Max Lee aka Phoenix616 (mail@moep.tv)
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

import org.bukkit.configuration.ConfigurationSection;

class SpawningSettings {
    private final int count;
    private final int radius;
    private final boolean dumb;

    public SpawningSettings(int count, int radius, boolean dumb) {
        this.count = count;
        this.radius = radius;
        this.dumb = dumb;
    }

    public SpawningSettings(ConfigurationSection config) {
        this(config.getInt("count", 0), config.getInt("radius", 0), config.getBoolean("dumb", false));
    }

    public int getCount() {
        return count;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isDumb() {
        return dumb;
    }
}
