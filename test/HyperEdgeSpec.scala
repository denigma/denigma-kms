import models.graphs.{SG, SemanticGraph}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.Play
import com.tinkerpop.blueprints._
import scala.collection.JavaConversions._


/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class HyperEdgeSpec extends SemanticSpec {




  "HyperEdge" should {
    val learn = "learn"
    val manage = "manage"
    val discover="discover"
    val discStr = "discoveries"
    val peopleStr="people"

    def prepareTest = {
      val sg = prepareGraph
      import sg._
      val root = sg.root(sg.ROOT,NAME->sg.ROOT,"other"->"otherval")

      root.addLink(learn,"name"->learn)
      val people = sg.addNode(NAME->peopleStr)
      val m = root.addLinkTo(manage,people,"name"->manage)
      val discoveries = sg.addNode(NAME->discStr)
      val d: Vertex = root.addGetLinkTo(discover,discoveries,"name"->discover)
      val dd: Vertex =root.addGetLinkTo(discover,discoveries)
      g.commit()
      sg
    }

    "add LinkNodes & in, out tests" in new WithApplication{
      val sg = prepareTest
      import sg._
      val rr = sg.root
      rr.getProperty[String]("other") must beEqualTo("otherval")
      rr.outE(learn).size must beEqualTo(1)
      rr.outV(learn).size must beEqualTo(1)
      rr.outL(learn).size must beEqualTo(1)
      rr.inL(learn).size must beEqualTo(0)
      rr.outL(learn,manage).size must beEqualTo(2)
      val learnL: List[Vertex] = rr.getVertices(Direction.OUT,learn).toList
      learnL.size must beEqualTo(1)
      val l = learnL.head


      l.getProperty[String]("name") must beEqualTo(learn)
      l.isLink must beTrue
      l.isLink(manage) must beFalse
      l.isLink(learn) must beTrue

      l.inE(manage).size must beEqualTo(0)
      l.inE(learn).size must beEqualTo(1)
      l.inV(learn).size must beEqualTo(1)
      l.outL(learn).size must beEqualTo(0)
      l.inV(manage,learn).size must beEqualTo(1)
      g.shutdown()
    }

    "add hyperedges to nodes" in new WithApplication{
      val sg = prepareTest
      import sg._
      val rr = sg.root
      val pT = sg.names.get(sg.NAME,peopleStr).headOption.get
      pT.getProperty[String](sg.NAME) must beEqualTo(peopleStr)
      pT.getVertices(Direction.OUT,manage).headOption must beNone
      (pT.getVertices(Direction.IN, manage).headOption must not).beNone
      val ml= pT.getVertices(Direction.IN,manage).head
      ml.isLink must beTrue
      ml.getProperty[String](NAME) must beEqualTo(manage)
      ml.isLink(manage) must beTrue

      //(ml.outV(manage).headOption must not) beNone
      ml.outV(manage).head.getProperty[String](NAME) must beEqualTo(peopleStr)
      (ml.inV(manage).headOption must not).beNone
      ml.inV(manage).head.getProperty[String]("other") must beEqualTo("otherval")

      pT.inL(manage).size must beEqualTo(1)
      //mT.getVertices(Direction.IN)
      g.shutdown()
      //pT.getEdges(Direction.IN,manage).headOption.
    }


    "add hyperedges" in new WithApplication{


    }



  }

}
