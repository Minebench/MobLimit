# MobLimit

Simple Spigot and Paper plugin to limit mob spawning under certain conditions.

Can also remove the AI of mobs while spawning (make them "dumb") while trying to still keep certain mob farm types working.

## Commands
Command                             | Permission                | Description 
------------------------------------|---------------------------|-------------------------------------------------
`/moblimit`                         | `moblimit.command`        | Main plugin command. Get current config.
`/moblimit reload`                  | `moblimit.command.reload` | Reload the plugin config
`/moblimit set chunk <amount>`      | `moblimit.command.set`    | Set chunk amount
`/moblimit set global <amount>`     | `moblimit.command.set`    | Set global amount
`/moblimit set breeding <amount>`   | `moblimit.command.set`    | Set breeding chunk limit
`/moblimit set purge true/false`    | `moblimit.command.set`    | Set whether or not to purge mobs on chunk load


## Config
```yaml
# Limit of entities in a chunk
# -1 disables
chunk: -1
# Amount of entities that can exist in a certain world at the same time
# -1 disables
global: -1
# Purge all monsters/ambient mobs in a chunk on load (ignores named ones)
purge: false
# You can add more reasons here
reasons:
  # The spawn reason
  breeding:
    # Limit if total entity count (not only the spawned mob) in chunk is above this value
    chunklimit: 50
  natural:
    # The type of the entity that is spawning
    pillager:
      # How many of that entity can be in the radius
      count: 4
      # The radius to check for
      radius: 16
  nether_portal:
    pig_zombie:
      count: 50
      radius: 64
      # Whether or not to disable the AI
      dumb: false
  # Spawn reasons don't need an entity type:
  spawner:
    count: 20
    radius: 50
  village_defense:
    count: 1
    radius: 16
```

## Downloads
This plugin can currently be downloaded from the [Minebench.de Jenkins server](https://ci.minebench.de/job/MobLimit/).

## License
This plugin is licensed under [GPLv3](https://github.com/Minebench/MobLimit/blob/master/LICENSE).

```
 MobLimit
 Copyright (c) 2020 Max Lee aka Phoenix616 (max@themoep.de)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
```