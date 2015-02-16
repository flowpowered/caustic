#!/bin/bash

function die_with() { echo "$*" >&2; exit 1; }

echo "Getting current version from pom.xml"
CURRENT_VERSION="`sed -n 's|.*<version>\(.*\)</version>.*|\1|p' pom.xml | awk '{ print $1; exit }'`"  && echo "Current version from pom.xml: $CURRENT_VERSION"

read -p "New version: " NEW_VERSION || die_with "Prompt for new version failed"

if ! echo $NEW_VERSION | grep -i -- '-SNAPSHOT' >/dev/null; then echo "WARNING: changing to a release version!"; fi

echo "Updating the project version in build.gradle, pom.xml and README.md to $NEW_VERSION"
find . \( -name "pom.xml" -o -name "README.md" \) -exec sed -i "s/$CURRENT_VERSION/$NEW_VERSION/g" {} \; die_with "Failed to update the project version!"
chmod -R 644 pom.xml README.md
