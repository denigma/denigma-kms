package controllers

import play.api.mvc._
import models._

import play.api.libs.json._
import com.tinkerpop.blueprints.Vertex
import org.joda.time.DateTimeZone
import scala._
import scala.Some
import org.denigma.graphs.viewmodels._
import org.denigma.graphs.SG
import SG._
import scala.collection.JavaConversions._



object Graphs extends Controller with GenGraph{

  DateTimeZone.setDefault(DateTimeZone.UTC)

  def index(id:String) = Action {
    implicit request =>


      val flags = List()//List("United Kingdom","Russia","Ukraine","Israel","Germany","France","Italy","United States","China","Turkey","Spain","Austria").sorted
      val items = List("About","Blog","ILA Manifesto","Take Action","Projects")
      val res = Items(items,flags)

      Ok(views.html.graphs.index(res,if(id=="")"TestRoot" else id)) //Ok(views.html.page("node","menu","0"))
  }

  def content = Action {
    implicit request =>
      Ok(views.html.graphs.content()) //Ok(views.html.page("node","menu","0"))
  }


//  def node(id:String) = Action {
//    implicit request =>
//      Ok(views.html.graphs.vertex(id)) //Ok(views.html.page("node","menu","0"))
//  }

  def vertices(id:String) = Action {
    implicit request =>
      vo(id) match {
        case None=>
          Ok(Json.obj("vertices" ->  JsNull)).as("application/json")
        case Some(r)=>
          Ok(Json.obj("vertices" -> Json.toJson(List(this.node2js(r))))).as("application/json") //Ok(views.html.page("node","menu","0"))
      }
  }


  def node2js(nv:NodeViewModel): JsObject = Json.obj("id" -> nv.id,
    "name" -> nv.name,
    "properties" -> nv.jsProps

  )


  def node2js(v:Vertex): JsObject  = this.node2js(new NodeViewModel(v))



  def edge2js(e:EdgeViewModel) = Json.obj("id" -> e.id,
    "label" -> e.label,
    "name" -> e.name,
    "properties" -> e.jsProps,
    "incoming" -> Json.toJson(e.v.inV().map(node2js)),
    "outgoing" -> Json.toJson(e.v.outV().map(node2js))
  )

  //def getV(id:String) = sg.nodeByIdOrName(id).orElse(TestGraph.rootOption())
  def vo(id:String) = sg.nodeByIdOrName(id).orElse(TestGraph.rootOption())


  /*
  gets incoming edges as json
   */
  def incoming(to:String) = Action{
    implicit request =>
      vo(to) match
      {
        case None =>Ok(Json.obj("edges" ->  JsNull)).as("application/json")
        case Some(vert) =>
          val edges = vert.allInV.map{case (label:String,v:Vertex)=>new EdgeViewModel(label, v)}
          Ok(Json.obj("edges" -> Json.toJson(edges.map(edge2js)))).as("application/json") //Ok(views.html.page("node","menu","0"))
      }
  }

  /*
  gets outgoing edges as json
   */
  def outgoing(from:String) = Action{
    implicit request =>
      vo(from) match
      {
        case None =>Ok(Json.obj("edges" ->  JsNull)).as("application/json")
        case Some(vert) =>
          val edges = vert.allOutV.map{case (label:String,v:Vertex)=>new EdgeViewModel(label, v)}
          Ok(Json.obj("edges" -> Json.toJson(edges.map(edge2js)))).as("application/json") //Ok(views.html.page("node","menu","0"))
      }
  }

}