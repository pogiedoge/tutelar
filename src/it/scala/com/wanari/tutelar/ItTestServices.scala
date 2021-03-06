package com.wanari.tutelar

import com.wanari.tutelar.core._
import com.wanari.tutelar.core.impl.database.MemoryDatabaseService
import com.wanari.tutelar.core.impl.{AuthServiceImpl, ConfigServiceImpl, HealthCheckServiceImpl, HookServiceImpl}
import com.wanari.tutelar.providers.oauth2.{
  FacebookService,
  GithubService,
  GoogleService,
  MicrosoftService,
  OAuth2Service
}
import com.wanari.tutelar.providers.userpass.{PasswordDifficultyChecker, PasswordDifficultyCheckerImpl}
import com.wanari.tutelar.providers.userpass.basic.{BasicProviderService, BasicProviderServiceImpl}
import com.wanari.tutelar.providers.userpass.email._
import com.wanari.tutelar.providers.userpass.ldap.LdapService
import com.wanari.tutelar.providers.userpass.token.{TotpService, TotpServiceImpl}
import com.wanari.tutelar.util._

import scala.concurrent.{ExecutionContext, Future}

class ItTestServices(implicit ec: ExecutionContext) extends Services[Future] {
  import cats.instances.future._
  override implicit lazy val configService: ConfigService = new ConfigServiceImpl
  import configService._
  override implicit lazy val healthCheckService: HealthCheckService[Future] = new HealthCheckServiceImpl[Future]
  override implicit lazy val databaseService: DatabaseService[Future]       = new MemoryDatabaseService[Future]

  override implicit lazy val idGenerator: IdGenerator[Future]      = new IdGeneratorCounterImpl[Future]
  override implicit lazy val dateTimeService: DateTimeUtil[Future] = new DateTimeUtilCounterImpl[Future]
  override implicit lazy val authService: AuthService[Future]      = new AuthServiceImpl[Future]

  override implicit lazy val facebookService: FacebookService[Future]   = null
  override implicit lazy val githubService: GithubService[Future]       = null
  override implicit lazy val googleService: GoogleService[Future]       = null
  override implicit lazy val microsoftService: MicrosoftService[Future] = null

  implicit lazy val httpWrapper: HttpWrapper[Future]     = null
  override implicit val hookService: HookService[Future] = new HookServiceImpl[Future]

  def getOauthServiceByName(provider: String): OAuth2Service[Future] = {
    provider match {
      case "facebook"  => facebookService
      case "github"    => githubService
      case "google"    => googleService
      case "microsoft" => microsoftService
    }
  }

  override implicit lazy val ldapService: LdapService[Future] = null

  override implicit lazy val basicLoginService: BasicProviderService[Future] = new BasicProviderServiceImpl[Future]()
  override implicit lazy val emailService: EmailService[Future]              = new EmailServiceSmtpImpl[Future]()
  override implicit lazy val emailLoginService: EmailProviderService[Future] = new EmailProviderServiceImpl[Future]()
  override implicit lazy val totpService: TotpService[Future]                = new TotpServiceImpl[Future]()
  override implicit lazy val passwordDifficultyChecker: PasswordDifficultyChecker[Future] =
    new PasswordDifficultyCheckerImpl[Future]
  override implicit lazy val tracerService: TracerService[Future]         = new TracerService[Future]()
  override implicit lazy val amqpService: AmqpService[Future]             = null
  override implicit lazy val escherService: EscherService[Future]         = null
  override implicit lazy val expirationService: ExpirationService[Future] = null
}
