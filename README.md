#SQuirreL SQL Client

## Build instructions
SQuirreL SQL uses an Ant based built system, see http://ant.apache.org/. To build SQuirreL you need to download Ant version 1.9.3 or higher from here: http://ant.apache.org/bindownload.cgi

Then open a cmd/shell window and change directory to `<squirrel-git-root>/sql12/`. Form there execute `<ant-home>/bin/ant`. This will generate the directory `<squirrel-git-root>/sql12/output/` where all the build artefacts (installer jars, plainzip packages) are placed.

The build script itself is located at `<squirrel-git-root>/sql12/build.xml` and contains a few more than 200 lines of code.

With the restructuring of the build system went a new directory structure:

  * `<squirrel-git-root>/sql12/core/`: Contains the code of SQuirreL's base application..
  * `<squirrel-git-root>/sql12/plugins/`: Contains all Plugins.
  * `<squirrel-git-root>/sql12/launcher/`: Contains the basis of SQuirreL's start scripts in a way they are needed for building the installer packages.
  *  `<squirrel-git-root>/sql12/plainZipScript/`: Scripts for plain zip distributions. If copied to the launcher directory these scripts are able to start SQuirreL without installation.

## Hints for developers

  * Use `<squirrel-git-root>/sql12/output/dist/` as SQuirreL's home directory (command line: `-home <squirrel-git-root>/sql12/output/dist/`).
  * Put the files in `<squirrel-git-root>/sql12/core/lib` in your classpath
  * In case you want to work with the Look & Feel Plugin put the files contained in `<squirrel-git-root>/sql12/pluins/laf/` and `<squirrel-git-root>/sql12/pluins/skinlf-theme-packs/` in your classpath
  * SQuirreL's source code is in `<squirrel-git-root>/sql12/core/src/` and `<squirrel-git-root>/sql12/plugins/<plugin>/src/`


