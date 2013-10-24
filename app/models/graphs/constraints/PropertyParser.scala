package models.graphs.constraints

import play.Logger
import com.tinkerpop.blueprints.{Direction, Vertex,Element}
import models.graphs.{SG, GP, SemanticGraph}
import scala.collection.JavaConversions._
import scala.util.parsing.combinator._
import SG._

abstract class PropertyParser[T]
{
  def parse(v:Vertex):Option[T]

  val constraint: String

  /*
  parses after checks if it has the name
   */
  def withParam(name:String,v:Vertex)(fun:(String,Vertex)=>T): Option[T] =  v.getProperty[String](name) match
  {
    case null =>
      Logger.error(s"there is no param $name of node")
      None
    case str:String=> Some(fun(str,v))
  }

  def withName(v:Vertex)(fun:(String,Vertex)=>T):Option[T] = this.withParam(GP.NAME,v)(fun)
}


trait PropertyWriter extends Property {

  val constraint:String

  def constraintVertex(v:Vertex): Option[Vertex] =  v.getVertices(Direction.OUT,name).headOption

  def write(v:Vertex):Vertex

  def addNode(v:Vertex): Vertex = {

    v.addGetConnected(name,sg.NAME->name)

  }

}
