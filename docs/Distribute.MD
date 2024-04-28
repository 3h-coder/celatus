## Prerequisites

1. In `./celatus`, run a `maven clean install` command
2. In `./celatus/release/package`, copy the jar with dependencies, the logo.ico and the LICENSE files
3. Run the [jpackage](https://docs.oracle.com/en/java/javase/17/docs/specs/man/jpackage.html) command for the desired platform

Find below one example for each platform

### Windows 

⚠️ The windows installer will fail and stall if the app is already installed with the given version, which is why it should **always** be incremented.

    jpackage --name celatus --vendor 3h-coder --type exe --app-version <version> --icon ./logo.ico --dest ../dist --verbose --input . --main-jar <jarWithDependencies> --about-url https://github.com/3h-coder/celatus/blob/main/README.md --win-menu --win-shortcut-prompt --win-dir-chooser --win-per-user-install --license-file ./LICENSE

### Mac

### Linux