# Metrolist Dev Guide

This file outlines the process of setting up a local dev environment for Metrolist.

## Prerequisites

- Java JDK 21
- Android Studio (or VSCode with Kotlin extensions)
- CMake 3.3.10
- Android NDK 27.0.12077973
- Go 1.20+
- protoc
- protoc-gen-go
- android tools (for keytool)

## Basic setup

```bash
git clone https://github.com/MetrolistGroup/Metrolist
cd Metrolist
git submodule update --init --recursive
cd app
bash generate_proto.sh
cd src/main/cpp/vibrafp/third_party && ./build-fftw-android.sh --ndk ~/Android/Sdk/ndk/27.0.12077973 --out ./fftw-android --version 3.3.10 --api 26
cd ../../../../../
[ ! -f "persistent-debug.keystore" ] && keytool -genkeypair -v -keystore persistent-debug.keystore -storepass android -keypass android -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US" || echo "Keystore already exists."
cd ..
./gradlew :app:assembleuniversalFossDebug
ls app/build/outputs/apk/universalFoss/debug/app-universal-foss-debug.apk
```
