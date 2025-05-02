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

package connectors.emcsTfeFrontend

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import mocks.connectors.MockHttpClient
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.twirl.api.Html
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class NavBarPartialConnectorSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with GetMovementResponseFixtures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new NavBarPartialConnector(mockHttpClient, appConfig)

  val dummyHtml: Html = Html("<div><p>hello</p></div>")

  "getNavBar()" - {

    "should return Some(Html)" - {

      "when call to TFE Frontend is successful" in {

        MockHttpClient.get(url"${appConfig.emcsTfeFrontendBaseUrl}/emcs/partials/navigation/trader/$testErn")
          .returns(Future.successful(Some(dummyHtml)))

        connector.getNavBar(exciseRegistrationNumber = testErn).futureValue mustBe Some(dummyHtml)
      }
    }

    "should None" - {

      "when call to TFE Frontend fails" in {

        MockHttpClient.get(url"${appConfig.emcsTfeFrontendBaseUrl}/emcs/partials/navigation/trader/$testErn")
          .returns(Future.failed(new Exception("foo")))

        connector.getNavBar(exciseRegistrationNumber = testErn).futureValue mustBe None
      }
    }
  }
}
