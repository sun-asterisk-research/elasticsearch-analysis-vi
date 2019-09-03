# Vietnamese Analysis Plugin for Elasticsearch

Vietnamese Analysis plugin integrates Vietnamese language analysis into Elasticsearch.
The plugin provides the following functions:

Analyzer: `vi_analyzer`. Tokenizer: `vi_tokenizer`. Filter: `vi_stop`. The `vi_analyzer` itself is composed of the `vi_tokenizer`, the `lowercase` and the `vi_stop` filter.

The tokenizer uses [coccoc-tokenizer](https://github.com/coccoc/coccoc-tokenizer) for tokenization.

## Installation

Choose a version from the [releases](https://github.com/sun-asterisk-research/elasticsearch-analysis-vi/releases) page to install:

```sh
elasticsearch-plugin install https://github.com/sun-asterisk-research/elasticsearch-analysis-vi/releases/download/<release>/<bundle>
```

Or [build from source](#build-from-source) and install from a plugin bundle.

```sh
elasticsearch-plugin instal file:///path/to/plugin
```

## Supported versions

| Branch | Elasticsearch version |
|--------|-----------------------|
| master | 7.x                   |

## Build from source

You need the following build dependencies: `JDK`, `make`, `cmake`, `libstdc++`. At least JDK 11 is required. Beware of your `libstdc++` version. If you build on a version too new, it will not work on older systems.

First update the git submodules:

```sh
git submodule update --init
```

Build and bundle the plugin:

```sh
./gradlew assemble
```

To build for a different elasticsearch version, add `-PelasticsearchVersion=<version>` to your build command. Also note the [branch and supported versions](#supported-versions). For example, to build for Elasticsearch 7.3.1:

```sh
./gradlew assemble -PelasticsearchVersion=7.3.1
```

To run tests:

```sh
./gradlew check
```
