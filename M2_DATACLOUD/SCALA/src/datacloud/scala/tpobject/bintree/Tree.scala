package datacloud.scala.tpobject.bintree

sealed abstract class Tree[+A]
case object EmptyTree extends Tree[Nothing]
case class Node[A](elem : A, left : Tree[A], right : Tree[A]) extends Tree