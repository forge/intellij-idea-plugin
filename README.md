# JBoss Forge IntelliJ IDEA Plugin

Published in http://plugins.jetbrains.com/plugin/7515

Provides support for [JBoss Forge](https://forge.jboss.org/) commands and wizards in IntelliJ IDEA.

To show a list of commands press `Ctrl+Alt+4`. On MacOSX, use `Command + Option + 4`.

## Installation

- Using IDE built-in plugin system:

  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "JBoss Forge IDEA Plugin"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/forge/intellij-idea-plugin/releases/latest) and install it manually using
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---

This plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template

### JDK Version

This plugin should work with *JDK 11* and above.

### Building

This plugin uses Gradle as the build tool and the Intellij Gradle plugin.

### Releasing

The release is performed using GitHub Actions. Once any changes are committed to the master branch, [a changelog and a release draft are created](https://github.com/forge/intellij-idea-plugin/releases).
Once the release is marked as published, the plugin is published to the [Intellij Plugins repository](https://plugins.jetbrains.com/plugin/7515-jboss-forge-idea-plugin/)
