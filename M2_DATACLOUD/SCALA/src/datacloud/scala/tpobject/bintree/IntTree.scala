package datacloud.scala.tpobject.bintree

sealed abstract class IntTree    /** sealed = obligation de définir les sous-classes de IntTree dans ce fichier*/
case object EmptyIntTree extends IntTree
case class NodeInt(elem: Int, left: IntTree, right: IntTree) extends IntTree