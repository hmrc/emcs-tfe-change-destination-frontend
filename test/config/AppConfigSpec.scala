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

package config

import base.SpecBase
import featureswitch.core.config.{FeatureSwitching, RedirectToFeedbackSurvey, ReturnToLegacy}
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach
import play.api.test.FakeRequest
import play.api.test.Helpers.GET

class AppConfigSpec extends SpecBase with BeforeAndAfterEach with FeatureSwitching with MockFactory {

  override val config: AppConfig = appConfig

  "AppConfig" - {

    ".deskproName must be emcstfe" in {
      config.deskproName mustBe "emcstfe"
    }

    ".feedbackFrontendSurveyUrl() must handoff to feedback frontend with the correct URL" in {
      config.feedbackFrontendSurveyUrl mustBe s"http://localhost:9514/feedback/${config.deskproName}/beta"
    }

    ".signOutUrl()" - {

      ".signOutUrl() must return the survey page when enabled" in {
        implicit val fakeRequest = FakeRequest(GET, "/emcs/cam/trader/123/draft/456/some/page")
        enable(RedirectToFeedbackSurvey)
        config.signOutUrl()(fakeRequest) mustBe controllers.auth.routes.SignedOutController.signOutWithSurvey().url
      }

      ".signOutUrl() must return the saved sign out URL when on a page that is savable" in {
        implicit val fakeRequest = FakeRequest(GET, "/emcs/cam/trader/123/draft/456/some/page")
        disable(RedirectToFeedbackSurvey)
        config.signOutUrl()(fakeRequest) mustBe controllers.auth.routes.SignedOutController.signOutSaved().url
      }

      ".signOutUrl() must return the none saved sign out URL when on a page that is not savable" in {
        implicit val fakeRequest = FakeRequest(GET, "/emcs/cam/trader/123/info/456/some/page")
        disable(RedirectToFeedbackSurvey)
        config.signOutUrl()(fakeRequest) mustBe controllers.auth.routes.SignedOutController.signOutNotSaved().url
      }

    }

    ".emcsTfeHomeUrl" - {
      "should generate the correct url" - {
        s"when the $ReturnToLegacy feature switch is enabled" in {
          enable(ReturnToLegacy)

          appConfig.emcsTfeHomeUrl mustBe "http://localhost:8080/emcs/trader"
        }

        s"when the $ReturnToLegacy feature switch is disabled" in {
          disable(ReturnToLegacy)

          appConfig.emcsTfeHomeUrl mustBe "http://localhost:8310/emcs/account"
        }
      }
    }
  }

}
