---
description: 'For building, we use JitPack.'
---

# Building

## Version

We use JitPack for building the project. This means that you have a lot of choice about which version you want to use. You can check out every commit of every branch. Jitpack provides also the way to add, but otherwise this page is a bit empty...

For the url go to:  [https://jitpack.io/\#nl.devhaan/KotlinPoetDSL](https://jitpack.io/#nl.devhaan/KotlinPoetDSL)

## Gradle - groovy

1. Add the JitPack repository to the end of your build file

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

1. Add the dependency

```groovy
dependencies {
        implementation 'nl.devhaan:KotlinPoetDSL:VERSION'
}
```

## Gradle - kotlin

1. Add the JitPack repository to the end of your build file

```kotlin
allprojects {
	repositories {
		...
		maven("https://jitpack.io")
	}
}
```

2. Add the dependency

```kotlin
dependencies {
        implementation("nl.devhaan:KotlinPoetDSL:VERSION")
}
```

## Maven

1. Add the JitPack repository to the end of your build file

```markup
	<repositories>
		<repository>
   			<id>jitpack.io</id>
			<url>https://jitpack.io</url>
		</repository>
	</repositories>
```

2. add the dependency

```markup
	<dependency>
	    <groupId>nl.devhaan</groupId>
	    <artifactId>KotlinPoetDSL</artifactId>
	    <version>Tag</version>
	</dependency>
```

