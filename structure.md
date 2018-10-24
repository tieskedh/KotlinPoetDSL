---
description: >-
  When you know the structure of the code, you know where to look if you want to
  achieve something. Therefor, we start with the structure.
---

# Structure

## Global

The code is seperated in packages which contain the components \(building-blocks\) that can be generated: functions, properties, etc.

Each package exists out of 4 important parts:

* The Tests
* The Builders
* The Acceptors
* The Accessors

## Acceptors

 The acceptors are interfaces that need to be implemented in order to receive the components.  
The main function of an acceptor is `accept(THECOMPONENT)`, this accepts the component when it is ready.  
Because the Acceptors want to receive the components, it makes sense to define the functions that start to build those components as receiver-functions to those interfaces. So, thats what we do.  
Last, there are some infix-functions that we want to have inside a scope which starts the build:

```kotlin
buildClazz{
   clazz("clazz") implements Comparable::class(delVar("delegationProperty")){
}
```

 Here the infix function is `KClass<*>.invoke(delvar: DelVar, body : ClassBody.()->Unit)`.  
We can define this function as top-level funciton, but that means that it will pop-up everywhere in the DSL.  
Therefor, these functions are added inside the interface, with a default-implementation.

## Accessors

 The accessors is a collection of the keywords in Kotlin:

```kotlin
  open.protected.inline.infix.tailrec.func("hi")
/*|-------constructs accessor--------|*/
```

 Above you see an acccessor being created and being invoked by an extension-function func.

 We can add these extension-functions in two ways:

1.  We add an infix-funciton to the Acceptor, which accepts the Accessor and the func. This means that we need to define the function func and a lot of other function twice: - Once for the usage with an accessor - Once for the usage without accessor.
2.  We can create different Accessors that implement the corresponding Acceptors. To chain the modifiers, \(public.open.inline.etc.\) we need to maintain the Accessor, therefor, we need to accept a generic, wich will be used to cast after a modifier is added. \(see Accessor.kt\)  We also need a class that creates those Accessors: we use the builders for that. To avoid duplication, we use an interface with a generic of the Accessor. \(see AccessorContainer.kt\)

I choose for the second option

## Builders

 The builders are wrappers around KotlinPoet-wrappers. They accept an constructor with two elements:

* The Accessor, which will be used to add the modifiers to the builder.
* A callback: this will be called once the object is created.

The builder also extends the interfaces of types it wants to accepts and adds them to the builder.  
It also modifies the builder in ways where it doesn't make sense to create the whole package, or where it isn't possible.

For the builders we also write a TopLevelBuilder. These builders are a stripped-down variant for the normal builders and are used for the build-functions: buildClass, buildFunc, etc.

## Tests

The test are the most important part of the code. Not only because it tests the functionality, but also because it shwos if the DSL is working. A test can most of the time tell if a particular design makes sense, or not, or if it is possible or not. Therefor the tests needs always to be written first. I'm talking about the invocation-tests. These are the tests that show the design and if the implementation of the builder works as expected.

Next to the invocation-tests, we have AcceptorTests.  
These tests are a bit boring to write... but they are very important to see if the Acceptors are implemented correctly in both the Builders and the Accessors.

