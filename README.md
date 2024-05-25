## Issuechecker
___

[![Build Project](https://github.com/usefulness/issuechecker/actions/workflows/on-pull-request.yml/badge.svg?branch=master)](https://github.com/usefulness/issuechecker/actions/workflows/on-pull-request.yml)
&nbsp;[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

![Maven Central](https://img.shields.io/maven-central/v/com.github.usefulness/issuechecker?style=plastic)


## Purpose
In a project that reached maintenance phase there usually are multiple workarounds left to fix _in the future_.  
This tools helps to find all links and check if they have been fixed already.

Supported issue trackers:
- ✅ Jetbrains Youtrack  
- ✅ Github Issues
- ❌ [~Google IssueTracker~](https://issuetracker.google.com/issues/171647219)

## Usage
This repository contains a raw tool written kotlin, available on MavenCentral repository. 
Additionally, it exposes a fat Jar which serves as a CLI. 

The core dependency is available under:
```groovy
repositories.mavenCentral()

dependencies {
    implementation("com.github.usefulness:issuechecker:x.y.z")
}
```

### Common application
- **CLI** - A `jar` file available directly on Github Package Repository - [download page](https://github.com/usefulness/issuechecker/packages/641930) 
- **Gradle Plugin** - `com.starter.quality` plugin runs the tool automatically under `issueLinksReport` name - [source](https://github.com/usefulness/project-starter) 
- **Github Action** - ⏳ In progress ⏳ 


#### CLI 
```text
Usage: issue-checker-cli [OPTIONS]

Options:
  -s, --src, --source TEXT  Source file filter, i.e. `--source **.java` to
                            find all java files
  --github-token TEXT       Github token to check private issues
  -d, --debug               Enables additional logging
  --stacktrace              Shows additional stacktrace in case of failure
  --dry-run                 Only finds all links, without checking them
  -h, --help                Show this message and exit
```

Sample commands:

- Find all links in files with `.kt` extension:  
```bash
java -jar issue-checker-cli.jar --dry-run --source "**.kt"
```

- Find and check all links in files with `.java` extension:  
```bash
java -jar issue-checker-cli.jar --source "**.java"
```
