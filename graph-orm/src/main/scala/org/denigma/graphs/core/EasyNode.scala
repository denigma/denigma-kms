package org.denigma.graphs.core

import com.tinkerpop.blueprints.{Edge, Direction, Vertex}
import java.lang
import scala.collection.JavaConversions._
import play.Logger
import org.joda.time.DateTime


/**
Trait for node that contains some useful helper methods
 */
trait EasyNode {
  val v:Vertex
  def linkFilter(v: Vertex, labels: String*): (Vertex => Boolean)


  def strings(params:String*): Map[String, String] = props[String](params:_*)
  def doubles(params:String*): Map[String, Double] = props[Double](params:_*)
  /*
  get a map of int properties
   */
  def ints(params:String*): Map[String, Int] = props[Int](params:_*)

  /*
  get a map of properties
   */
  def props[T](params:String*): Map[String, T] = params.map(p=>
    v.getProperty[T](p) match
    {
      case value:T=>p->value
    }).toMap

  /*
  set a lit of properties
   */
  def props(params:(String,Any)*) = params.foreach{case (key,value)=>v.setProperty(key,value)}
  /*
  gets all properties of the node
   */
  def allProps:Map[String,Any] = v.getPropertyKeys.map(key=>(key,v.getProperty[Any](key))).toMap
  /*
  get property as type
   */
  def p[T](label:String): Option[T] = v.getProperty[T](label) match {case null=>None; case value:T=>Some(value)}
  def has[T](prop:String,value:T): Boolean = v.getProperty[T](prop) match {case null=>false; case v:T=>v==value}

  /*
  get property as string
   */
  def str(name:String): Option[String] = p[String](name)
  def double(name:String): Option[Double] = p[Double](name)
  def int(name:String): Option[Int] = p[Int](name)
  def long(name:String): Option[Long] = p[Long](name)
  def dateTime(name:String): Option[DateTime] =  p[String](name).map(DateTime.parse)

  def toStr(name: String): Option[String] = v.getProperty[Any](name) match
  {
    case null=>None
    case obj=>Some(obj.toString)
  }

  /*
  get id of the node, it is supposed that ids are not changed, that is why it is lazy val
   */
  lazy val id = v.getProperty[String](GP.ID)

  def bool(name:String): Option[Boolean] = p[Boolean](name)



  def propsWithNew(obj:Map[String,Any]) = allProps++obj

  def inV(labels:String*): lang.Iterable[Vertex] = v.getVertices(Direction.IN,labels:_*)
  def inE(labels:String*): lang.Iterable[Edge] = v.getEdges(Direction.IN,labels:_*)


  def inL(labels:String*): Iterable[Vertex] = v.getVertices(Direction.IN,labels:_*).filter(linkFilter(v,labels:_*))

  def outV(labels:String*): lang.Iterable[Vertex] = v.getVertices(Direction.OUT,labels:_*)
  def outE(labels:String*): lang.Iterable[Edge] = v.getEdges(Direction.OUT,labels:_*)
  def outL(labels:String*): Iterable[Vertex] = v.getVertices(Direction.OUT,labels:_*).filter(linkFilter(v,labels:_*))

  def allInV: Iterable[(String, Vertex)] = v.getEdges(Direction.IN).map(e=>(e.getLabel,e.getVertex(Direction.OUT)))
  def allOutV = v.getEdges(Direction.OUT).map(e=>(e.getLabel,e.getVertex(Direction.IN)))
  //def allV = v.getEdges(Direction.BOTH).map(e=>(e.getLabel,e.getVertex(Direction.BOTH)))

  //def outLV(labels: String*): Iterable[Vertex] = outL(labels:_*).flatMap(r=>r.getVertices(Direction.OUT,labels:_*))

  def L(dir:Direction,labels: String*): Iterable[Vertex] = v.getVertices(dir, labels: _*).filter(linkFilter(v, labels: _*))


}

