package pcd.ass01

class Boid(var pos: P2d, var vel: V2d) {
  import Attribute.*

  def updateVelocity(model: BoidsModel): Unit =
    val calc = calculate(getNearbyBoids(model), model)
    vel = vel
      .sum(calc(ALIGNMENT)  mul model.alignmentWeight)
      .sum(calc(SEPARATION) mul model.separationWeight)
      .sum(calc(COHESION)   mul model.cohesionWeight)

    if vel.abs > model.maxSpeed then vel = vel.getNormalized mul model.maxSpeed

  def updatePos(model: BoidsModel): Unit =
    pos = pos sum vel
    if pos.x < model.getMinX  then pos = pos sum V2d(model.width, 0)
    if pos.x >= model.getMaxX then pos = pos sum V2d(-model.width, 0)
    if pos.y < model.getMinY  then pos = pos sum V2d(0, model.height)
    if pos.y >= model.getMaxY then pos = pos sum V2d(0, -model.height)

  private def getNearbyBoids(model: BoidsModel) = model.boids filter : other =>
    (other ne this) && (pos.distance(other.pos) < model.perceptionRadius)

  private def calculate(boids: List[Boid], model: BoidsModel)(a: Attribute): V2d = (a match
    case SEPARATION => calculateSeparation
    case ALIGNMENT  => calculateAlignment
    case COHESION   => calculateCohesion)(boids, model)

  private def calculateAll(t: Boid => Vector2d)(nearbyBoids: List[Boid], model: BoidsModel) =
    import scala.language.implicitConversions
    given Conversion[Double, Int] = _.toInt
    if nearbyBoids.nonEmpty
    then
      val (avgVx, avgVy) = nearbyBoids.map(t).foldLeft((0,0)): (acc, nearBoidAttr) =>
        (acc._1 + nearBoidAttr.x, acc._2 + nearBoidAttr.y)
      V2d(avgVx / nearbyBoids.size - vel.x, avgVy / nearbyBoids.size - vel.y).getNormalized
    else
      V2d(0, 0)

  private def calculateAlignment(nearbyBoids: List[Boid], model: BoidsModel) =
    calculateAll(_.vel)(nearbyBoids, model)

  private def calculateCohesion(nearbyBoids: List[Boid], model: BoidsModel) =
    calculateAll(_.pos)(nearbyBoids, model)

  private def calculateSeparation(nearbyBoids: List[Boid], model: BoidsModel) =
    import scala.language.implicitConversions
    given Conversion[Double, Int] = _.toInt
    nearbyBoids.foldLeft((0, 0, 0)) { (acc, boid) =>
      val otherPos = boid.pos
      if pos.distance(otherPos) < model.avoidRadius
      then
        (acc._1 + pos.x - otherPos.x, acc._2 + pos.y - otherPos.y, acc._3 + 1)
      else acc
    } match
      case (_, _, 0)       => V2d(0,0)
      case (dx, dy, count) => V2d(dx / count, dy / count).getNormalized
}