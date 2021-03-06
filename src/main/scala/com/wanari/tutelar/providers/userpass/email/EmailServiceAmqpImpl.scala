package com.wanari.tutelar.providers.userpass.email

import cats.Monad
import com.wanari.tutelar.Initable
import com.wanari.tutelar.core.AmqpService
import com.wanari.tutelar.core.AmqpService.{AmqpQueue, AmqpQueueConfig}
import com.wanari.tutelar.providers.userpass.email.EmailServiceAmqpImpl.TokenMessage
import com.wanari.tutelar.util.LoggerUtil.LogContext
import spray.json.RootJsonFormat

class EmailServiceAmqpImpl[F[_]: Monad](
    implicit amqpService: AmqpService[F],
    configByName: String => AmqpQueueConfig
) extends EmailService[F]
    with Initable[F] {
  import cats.syntax.applicative._

  protected lazy val queue: AmqpQueue = {
    val conf = configByName("email_service")
    amqpService.createQueue(conf)
  }

  override def init: F[Unit] = {
    queue
    ().pure[F]
  }

  override def sendRegisterUrl(email: String, token: String)(implicit ctx: LogContext): F[Unit] = {
    queue.send(TokenMessage("register", email, token)).pure[F]
  }

  override def sendResetPasswordUrl(email: String, token: String)(implicit ctx: LogContext): F[Unit] = {
    queue.send(TokenMessage("reset-password", email, token)).pure[F]
  }
}

object EmailServiceAmqpImpl {
  import spray.json.DefaultJsonProtocol._
  private case class TokenMessage(`type`: String, email: String, token: String)
  private implicit val tokenMessageFormat: RootJsonFormat[TokenMessage] = jsonFormat3(TokenMessage)
}
