package datacloud.scala.tpobject.bintree

object BinTrees {
  
  def contains(tree: IntTree, elem: Int): Boolean =
    tree match {        /** On sépare les différents cas de IntTree*/
    
      case EmptyIntTree => {
        return false
      }
      
      case node : NodeInt =>
        if (node.elem == elem) {
          return true
        } else {
          return contains(node.left, elem) || contains(node.right, elem)
        }
        
    }

  def size(tree: IntTree): Int = {
    tree match {
      case empty: EmptyIntTree.type => return 0
      case node: NodeInt => return 1 + size(node.left) + size(node.right)
    }
  }

  def insert(tree: IntTree, elem: Int): IntTree = 
    tree match {
    
      case empty: EmptyIntTree.type =>
        new NodeInt(elem, EmptyIntTree, EmptyIntTree)
        
      case node: NodeInt =>
        if (size(node.left) < size(node.right))
          NodeInt(node.elem, insert(node.left, elem), node.right)   /** On insère dans la branche qui comporte le moins d'élément*/
        else NodeInt(node.elem, node.left, insert(node.right, elem))
  }


  /** GENERALISATION  POUR N'IMPORTE QUEL TYPE A */

  def contains[A](tree: Tree[A], elem: A): Boolean = tree match {
    case EmptyTree => false
    case Node(test, left, right) => (test == elem) || (contains(left, elem) || contains(right, elem))
  }

  def size[A](tree: Tree[A]): Int = tree match {
    case EmptyTree     => 0
    case Node(test, left, right) => 1 + size(left) + size(right)
  }

  def insert[A](tree: Tree[A], elem: A): Tree[A] = tree match {
    case empty: EmptyTree.type => Node(elem, EmptyTree, EmptyTree)
    case Node(test, left, right) =>
                                    if (size(left) < size(right))
                                      Node(test, insert(left, elem), right)
                                    else Node(test, left, insert(right, elem))
  }
}