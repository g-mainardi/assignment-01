/*
 *   V2d.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 */
package pcd.ass01

trait Vector2d:
  def x: Double
  def y: Double
  override def toString: String = s"$getClass(" + x + "," + y + ")"

/**
 *
 * 2-dimensional vector
 * objects are completely state-less
 *
 */
case class V2d(x: Double, y: Double) extends Vector2d:

  def sum(v: V2d): V2d = V2d(x + v.x, y + v.y)
  def abs: Double = Math.sqrt(x * x + y * y)

  def getNormalized: V2d = 
    val module = Math.sqrt(x * x + y * y)
    V2d(x / module, y / module)

  def mul(fact: Double): V2d = V2d(x * fact, y * fact)

case class P2d(x: Double, y: Double) extends Vector2d:

  def sum(v: V2d): P2d = P2d(x + v.x, y + v.y)
  def sub(v: P2d): V2d = V2d(x - v.x, y - v.y)

  def distance(p: P2d): Double =
    val dx = p.x - x
    val dy = p.y - y
    Math.sqrt(dx * dx + dy * dy)