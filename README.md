The Allocation Instrumenter Tracker is a small library to simplify tracking memory allocations during method execution with [allocation-instrumenter](https://github.com/google/allocation-instrumenter), a Java agent that uses the java.lang.instrument API and ASM to track every allocation in your Java program. The tracker consists of a single class, `MemoryAllocationTracker`, that tracks method execution time and the memory allocated during execution. This is helpful because, while the library this adds to tracks every allocation, it does not enable attributing the allocation to a particular function.

To keep things relatively low-overhead, the tracker uses AspectJ to track entry-exit to method(s) of interest, rather than instrumenting all methods.

## How to Get it
You can add this library into your Maven/Gradle/SBT/Leiningen project thanks to JitPack.io. Follow the instructions [here](https://jitpack.io/#lfeagan/allocation-instrumenter-tracker).

### Example Gradle instructions

1. Add this into your build.gradle file:
```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```

2. Add the dependency in appropriate location(s):
```
dependencies {
  implementation 'com.github.lfeagan:allocation-instrumenter-tracker:main-SNAPSHOT'
}
```

## How to Use
A short [usage](docs/USAGE.md) guide.

## Dependencies
1. [allocation-instrumenter](https://github.com/google/allocation-instrumenter)
1. [AspectJ](https://github.com/eclipse-aspectj/aspectj)
   1. `org.aspectj:aspectjrt:1.9.19+`
   2. `org.aspectj:aspectjweaver:1.9.19+`
2. [Wheat](https://github.com/lfeagan/wheat)
3. [SLF4J](https://www.slf4j.org/)

## Test Dependencies
1. [TestNG](https://testng.org/doc/)

