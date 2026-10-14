# NoBlocklist
A simple Java agent that prevents the Mojang blocked servers list from loading.
## Supported versions
Currently tested on 1.9.3 and 1.21.11. Should be compatible with versions from 1.9.3-pre2 until 26.3 and further.
## Building
```shell
./gradlew assemble
```
Final agent jar will be available in `build/libs/`
## Usage
Add `-javaagent:/path/to/NoBlocklist-1.0-SNAPSHOT-all.jar` into the JRE arguments of your MC launcher
## Prebuilt JAR
Avaliable in [Releases](https://github.com/MojoLauncher/NoBlocklist/releases)
