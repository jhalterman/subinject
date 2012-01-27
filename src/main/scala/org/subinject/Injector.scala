package org.subinject

import org.scala_tools.subcut.inject.BindingModule

/** Dependency injector. Provides fully injected instances of configured types.
  *
  * @author Jonathan Halterman
  */
object Injector {
  private[subinject] var module: BindingModule = _

  /** Provides a fully injected instance of `T`.
    *
    * @throws IllegalStateException if no modules have been registered
    */
  def instanceOf[T <: Any: Manifest]: T = {
    if (module == null) throw new IllegalStateException("No modules have been registered")
    module.inject[T](None)
  }

  /** Provides a fully injected instance of `T` for the `name`.
    *
    * @throws IllegalStateException if no modules have been registered
    */
  def instanceOf[T <: Any: Manifest](name: String): T = {
    if (module == null) throw new IllegalStateException("No modules have been registered")
    module.inject[T](Some(name))
  }

  /** Registers the `bindingModule` with the injector. */
  def registerModule(bindingModule: BindingModule) {
    module = if (module == null) bindingModule else module ~ bindingModule
  }

  /** Resets the injector's configuration, removing any previously registered modules. */
  def reset() = module = null
}

/** Provides support for dependency injection. */
trait Injectable extends org.scala_tools.subcut.inject.Injectable {
  override def bindingModule = Injector.module
}