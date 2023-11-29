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

import config.AppConfig
import controllers.actions._
import controllers.actions.predraft.PreDraftDataRequiredAction
import fixtures.BaseFixtures
import handlers.ErrorHandler
import models.UserAnswers
import models.requests.{DataRequest, MovementRequest, OptionalDataRequest, UserRequest}
import models.response.emcsTfe.GetMovementResponse
import models.response.referenceData.TraderKnownFacts
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import models.requests.{DataRequest, UserRequest}
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Play.materializer
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.mvc.{MessagesControllerComponents, Request}
import play.api.test.Helpers.stubPlayBodyParsers

trait SpecBase extends AnyFreeSpec with Matchers with OptionValues with ScalaFutures with BaseFixtures with GuiceOneAppPerSuite {

  lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  lazy val messagesControllerComponents: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]
  lazy val preDraftDataRequiredAction: PreDraftDataRequiredAction = app.injector.instanceOf[PreDraftDataRequiredAction]
  lazy val dataRequiredAction: DataRequiredAction = app.injector.instanceOf[DataRequiredAction]
  lazy val errorHandler: ErrorHandler = app.injector.instanceOf[ErrorHandler]

  def messages(request: Request[_]): Messages = app.injector.instanceOf[MessagesApi].preferred(request)

  def messages(candidates: Seq[Lang]): Messages = app.injector.instanceOf[MessagesApi].preferred(candidates)

  val fakeAuthAction = new FakeAuthAction(stubPlayBodyParsers)
  val fakeUserAllowListAction = new FakeUserAllowListAction()

  def userRequest[A](request: Request[A], ern: String = testErn): UserRequest[A] =
    UserRequest(request, ern, testInternalId, testCredId, testSessionId, hasMultipleErns = false)

  def movementRequest[A](request: Request[A],
                         ern: String = testErn,
                         movementDetails: GetMovementResponse = getMovementResponseModel
                        ): MovementRequest[A] =
    MovementRequest(userRequest(request, ern), testArc, movementDetails)

  def dataRequest[A](request: Request[A],
                     answers: UserAnswers = emptyUserAnswers,
                     ern: String = testErn,
                     movementDetails: GetMovementResponse = getMovementResponseModel
                    ): DataRequest[A] =
    DataRequest(userRequest(request, ern, movementDetails), testDraftId, answers, testMinTraderKnownFacts)
}
