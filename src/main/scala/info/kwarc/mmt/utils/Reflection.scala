package info.kwarc.mmt.utils

import java.lang.reflect.InvocationTargetException

import scala.reflect.runtime.universe

class Reflection(classLoader: ClassLoader) {
  private val mirror = universe.runtimeMirror(classLoader)

  def getClass(fqcp : String): ReflectedClass = new ThisReflectedClass(fqcp)

  private class ThisReflectedClass(val fqcp : String) extends ReflectedClass {
    private val cls =  mirror.reflectClass(mirror.staticClass(fqcp))
    private val constructor = cls.symbol.toType.decl(universe.termNames.CONSTRUCTOR).asMethod
    def getInstance(args:List[Any]) = {
      new ThisReflectedInstance(cls.reflectConstructor(constructor).apply(args:_*),this)
    }
  }
  private class ThisReflectedInstance(private[Reflection] val instance : Any,val reflectedClass : ReflectedClass) extends ReflectedInstance {
    import Reflection._
    private val symbol = mirror.classSymbol(instance.getClass)
    private val reflected = mirror.reflect(instance)
    def method[A](name : String,tp : Reflection.ReturnType[A], args : List[Any]) : A = {
      val rargs = args.map {
        case r:ThisReflectedInstance => r.instance
        case a => a
      }
      val decl = symbol.toType.decl(universe.TermName(name)).asMethod//.member(universe.TermName(name)).asInstanceOf[universe.MethodSymbol]
      val t = decl.asMethod.returnType
      val method = reflected.reflectMethod(decl)
      val res = try {
        method.apply(rargs:_*)
      } catch {
        case e : InvocationTargetException =>
          throw e.getCause
      }
      val ret = mirror.reflect(res).instance
      resolve(ret,tp)
    }
    private def resolve[A](o : Any,tp : Reflection.ReturnType[A]) : A = tp match {
      case Atomic(cls) => o.asInstanceOf[A]
      case RList(cls) => o.asInstanceOf[Array[Any]].map {
        case io => resolve(io,cls)
      }.toList.asInstanceOf[A]
      case RPair(l,r) =>
        val p = o.asInstanceOf[{
          def _1 : l.AA
          def _2 : r.AA
        }]
        (resolve(p._1,l),resolve(p._2,r)).asInstanceOf[A]
      case RTriple(l,m,r) =>
        val p = o.asInstanceOf[{
          def _1 : l.AA
          def _2 : m.AA
          def _3 : r.AA
        }]
        (resolve(p._1,l),resolve(p._2,m),resolve(p._3,r)).asInstanceOf[A]
      case Reflected(cls) =>
        new ThisReflectedInstance(o,cls).asInstanceOf[A]
      case ROption(cls) => o match {
        case None => None.asInstanceOf[A]
        case Some(a: Any) => Some(resolve(a, cls)).asInstanceOf[A]
      }
    }
  }
}

trait ReflectedClass {
  val fqcp : String
  def getInstance(args:List[Any]) : ReflectedInstance
}
trait ReflectedInstance {
  val reflectedClass : ReflectedClass
  def method[A](name : String,tp : Reflection.ReturnType[A], args : List[Any]) : A
}
object Reflection {
  sealed trait ReturnType[A] {
    type AA = A
  }
  case class Atomic[A](cls: Class[A]) extends ReturnType[A]
  case class RList[A](cls: ReturnType[A]) extends ReturnType[scala.collection.immutable.List[A]]
  case class RPair[A, B](l: ReturnType[A], r: ReturnType[B]) extends ReturnType[(A, B)]
  case class RTriple[A, B, C](l: ReturnType[A], m: ReturnType[B], r: ReturnType[C]) extends ReturnType[(A, B, C)]
  case class Reflected(cls : ReflectedClass) extends ReturnType[ReflectedInstance]
  case class ROption[A](cls: ReturnType[A]) extends ReturnType[Option[A]]
  class RFunction
  val string = Atomic(classOf[String])
  val unit = Atomic(classOf[Unit])
}