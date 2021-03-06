package org.denigma.graphs

import com.tinkerpop.blueprints._
import play.Logger
import scala.collection.JavaConversions._
import scala.{collection, Some}
import org.denigma.graphs.core._
import org.denigma.graphs.schemes.{LinkInCreator, LinkOutCreator}


//import com.tinkerpop.blueprints.impls.orient.{OrientVertex, OrientGraph}


/*
LinkIn and LinkOut are used to serialize Links to, i.e. not for the schema
 */





object SG extends GraphParams
{

  var _sg:SemanticGraph = null

  def sg = {
    if(_sg==null) _sg = new SemanticGraph()
    _sg
  }
  def sg_= (value:SemanticGraph) = _sg = value





  trait IndexedNode{
    val v:Vertex

    def setInd(ind: Index[Vertex], name: String, value: String): Vertex = {

      v.getProperty[String](name) match {
        case null => //skip
        case str: String => ind.remove(name, value, v)
      }

      v.setProperty(name, value)
      ind.put(name, value, v)
      v
    }


    def setName(name: String): Vertex = setInd(sg.names, "name", name)

    def setTypes(name: String): Vertex = setInd(sg.types, "type", name)

    def setUsers(name: String): Vertex = setInd(sg.users, "user", name)

    def nodeTypes = v.getVertices(Direction.OUT, TYPE)

    def nodeType(name: String): Option[Vertex] = nodeTypes.find(_.getProperty[String](TYPE) == name)

    def typeExists(name: String): Boolean = nodeType(name) == None

    def isProperty: Boolean = v.getProperty(PROPERTY) != null


  }



    /*
  * class that add some new features to Vertex
  * */
  implicit class SemanticNode(val v:Vertex)  extends EasyNode with IndexedNode
  {




    /*get vertices marked as links*/
    def linkVertices(dir:Direction,labels:String): collection.Iterable[Vertex] = v.getVertices(dir,labels).filter(p=>p.isLink)   //TODO: rewrite


    /* adds connected node*/
    def addConnected(label:String, params:(String,String)*): Vertex = {
      val n=   sg.addNode(params:_*)
      v.addEdge(label,n)
      n.toLink(label)
      n
    }
    /* adds or gets connected node*/
    def addGetConnected(label:String, params:(String,String)*): Vertex =  {
      val n= sg.setParams(v.getVertices(Direction.OUT, label).headOption.getOrElse(sg.addNode()))
      v.addEdge(label,n)
      n
    }

    /*
    adds vertex as link
     */
    def addLink(label:String, params:(String,String)*): Vertex = addConnected(label,params:_*).toLink(label)




    def addGetLink(label:String, params:(String,String)*): Vertex =  addGetConnected(label,params:_*).toLink(label)

    def addGetLinkTo(label:String, to:Vertex,params:(String,String)*): Vertex=  {
      val n= this.addGetLink(label,params:_*)
      n.addEdge(label,to)
      n
    }

    def addLinkTo(label:String, to:Vertex, params:(String,String)*): Vertex = {
      val n=   this.addLink(label,params:_*)
      n.addEdge(label,to)
      n
    }

    /* get node that represents the link*/
    def getLinkNode(label:String): Option[Vertex] = v.getVertices(Direction.OUT, label).find(p => p.isLink(label))

    def getSetLinkNode(label:String, params:(String,String)*): Vertex =  sg.setParams(v.getLinkNode(label).getOrElse(sg.addNode().toLink(label)))


    /* if this node is link*/
    def isLink: Boolean = v.getProperty[String](LINK) match {
      case null => false
      //case false=>false
      case _ => true
    }


    /*check if it is link of label type*/
    def isLink(labels: String*): Boolean = this.asLink match {
      case Some(l) => labels.contains(l)
      case None => false
    }

      /*
      checks if Vertex is one of the type
       */
    def isOfType(types:String*) = nodeTypes.exists{
      case tp:Vertex=>tp.str(TYPE) match {
        case Some(tpn)=>types.contains(tpn)
        case None=>false
        }
    }

    def toLink(label:String): Vertex = {
      //labels.foreach(v.setProperty(LINK,_))
      v.setProperty(LINK,label)
      v
    }

    def asLink:Option[String] = v.getProperty[String](LINK) match {
      case null=>None
      case str:String =>Some(str)
      case _ => Logger.error("Strange link inside") ; None
    }


    def linkFilter(v: Vertex, labels: String*): (Vertex) => Boolean = av=>av.isLink(labels:_*)

    def ~>(label:String,params:(String,String)*):LinkOutCreator = new LinkOutCreator(v,label,params:_*)

    def <~(label:String,params:(String,String)*):LinkInCreator = new LinkInCreator(v,label,params:_*)




  }
}
