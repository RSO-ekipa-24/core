# core

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/core-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

## Branching Strategy

- main: The production-ready branch.
- dev: The integration branch for features and fixes, often considered the "next release" branch.
- feature/: Branches for developing new features. These branches are created from dev and merged back into dev when the feature is complete.
- bugfix/: Branches for fixing bugs in the dev branch.
- release/: Branches for preparing a new production release. These branches allow for last-minute fixes and preparing release notes.
- hotfix/: Branches for fixing critical issues in the main branch. These are created from main and merged back into both main and dev.

## How to run this shi - simplified

- Clone this.
- If you're in intellij, you can press the Maven icon on the right, press 'Execute Maven Goal' at the top, put in 'mvn clean install -Dquarkus.container-image.build=true -Dquarkus.container-image.name=core -Dquarkus.container-image.tag=latest' and enter to build image.
- If you're not in intellij you need to use ./mvnw package in terminal.
- Go into docker-compose/docker-compose.yml
- Run it.

## How to use this shi - simplified

- There is a POST endpoint at http://localhost:6969/users/, which is to only be called once for every user when you want to register them on core. It requires no body, just a keycloak bearer token.
- For information on other endpoints (what they require, what they return) we need to have a space to document them all. Find one and contact me so I can write it there, I dont think that belongs here.

Yours truly, Antišek.