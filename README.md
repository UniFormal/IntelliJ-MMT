# IntelliJ-MMT
An IntelliJ-Plugin for MMT (under development)

## Installation for endusers
- The latest version of this plugin can be installed from *IntelliJ's marketplace* (File -> Settings -> Plugins)
- Alternatively, you can install the plugin by extracting `build/distributions/MMT-<version>.zip` into the `plugins`-folder of your IntelliJ-installation (which might be something like `/usr/share/idea/plugins` or `/opt/share/intellij-idea-ultimate-edition`)

## Usage for endusers
- Set up a new MathHub-project in IntelliJ (this will contain all your MMT archives, a .msl-file and an mmtrc). You may choose a preexisting folder with MMT archives; these will **not** be overwritten.
- In the project-view on the left, select the *MathHub* view to get an overview over all available MMT archives on 
[MathHub](http://gl.mathhub.info). You can *git clone* them via right-clicking an archive and choosing *Install archive*.
- By default, the Plugin does **not** type check the terms of an open *mmt*-file, since doing so is computationally expensive and inconvenvient for the user. Type checking can be easily activated and deactivated in the *Errors* panel of the MMT tool window (View -> Tool Windows -> MMT)
- The *Document Tree* (on the left border of the IntelliJ-Window) only shows the syntax tree of the document that has been type checked last. To see the tree for the currently opened document, check the *Type Checking* checkbox in the *Errors* panel. Automatically navigating the syntax tree by caret position in the document can be turned on and off with the corresponding check box at the top of the *Document Tree* panel.

## Development for developers

### Initial building

1. Clone the repository
2. Open the cloned repository in IntelliJ as an IntelliJ project (just open, no import!)
3. Let the Gradle autoimporter do its work
4. If asked, setup the Scala SDK. Preferably to the same version as used in MMT: go to [UniFormal/MMT src/build.sbt @ devel](https://github.com/UniFormal/MMT/blob/devel/src/build.sbt#L44), search for "scalaVersion". Currently, it's 2.12.9. You may need to download that first in the dialog IntelliJ shows you.
5. The plugin uses GrammarKit to specify a grammar. You still need to generate the resulting lexer and parser:

  1. Generate the things, see [`./docs/how-to-generate-grammar-things.png`](./docs/how-to-generate-grammar-things.png)
  2. Fix the generated `MMTLexer`: [`./docs/how-to-fix-generated-grammar-thing.png`](./docs/how-to-fix-generated-grammar-thing.png)
  
### Testing (manual)

See [`./docs/how-to-test-plugin.png`](./docs/how-to-test-plugin.png).