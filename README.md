# IntelliJ-MMT

An IntelliJ-Plugin for MMT (under development).

**Plugin on IntelliJ Marketplace: <https://plugins.jetbrains.com/plugin/11450-mmt/>**

**Documentation for Endusers: <https://uniformal.github.io//doc/applications/intellij/index.html#usage-of-the-sidekick>**

## Development for developers

### Initial building

1. Clone the repository
2. Open the cloned repository in IntelliJ as an IntelliJ project (just open, do not import!)

3. Confirm to autoimport the Gradle setup. If not prompted, open `build.gradle` and click the appearing import button ([screenshot](./docs/how-to-import-gradle.png))

4. If asked, setup the Scala SDK. Preferably to the same version as used in MMT: go to [UniFormal/MMT src/build.sbt @ devel](https://github.com/UniFormal/MMT/blob/devel/src/build.sbt#L44), search for "scalaVersion". Currently, it's 2.12.9. You may need to download that first in the dialog IntelliJ shows you.

5. The plugin uses GrammarKit to specify a grammar. You still need to generate the resulting lexer and parser:

   1. Generate the things, see [`./docs/how-to-generate-grammar-things.png`](./docs/how-to-generate-grammar-things.png)
   2. Fix the generated `MMTLexer`: [`./docs/how-to-fix-generated-grammar-thing.png`](./docs/how-to-fix-generated-grammar-thing.png)
  
### Testing (manual)

See [`./docs/how-to-test-plugin.png`](./docs/how-to-test-plugin.png).

### Preparing and Making a new Release

1. Change `MMT.requiredVersion` in `Plugin.scala` accordingly to required MMT version.

2. Change release notes section in `build.gradle` by completely overwriting the old release notes

    The old ones are still saved with the previously published plugin on the IntelliJ plugin marketplace.
    
3. Copy `gradle.properties.template` in the same folder to `gradle.properties` and configure its contents.

4. Run `publishPlugin` Gradle task, e.g. from within the IntelliJ Gradle Plugin GUI. 