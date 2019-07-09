package com.wanari.tutelar.providers.userpass.email

import cats.MonadError
import cats.syntax.functor._
import com.wanari.tutelar.core.AmqpService.AmqpQueueConfig
import com.wanari.tutelar.core.AmqpService
import com.wanari.tutelar.providers.userpass.email.EmailServiceHttpImpl.EmailServiceHttpConfig
import com.wanari.tutelar.util.HttpWrapper

object EmailServiceFactory {
  import DatabaseConfig._

  def create[F[_]: MonadError[?[_], Throwable]]()(
      implicit configF: () => F[EmailServiceFactoryConfig],
      http: HttpWrapper[F],
      httpConfigF: () => F[EmailServiceHttpConfig],
      configByNameF: String => F[AmqpQueueConfig],
      amqpService: AmqpService[F]
  ): F[EmailService[F]] = {
    configF().map { config =>
      config.`type` match {
        case HTTP => new EmailServiceHttpImpl[F]()
        case AMQP => new EmailServiceAmqpImpl[F]()
      }
    }
  }

  case class EmailServiceFactoryConfig(`type`: String)
  object DatabaseConfig {
    val HTTP = "http"
    val AMQP = "amqp"
  }
}
