[![Build](https://github.com/exceljava/com4j/actions/workflows/build.yml/badge.svg)](https://github.com/exceljava/com4j/actions/workflows/build.yml)
[![](https://jitpack.io/v/exceljava/com4j.svg)](https://jitpack.io/#exceljava/com4j)

About
=====

This is a fork of [kohsuke/com4j](https://github.com/kohsuke/com4j) and contains some changes necessary for calling the Excel COM API from Java. All changes are raised as a PR in the original repo, as the intention is that this repo is only needed temporarily.

Using com4j
===========

[Download com4j](https://github.com/exceljava/com4j/releases) or [access it from jitpack.io](https://jitpack.io/#exceljava/com4j)

To add to a Maven project, add the following to your pom.xml (using the latest release tag for the `version`)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.exceljava.com4j</groupId>
    <artifactId>com4j</artifactId>
    <version>release-20190528</version>
</dependency>
```

Building com4j
==============

com4j is divided into two parts, native code and Java. The native side (`com4j.dll`, x86 and x64) is built with CMake, but the *build* of the Java side does not build the native side - it just packages up the **prebuilt DLLs already committed to this repo**, under `bin/x86/<mode>/com4j.dll` and `bin/x64/<mode>/com4j.dll` (`<mode>` is `Release` or `Debug`; the Java build uses `Release` by default).

This means a plain `mvn package` from the repository root works anywhere with just a JDK and Maven - no C++ toolchain required - which is what lets [jitpack.io](https://jitpack.io/#exceljava/com4j) build this project: JitPack builds from source on its own (Linux) servers, with no way to run a Windows/MSVC build, so it can only ever consume the committed DLLs, never rebuild them itself.

Rebuilding the native DLLs
--------------------------

You only need to do this if you've changed code under `native/`. It's opt-in - pass `-DbuildNative=true` - and requires:

- Check out the git submodules that are linked (`git submodule update --init`).
- [CMake](https://cmake.org/) (3.20+), on your `PATH`.
- Visual Studio 2022 (or newer) with the "Desktop development with C++" workload, including the optional **C++ ATL** and **C++ MFC** components (both are used by the native project and aren't installed by default).
- `JAVA_HOME` pointing at a full JDK (not a JRE) - the build needs its `include/`/`include/win32` JNI headers. Any modern JDK works for this. Note that some very old JDKs (e.g. JDK 8) have their own runtime incompatibility loading a DLL built by a modern MSVC toolchain and can crash when actually *running* code that loads it (see below), even though their headers compile fine.

With those in place, rebuild the Release DLLs with:

```
mvn generate-resources -DbuildNative=true
```

or the Debug DLLs with:

```
mvn generate-resources -DbuildNative=true -Dmode=Debug
```

Either configures and builds both x86 and x64 with CMake, copying the results into `bin/x86/<mode>` and `bin/x64/<mode>`. **Commit the updated files under `bin/`** afterwards so everyone else - and JitPack - picks up the change; `mvn package`/`mvn install` on their own won't rebuild or re-copy them.

To build the native side by itself, e.g. to iterate faster while working on it without going through the copy step every time, you can also drive CMake directly:

```
cmake -G "Visual Studio 17 2022" -A x64 -S native -B native/cmake-build-x64 -DJAVA_HOME="C:/path/to/jdk"
cmake --build native/cmake-build-x64 --config Release
```

(swap `x64`/`-A x64` for `x86`/`-A Win32` for the 32-bit build, and `Release` for `Debug` as needed)

Running the test suite
-----------------------

The `test` module exercises a small COM test object, `TestObject`, whose implementation only compiles into **Debug** builds of `com4j.dll`. The committed `bin/x64/Debug/com4j.dll` already has it; if you've changed native code, rebuild it first (see above). To run the tests:

1. Register the Debug DLL as a COM server from an **elevated** command prompt (this needs administrator rights - Maven won't do this for you, and shouldn't): `regsvr32 bin\x64\Debug\com4j.dll`. Use `regsvr32 /u ...` to undo this later.
2. Run the tests using `mvn test`

If you switch which architecture's Debug DLL you use for step 1, re-register the other one and un-register the old one first.

javah
-----
If you change the Java classes that define native methods, be sure to execute `native/run_javah.bat` to keep header files in sync
