#!/bin/sh

set -eu

BASE_DIR=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
WRAPPER_JAR="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"
export MAVEN_USER_HOME="$BASE_DIR/.mvn"
JAVA_CMD="${JAVA_HOME:-}/bin/java"

if [ ! -x "$JAVA_CMD" ]; then
  JAVA_CMD="java"
fi

exec "$JAVA_CMD" \
  "-Dmaven.multiModuleProjectDirectory=$BASE_DIR" \
  -classpath "$WRAPPER_JAR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
