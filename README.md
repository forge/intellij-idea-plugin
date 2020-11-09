# JBoss Forge IntelliJ IDEA Plugin

Published in http://plugins.jetbrains.com/plugin/7515

Provides support for [JBoss Forge](https://forge.jboss.org/) commands and wizards in IntelliJ IDEA.

To show a list of commands press `Ctrl+Alt+4`. On MacOSX, use `Command + Option + 4`.

## Installation

- Using IDE built-in plugin system:

  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "JBoss Forge IDEA Plugin"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/%REPOSITORY%/releases/latest) and install it manually using
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---

This plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template

### JDK Version

This plugin should work with *JDK 8* and above.

### Building

This plugin uses Gradle as the build tool and the Intellij Gradle plugin.

### Debugging

To debug the plugin in IntelliJ IDEA, you need to install `Intellij plugin development with Maven` plugin first.
That will allow you to run a second instance of the IDE, in which you can test and debug the plugin.

After installing this plugin, you can add a plugin run/debug configuration.
To do that, go to `Run -> Edit Configurations...` and add _Plugin_ configuration.

This configuration should start a new IntelliJ window. In case the plugin is not installed, you will need
to install it manually in IDE settings and run it again.
