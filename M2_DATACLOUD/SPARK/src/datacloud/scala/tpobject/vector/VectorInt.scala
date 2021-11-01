package datacloud.scala.tpobject.vector

class VectorInt(val elements: Array[Int]) extends Serializable {

  def length: Int = elements.length

  def get(i: Int): Int = {
    if (i >= this.length || i < 0) throw new Exception("Entre 0 et i")
    else {
      elements(i)
    }
  }

  override def toString: String = elements.mkString(" ")

  override def equals(a: Any): Boolean = a match {
    case vect: VectorInt => {
      if (this.length != vect.length) return false
      else
        for (i <- 0 to this.length - 1) {
          if (vect.get(i) != elements(i)) return false
        }
      return true
    }
    case _ => false
  }

  
  def +(other: VectorInt): VectorInt = {

    if (other.length != this.length) {
      throw new Exception("Vecteur de tailles différentes")
    } else {
      var arr: Array[Int] = new Array[Int](this.length)
      for (i <- 0 to this.length - 1) {
        arr(i) = (elements(i) + other.get(i))
      }
      return new VectorInt(arr)
    }
  }

  
  def *(v: Int): VectorInt = {

    var arr: Array[Int] = new Array[Int](this.length)

    for (i <- 0 to this.length - 1) {
      arr(i) = (this.get(i) * v)
    }
    return new VectorInt(arr)
  }
  

  def prodD(other: VectorInt): Array[VectorInt] = {
    if (this.length != other.length)
      throw new Exception("prodD Vecteurs de mauvaise taille")
    else {

      var result: Array[VectorInt] = new Array[VectorInt](this.length)

      for (i <- 0 to this.length-1) {
        result(i) = other * (this.get(i))
      }
      return result
    }
  }

}
object VectorInt {

  implicit def intToVectorInt(array: Array[Int]): VectorInt = 
    return new VectorInt(array)

}