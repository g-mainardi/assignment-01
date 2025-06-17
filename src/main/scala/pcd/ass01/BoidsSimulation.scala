package pcd.ass01

import pcd.ass02

object BoidsSimulation extends App{
  private[ass01] val N_BOIDS = 3000
  private[ass01] val SEPARATION_WEIGHT = 1.0
  private[ass01] val ALIGNMENT_WEIGHT = 1.0
  private[ass01] val COHESION_WEIGHT = 1.0
  private[ass01] val ENVIRONMENT_WIDTH = 1000
  private[ass01] val ENVIRONMENT_HEIGHT = 1000
  private[ass01] val MAX_SPEED = 4.0
  private[ass01] val PERCEPTION_RADIUS = 50.0
  private[ass01] val AVOID_RADIUS = 20.0
  private[ass01] val SCREEN_WIDTH = 1280
  private[ass01] val SCREEN_HEIGHT = 720

  val model = BoidsModel(N_BOIDS, SEPARATION_WEIGHT, ALIGNMENT_WEIGHT,
                          COHESION_WEIGHT, ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
                          MAX_SPEED, PERCEPTION_RADIUS, AVOID_RADIUS)

//  val modelJ = new ass02.BoidsModel(N_BOIDS, SEPARATION_WEIGHT, ALIGNMENT_WEIGHT,
//    COHESION_WEIGHT, ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
//    MAX_SPEED, PERCEPTION_RADIUS, AVOID_RADIUS)
  private val sim = BoidsSimulatorSequential(model)
  sim attachView BoidsView(model, SCREEN_WIDTH, SCREEN_HEIGHT)
  sim.runSimulation()
//  private val simJ = new ass02.BoidsSimulatorSequential(model)
//  simJ attachView ass02.BoidsView(modelJ, SCREEN_WIDTH, SCREEN_HEIGHT)
//  simJ.runSimulation()
}