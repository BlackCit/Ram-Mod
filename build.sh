#!/data/data/com.termux/files/usr/bin/bash
set -e
rm -rf build/* ram bundles sprites sprites-override classes*.dex RAM-Android.jar
mkdir -p build sprites sprites-override
javac --release 8 -cp mindustry.jar -d build src/ram/*.java src/ram/*/*.java
d8 --output . $(find build -name "*.class")
cp -r build/ram .
cp -r assets/bundles .
cp -r assets/sprites/* sprites/
cp -r assets/sprites/* sprites-override/
jar cf RAM-Android.jar mod.hjson icon.png classes.dex ram bundles sprites sprites-override
rm -rf sprites sprites-override
cp -v RAM-Android.jar /sdcard/Download/
