package pcd.ass01

/**
 *
 * 2-dimensional point
 * objects are completely state-less
 *
 */
case class P2d(x: Double, y: Double):

  def sum(v: V2d): P2d = P2d(x + v.x, y + v.y)
  def sub(v: P2d): V2d = V2d(x - v.x, y - v.y)

  def distance(p: P2d): Double =
    val dx = p.x - x
    val dy = p.y - y
    Math.sqrt(dx * dx + dy * dy)

  override def toString: String = "P2d(" + x + "," + y + ")"
