# AGS-GC

This is a fork of Grasscutter, a server emulator for a certain anime game written in Java, with its own direction and goal.
Currently, we are mostly focussing on the developer experience and stability, so while everyone is free to play around with it,
normal end-users are currently not our main demographic yet, so the work put into streamlining it for normal users is limited.

## Dependencies
### AGS Modules/Libraries
All of those modules are loaded via gradle and mvn, if you want to work on them locally and test them, build them via the `mavenPublishLocal` task.
The public releases and snapshots of them are hosted on the [AGS maven repo](https://mvn.animegameservers.org/#/).

[AnimeGameMultiProto](https://github.com/Anime-Game-Servers/anime-game-multi-proto):
* Abstracts the proto parsing to make supporting multiple versions easier.
* Currently only one version is supported at a time
  * You can change it by changing the last digit of the version for `gi-jvm` in the build.grade file, e.g. 0.2.32 for 3.2 and 0.2.53 for 5.3
* Long term goal is to have a single version which supports all known version

[AnimeGameLua](https://github.com/Anime-Game-Servers/AnimeGamesLua):
* Handles the Lua execution and extracting info out of it.
* Also defines the interface for the server and lua to interact with each other.

[AnimeGameDataModels](https://github.com/Anime-Game-Servers/AnimeGameDataModels):
* Will be used for parsing Excel and Binout resources.
* Long term it will also handle patching resources.

[AnimeGameCore](https://github.com/Hartie95/Core)
* This contains some shared code, annotations and models for the AGS libraries, including the game version definitions.

### Other Libraries
[grasskcpper (fork)](https://github.com/Hartie95/grasskcpper):
This library is used for handling the KCP communication. The fork in use adds support for newer clients, while also keeping support for older versions.

### Required Resources
* Deobfuscated Resources for your targeted game version. These are created by extracting and parsing data from the client so we don't provide a link.
They should contain:
  * Lua scripts for spawning entities and open world/level logic
  * ExcelBinOutput containing simple data that defines available scenes, quests, activities, characters, items and more
  * BinOutput containing more complex data that defines the Abilities and base stats of monster, avatars and gadgets, quest and talk definitions, routes and more
  * TextMap defining the langauge Strings for each string used by the client
    * used for generating the handbook and in other cases where we display an entities, items or other type of name
* [CustomGCResources](https://github.com/Anime-Game-Servers/CustomGCResources) for community created resources
  * This contains data that the client is missing and open replacements for some client resources.

### Required software
* Java 17+ Runtime
* MongoDB Server

### Client requirements
* Proxy (e.g. mitmdump) or patch (e.g. mhypbase or hk4e-patch-universal) to redirect sdk and dispatch requests
* For full login support, or the ability to connect with 2.8+ clients a patch to replace the public rsa keys

# Building
# TODO more detail
To build a jar, just run the `shadowJar` task. It will create a fat jar, where both available lua engine can be used properly, just using jar will not merge some resources and break the one added first during jar creation.

## Our Goals
* Research in understanding how the game works and how it evolves
* Preserve old versions
  * Especially for version specific content, like older events
* Allow free modding in the world of Teyvat, without affecting the experience on official
  * e.g. increasing the difficulty for quests, improving qol, replacing or adding character models, adding quests
