package org.subinject

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scala_tools.subcut.inject.NewBindingModule
import org.scala_tools.subcut.inject.BindingModule
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec

/** @author Jonathan Halterman  */
@RunWith(classOf[JUnitRunner])
class InjectorSpec extends WordSpec with ShouldMatchers with BeforeAndAfterEach {
  override def beforeEach() {
    super.beforeEach()
    Injector.reset()
  }

  "Injector" should {
    "inject dependencies" in {
      val m = new NewBindingModule({ module =>
        import module._

        bind[java.util.List[_]].toClass[java.util.ArrayList[_]]
        bind[java.util.List[_]].idBy('threadsafe).toClass[java.util.Vector[_]]
      })

      Injector.registerModule(m)
      Injector.instanceOf[java.util.List[_]].isInstanceOf[java.util.ArrayList[_]] should be(true)
      Injector.instanceOf[java.util.List[_]]("threadsafe").isInstanceOf[java.util.Vector[_]] should be(true)
    }

    "inject recursive dependencies" in {
      Injector.registerModule(RecursiveModule)
      val a = Injector.instanceOf[A]
      a.b.c.name should equal("joe")
    }

    "register modules" in {
      val m1 = new NewBindingModule({ m =>
        m.bind[java.util.List[_]].toClass[java.util.ArrayList[_]]
      })

      val m2 = new NewBindingModule({ m =>
        m.bind[java.util.List[_]].idBy('threadsafe).toClass[java.util.Vector[_]]
      })

      Injector.registerModule(m1)
      Injector.registerModule(m2)

      Injector.instanceOf[java.util.List[_]].isInstanceOf[java.util.ArrayList[_]] should be(true)
      Injector.instanceOf[java.util.List[_]]("threadsafe").isInstanceOf[java.util.Vector[_]] should be(true)
    }

    "fail on inject when no modules have been registered" in {
      intercept[IllegalStateException] {
        Injector.instanceOf[A]
      }
    }
  }

  "Injectable" should {
    "inject dependencies into mixed classes" in {
      val m = new NewBindingModule({ m =>
        m.bind[SomeInjectable].toClass[SomeInjectable]
        m.bind[String] toSingle "bob"
      })

      Injector.registerModule(m)
      Injector.instanceOf[SomeInjectable].name should be("bob")
    }
  }
}

object RecursiveModule extends NewBindingModule({ m =>
  m.bind[A].toClass[A]
  m.bind[B].toClass[B]
  m.bind[C].toClass[C]
  m.bind[String] toSingle "joe"
})

trait RecursiveInjectable extends Injectable {
  override def bindingModule = RecursiveModule
}

class A extends RecursiveInjectable {
  val b = inject[B]
}

class B extends RecursiveInjectable {
  val c = inject[C]
}

class C extends RecursiveInjectable {
  val name = inject[String]
}

class SomeInjectable extends Injectable {
  val name = inject[String]
}