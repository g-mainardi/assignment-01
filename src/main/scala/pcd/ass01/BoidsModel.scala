package pcd.ass01

enum Attribute:
  case SEPARATION
  case ALIGNMENT
  case COHESION

class BoidsModel(var nBoids: Int,
                 var separationWeight: Double,
                 var alignmentWeight: Double,
                 var cohesionWeight: Double,
                 val width: Double, val height: Double,
                 val maxSpeed: Double,
                 val perceptionRadius: Double,
                 val avoidRadius: Double) :
  var boids: List[Boid] = List()
  var isRunning   = false
  var isSuspended = false

  def turnOn(): Unit = isRunning = true
  def turnOff(): Unit = isRunning = false
  def suspend(): Unit = isSuspended = true
  def resume(): Unit = isSuspended = false

  def generateBoids(): Unit =
    boids = (for
      _  <- 0 to nBoids
      pos = P2d(-width / 2 + Math.random * width, -height / 2 + Math.random * height)
      vel = V2d(Math.random * maxSpeed / 2 - maxSpeed / 4, Math.random * maxSpeed / 2 - maxSpeed / 4)
    yield
      Boid(pos, vel)).toList

  def clearBoids(): Unit =
    nBoids = 0
    boids = List()

  def getMinX: Double = -width / 2
  def getMaxX: Double = width / 2
  def getMinY: Double = -height / 2
  def getMaxY: Double = height / 2

  def setWeight(a: Attribute, value: Double): Unit = a match
    case Attribute.SEPARATION => separationWeight = value
    case Attribute.ALIGNMENT  => alignmentWeight = value
    case Attribute.COHESION   => cohesionWeight = value

  def setBoidsNumber(n: Int): Unit = nBoids = n