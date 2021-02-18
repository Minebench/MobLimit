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

import de.themoep.minedown.MineDown;
import de.themoep.utils.lang.bukkit.LanguageManager;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public final class MobLimit extends JavaPlugin {
    static final Set<Material> NONSOLID_SOLID = EnumSet.of(
            Material.LADDER,
            Material.OAK_SIGN,
            Material.ACACIA_SIGN,
            Material.BIRCH_SIGN,
            Material.DARK_OAK_SIGN,
            Material.JUNGLE_SIGN,
            Material.SPRUCE_SIGN,
            Material.OAK_WALL_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.DARK_OAK_WALL_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.SPRUCE_WALL_SIGN,
            Material.SPRUCE_TRAPDOOR,
            Material.OAK_TRAPDOOR,
            Material.SPRUCE_TRAPDOOR,
            Material.BIRCH_TRAPDOOR,
            Material.DARK_OAK_TRAPDOOR,
            Material.JUNGLE_TRAPDOOR
    );
    static final Set<Class<? extends Entity>> HOSTILE_MOBS = new HashSet<>(Arrays.asList(
            EntityType.MAGMA_CUBE.getEntityClass(),
            EntityType.SLIME.getEntityClass()
    ));
    static final Set<Class<? extends Entity>> AMBIENT_MOBS = new HashSet<>();
    static final Set<Class<? extends Entity>> WATER_MOBS = new HashSet<>();
    static final Set<Class<? extends Entity>> PEACEFUL_MOBS = new HashSet<>(Arrays.asList(
            EntityType.VILLAGER.getEntityClass()
    ));

    static {
        NONSOLID_SOLID.addAll(Tag.TRAPDOORS.getValues());

        for (EntityType type : EntityType.values()) {
            if (type.getEntityClass() == null) {
                // ignore
            } else if (Animals.class.isAssignableFrom(type.getEntityClass())) {
                PEACEFUL_MOBS.add(type.getEntityClass());
            } else if (Ambient.class.isAssignableFrom(type.getEntityClass())) {
                AMBIENT_MOBS.add(type.getEntityClass());
            } else if (Monster.class.isAssignableFrom(type.getEntityClass())) {
                HOSTILE_MOBS.add(type.getEntityClass());
            } else if (WaterMob.class.isAssignableFrom(type.getEntityClass())) {
                WATER_MOBS.add(type.getEntityClass());
            }
        }
    }

    private int chunkLimit = 20;
    private int globalLimit = 2500;
    private boolean purge = true;

    private int breedingLimit = 0;

    private Map<CreatureSpawnEvent.SpawnReason, ReasonSettings> spawningSettings;

    private LanguageManager lang;

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfig();
        lang = new LanguageManager(this, "en");
        getCommand("moblimit").setExecutor(new MobLimitCommand(this));
        getServer().getPluginManager().registerEvents(new MobLimitListener(this), this);

        checkSpawnTick("monster", getServer().getTicksPerMonsterSpawns(), 10);
        checkSpawnTick("animals", getServer().getTicksPerAnimalSpawns(), 400);
        checkSpawnTick("water-animals", getServer().getTicksPerWaterSpawns(), 40);
        checkSpawnTick("water-ambient", getServer().getTicksPerWaterAmbientSpawns(), 40);
        checkSpawnTick("ambient", getServer().getTicksPerAmbientSpawns(), 40);
    }

    private void checkSpawnTick(String type, int ticks, int target) {
        if (ticks < target) {
            getLogger().log(Level.WARNING, "Your " + type + " spawning ticks are pretty low (" + ticks + "). Set ticks-per." + type + " to " + target + " in your bukkit.yml to improve performance!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadConfig() {
        saveDefaultConfig();
        reloadConfig();

        chunkLimit = getConfig().getInt("chunk");
        globalLimit = getConfig().getInt("global");

        breedingLimit = getConfig().getInt("reasons.breeding.chunklimit");

        purge = getConfig().getBoolean("purge");

        spawningSettings = new EnumMap<>(CreatureSpawnEvent.SpawnReason.class);
        for (CreatureSpawnEvent.SpawnReason spawnReason : CreatureSpawnEvent.SpawnReason.values()) {
            if (getConfig().contains(spawnReason.name().toLowerCase()) && getConfig().isConfigurationSection("reasons." + spawnReason.name().toLowerCase())) {
                spawningSettings.put(spawnReason, new ReasonSettings(getConfig().getConfigurationSection("reasons." + spawnReason.name().toLowerCase())));
            }
        }
        getLogger().log(Level.INFO, String.join("\n", getCurrentStateMessage()));

    }

    SpawningSettings getSettings(CreatureSpawnEvent.SpawnReason reason, EntityType type) {
        ReasonSettings reasonSettings = spawningSettings.get(reason);
        if (reasonSettings != null) {
            return reasonSettings.getSpawningSettings().getOrDefault(type, reasonSettings);
        }
        return null;
    }

    public void setGlobalLimit(int globalLimit) {
        this.globalLimit = globalLimit;
        getConfig().set("global", globalLimit);
        saveConfig();
    }

    public int getGlobalLimit() {
        return globalLimit;
    }

    public MobLimit getMobLimit() {
        return this;
    }

    public void setChunkLimit(int chunkLimit) {
        this.chunkLimit = chunkLimit;
        getConfig().set("chunk", chunkLimit);
        saveConfig();
    }

    public int getChunkLimit() {
        return chunkLimit;
    }

    public void setPurging(boolean purge) {
        this.purge = purge;
        getConfig().set("purge", purge);
        saveConfig();
    }

    public boolean isPurging() {
        return this.purge;
    }

    public Map<CreatureSpawnEvent.SpawnReason, ReasonSettings> getSpawningSettings() {
        return spawningSettings;
    }

    public List<String> getCurrentStateMessage() {
        // TODO: use language config :eyes:
        // build message
        List<String> message = new ArrayList<>();
        Collections.addAll(message,
                ChatColor.BOLD + "Moblimits:",
                "Global limit: "+(getGlobalLimit() == 0 ? "disabled":getGlobalLimit()),
                "Chunk limit: "+(getChunkLimit() == 0 ? "disabled":getChunkLimit()),
                "Chunk purging: "+ isPurging()
        );
        for (Map.Entry<CreatureSpawnEvent.SpawnReason, MobLimit.ReasonSettings> entry : getSpawningSettings().entrySet()) {
            List<String> reasonValues = new ArrayList<>();
            if (entry.getValue().getCount() > 0) {
                reasonValues.add("  Count: " + entry.getValue().getCount());
            }
            if (entry.getValue().getRadius() > 0) {
                reasonValues.add("  Radius: " + entry.getValue().getRadius());
            }
            if (entry.getValue().isDumb()) {
                reasonValues.add("  Dumb: " + entry.getValue().isDumb());
            }
            for (Map.Entry<EntityType, SpawningSettings> settingsEntry : entry.getValue().getSpawningSettings().entrySet()) {
                List<String> typeReasonValues = new ArrayList<>();
                if (settingsEntry.getValue().getCount() > 0) {
                    typeReasonValues.add("    Count: " + settingsEntry.getValue().getCount());
                }
                if (settingsEntry.getValue().getRadius() > 0) {
                    typeReasonValues.add("    Radius: " + settingsEntry.getValue().getRadius());
                }
                if (settingsEntry.getValue().isDumb()) {
                    typeReasonValues.add("    Dumb: " + settingsEntry.getValue().isDumb());
                }
                if (!typeReasonValues.isEmpty()) {
                    reasonValues.add("  " + settingsEntry.getKey().name() + ":");
                    reasonValues.addAll(typeReasonValues);
                }
            }
            if (!reasonValues.isEmpty()) {
                message.add(entry.getKey().name() + ":");
                message.addAll(reasonValues);
            }
        }
        return message;
    }

    public BaseComponent[] getMessage(CommandSender sender, String key, String... replacements) {
        return MineDown.parse(lang.getConfig(sender).get(key), replacements);
    }

    class ReasonSettings extends SpawningSettings {

        private Map<EntityType, SpawningSettings> spawningSettings = new EnumMap<>(EntityType.class);

        public ReasonSettings(ConfigurationSection section) {
            super(section);
            for (String key : section.getKeys(false)) {
                if (section.isConfigurationSection(key) && !key.equals("count") && !key.equals("radius") && !key.equals("dumb")) {
                    try {
                        spawningSettings.put(EntityType.valueOf(key.toUpperCase()), new SpawningSettings(section.getConfigurationSection(key)));
                    } catch (IllegalArgumentException e) {
                        getLogger().log(Level.WARNING, key + " is not a valid entity type in " + section.getCurrentPath());
                    }
                }
            }
        }

        public Map<EntityType, SpawningSettings> getSpawningSettings() {
            return spawningSettings;
        }
    }

}
