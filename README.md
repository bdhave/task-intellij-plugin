# Task IntelliJ Run Configuration Plugin

![Plugin Downloads](https://img.shields.io/jetbrains/plugin/d/17058-scala.svg)

IntelliJ Run configuration plugin for [Task](https://taskfile.dev/).

![](docs/screenshot.png)

## Requirements

* Install `task`. See [here](https://taskfile.dev/#/installation)
* JDK 11 to build from source

## Install from JetBrains Plugin Marketplace

Install [Taskfile Plugin](https://plugins.jetbrains.com/plugin/17058-taskfile) by searching `Taskfile` in plugin marketplace.

## Install from source

1. Build source
   ```bash
   $ ./gradlew build
   ```
2. Copy `build/distributions/task-intellij-plugin-*.zip` file.
3. In IntelliJ IDEA Preferences -> Plugin -> Install Plugin from Disk -> Select file from step 2. \
    ![](docs/install_from_disk.png) 

## Usage

1. Open 'Run/Debug Configurations'.
2. Add `Taskfile`:
   * Taskfile: Select `Taskfile.yml` file to use.
   * Task: Input task name to run.
