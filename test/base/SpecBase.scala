/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package base

import controllers.action.{FakeAuthAction, FakeDataRetrievalAction, FakeMovementAction, FakeUserAllowListAction}
import controllers.actions.{AuthAction, DataRetrievalAction, MovementAction, UserAllowListAction}
import fixtures.{BaseFixtures, GetMovementResponseFixtures}
import models.UserAnswers
import models.requests.{DataRequest, MovementRequest, OptionalDataRequest, UserRequest}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import play.api.Application
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Request, Result}
import play.api.test.FakeRequest

import scala.concurrent.Future
import play.api.test.Helpers.{cookies, defaultAwaitTimeout}

trait SpecBase
  extends AnyFreeSpec
    with Matchers
    with TryValues
    with OptionValues
    with ScalaFutures
    with IntegrationPatience
    with BaseFixtures
    with GetMovementResponseFixtures {

  def messagesApi(app: Application): MessagesApi = app.injector.instanceOf[MessagesApi]
  def messages(app: Application): Messages = messagesApi(app).preferred(FakeRequest())
  def messages(app: Application, lang: Lang): Messages = messagesApi(app).preferred(Seq(lang))

  def getLanguageCookies(of: Future[Result]): String = {
    cookies(of).get("PLAY_LANG").get.value
  }

  def optionalDataRequest[A](request: Request[A], answers: Option[UserAnswers] = None): OptionalDataRequest[A] =
    OptionalDataRequest(movementRequest(request), answers)

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "play.filters.csp.nonce.enabled" -> false
      )
      .overrides(
        bind[AuthAction].to[FakeAuthAction],
        bind[UserAllowListAction].to[FakeUserAllowListAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers)),
        bind[MovementAction].toInstance(new FakeMovementAction(getMovementResponseModel))
      )

  def userRequest[A](request: Request[A]): UserRequest[A] = UserRequest(request, testErn, testInternalId, testCredId)

  def movementRequest[A](request: Request[A]): MovementRequest[A] = MovementRequest(userRequest(request), testArc, getMovementResponseModel)

  def dataRequest[A](request: Request[A], answers: UserAnswers = emptyUserAnswers): DataRequest[A] =
    DataRequest(movementRequest(request), answers)
}
