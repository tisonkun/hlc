# Hybrid Logical Clock

A [Hybrid Logical Clock](http://muratbuffalo.blogspot.de/2014/07/hybrid-logical-clocks.html) implementation in Java.

## Build

This library requires JDK 17+ for building:

```shell
./gradlew build -x test
```

## Use with Gradle

If you are using Gradle (recommended), add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation 'org.tisonkun.hlc:hlc:1.0.0'
}
```

## License

This library is under the [Apache License 2.0](LICENSE).

It ports a lot of code from [hlc-rs](https://github.com/tbg/hlc-rs). See [LICENSE.hlc-rs](licenses/LICENSE.hlc-rs) for COPYING info.
