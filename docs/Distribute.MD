## Prerequisites



1. In `./celatus`, run a `maven clean install` command
2. In `./celatus/release/package`, copy the jar with dependencies, the logo.ico and the LICENSE files
3. Run the [jpackage](https://docs.oracle.com/en/java/javase/17/docs/specs/man/jpackage.html) command for the desired platform

Note that the installation is user scoped, not system scoped.

Find below one example for each platform.

## Windows 

⚠️ The windows installer will fail and stall if the app is already installed with the given version, which is why it should **always** be incremented.

    jpackage --type exe --name Celatus --vendor 3h-coder --app-version <version> --icon ./logo.ico --dest ../dist --verbose --input . --main-jar <jarWithDependencies> --about-url https://github.com/3h-coder/celatus/blob/main/README.md --license-file ./LICENSE --win-menu --win-shortcut-prompt --win-dir-chooser --win-per-user-install 

## Mac

*To Be Completed*

## Linux

*Debian based distributions (debian, ubuntu, mint etc.)*

    jpackage --type deb --name Celatus --vendor 3h-coder --app-version <version> --icon ./logo.png --dest ../dist --verbose --input . --main-jar <jarWithDependencies> --about-url https://github.com/3h-coder/celatus/blob/main/README.md --license-file ./LICENSE --linux-shortcut --linux-menu-group Utilities


*Red-hat based distributions (fedora, centOs, rhel, etc.)*

    jpackage --type rpm --name Celatus --vendor 3h-coder --app-version <version> --icon ./logo.png --dest ../dist --verbose --input . --main-jar <jarWithDependencies> --about-url https://github.com/3h-coder/celatus/blob/main/README.md --license-file ./LICENSE --linux-shortcut --linux-menu-group Utilities