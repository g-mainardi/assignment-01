package pcd.ass01

class BoidsModel(
                  var nBoids: Int,
                  var separationWeight: Double,
                  var alignmentWeight: Double,
                  var cohesionWeight: Double,
                  val width: Double, val height: Double,
                  val maxSpeed: Double,
                  val perceptionRadius: Double,
                  val avoidRadius: Double) {
  var boids: List[Boid] = List[Boid]()
  var isRunning = false
  var isSuspended = false

  def turnOn(): Unit = isRunning = true

  def turnOff(): Unit = isRunning = false

  def suspend(): Unit = isSuspended = true

  def resume(): Unit = isSuspended = false

  def generateBoids(): Unit =
    for (i <- 0 until nBoids) {
      val pos = new P2d(-width / 2 + Math.random * width, -height / 2 + Math.random * height)
      val vel = new V2d(Math.random * maxSpeed / 2 - maxSpeed / 4, Math.random * maxSpeed / 2 - maxSpeed / 4)
      boids = boids :+ Boid(pos, vel)
    }

  def clearBoids(): Unit =
    nBoids = 0
    boids = List[Boid]()

  def getMinX: Double = -width / 2
  def getMaxX: Double = width / 2
  def getMinY: Double = -height / 2
  def getMaxY: Double = height / 2

  def setSeparationWeight(value: Double): Unit = separationWeight = value
  def setAlignmentWeight(value: Double): Unit = alignmentWeight = value
  def setCohesionWeight(value: Double): Unit = cohesionWeight = value

  def setBoidsNumber(n: Int): Unit = nBoids = n
}