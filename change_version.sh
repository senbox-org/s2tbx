#!/bin/bash

die() {
message=$1

echo $message
exit 1
}


[ -f "pom.xml" ] || die "The script should be run from the s2tbx repository path"

OLD_VERSION=$1
NEW_VERSION=$2


# Update all pom.xml
find . -name "pom.xml" | xargs sed -i "s@<version>$OLD_VERSION</version>@<version>$NEW_VERSION</version>@g"

# Adapters have a harcoded version for now
if echo $NEW_VERSION | grep SNAPSHOT ; then
  ADAPTER_VERSION=$(echo $NEW_VERSION | cut -d "-" -f 1).0
else
  ADAPTER_VERSION=$NEW_VERSION
fi

find s2tbx-sta-adapters -name "manifest.mf"  | xargs sed -i "s@OpenIDE-Module-Specification-Version.*@OpenIDE-Module-Specification-Version: $ADAPTER_VERSION@"
find s2tbx-sta-adapters -name "manifest.mf"  | xargs sed -i "s@OpenIDE-Module-Implementation-Version.*@OpenIDE-Module-Implementation-Version: $ADAPTER_VERSION@"

# Adapters also have a descriptor.xml with a harcoded version...
find s2tbx-sta-adapters -name "descriptor.xml" | xargs sed -i "s@<version>.*</version>@<version>$ADAPTER_VERSION</version>@"

