package com.wanari.tutelar.core.impl.database

import cats.Applicative
import com.wanari.tutelar.core.{ConfigService, DatabaseService}
import reactivemongo.api.MongoDriver

import scala.concurrent.{ExecutionContext, Future}

object DatabaseServiceFactory {
  def create()(
      implicit config: ConfigService,
      driver: MongoDriver,
      ev: Applicative[Future],
      ec: ExecutionContext
  ): DatabaseService[Future] = {
    config.getDatabaseConfig.`type` match {
      case DatabaseConfig.MEMORY   => new MemoryDatabaseService[Future]
      case DatabaseConfig.POSTGRES => new PostgresDatabaseService(PostgresDatabaseService.getDatabase)
      case DatabaseConfig.MONGO    => new MongoDatabaseService(config.getMongoConfig)
      // todo not supported?
    }
  }

  case class DatabaseConfig(`type`: String)
  object DatabaseConfig {
    val MEMORY   = "memory"
    val POSTGRES = "postgres"
    val MONGO    = "mongo"
  }
}
