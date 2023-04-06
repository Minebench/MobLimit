# MobLimit

Simple Spigot and Paper plugin to limit mob spawning and entity placement depending on the amount of entities around or in a chunk.

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

## Why no TPS settings?
Unlike other popular limiter plugins this plugin does not have option to specify limits depending on the ticks per second
that the server is able to run it. This has multiple reasons:

- The main one being that arbitrarily limiting mobs to a certain amount just because the ticks go down doesn't actually
  solve the underlying problem (too many mobs being spawned to begin with) and you should adjust your spawning ticks as
  well as radius max counts to a value that results in stable gameplay in the first place without tick loss as otherwise
  the server will bounce between spawning too many mobs to not spawning them at all again and again resulting in even less
  stable TPS.
- Arbitrarily limiting spawn rates based on ticks only hides the problem and makes it more difficult for you to figure out
  what actually causes the state which causes your server to lag to begin with. (Most likely too high mob spawning ticks)
- TPS is a terrible metric. You will not notice that your server is unstable if it still barily manages 20 ticks per second.
  MSPT (Milliseconds per tick) is the better metric as it tells you how long one tick actually took to process (up to 50ms
  per tick results in 20 ticks per second) which makes it easier to notice unstable behaviour fast.
- TL;DR: If your server doesn't run at 20tps then you should fix that by adjusting your server settings.

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