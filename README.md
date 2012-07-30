# Subinject 0.1.0

A thread-safe dynamically configurable service locator that produces fully injected instances via [SubCut](https://github.com/dickwall/subcut).

## Examples

Register a module:

```scala
val module = new NewBindingModule({ m =>
  m.bind[SomeService].toClass[SomeServiceImpl]
  m.bind[SomeService].idBy('threadsafe).toClass[SomeServiceThreadsafeImpl]
}

Injector.registerModule(module)
```

Get an instance of something:

```scala
val myService = Injector.instanceOf[MyService]
```

Get a named instance of something:

```scala
val myThreadsafeService = Injector.instanceOf[MyService]("threadsafe")
```

Make a class injectable:

```scala
class SomeClass extends Injectable {
  val someService = inject[SomeService]	
}
```

Reset the Injector's internal modules (useful for testing)

```scala
Injector.reset()
```

## License

Copyright 2012 Jonathan Halterman - Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
