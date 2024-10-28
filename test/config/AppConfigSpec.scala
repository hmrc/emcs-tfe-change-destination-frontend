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
import featureswitch.core.config.FeatureSwitching
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach

class AppConfigSpec extends SpecBase with BeforeAndAfterEach with FeatureSwitching with MockFactory {

  override val config: AppConfig = appConfig

  "AppConfig" - {

    ".deskproName must be emcstfe" in {
      config.deskproName mustBe "emcstfe"
    }

    ".feedbackFrontendSurveyUrl() must handoff to feedback frontend with the correct URL" in {
      config.feedbackFrontendSurveyUrl mustBe s"http://localhost:9514/feedback/${config.deskproName}"
    }

    ".signOutUrl()" - {
      "should have the correct url" in {
        appConfig.signOutUrl mustBe "http://localhost:8308/gg/sign-out"
      }
    }

    ".emcsTfeHomeUrl" - {
      "should generate the correct url" in {
        appConfig.emcsTfeHomeUrl mustBe "http://localhost:8310/emcs/account"
      }
    }

    ".emcsGeneralEnquiriesUrl" - {
      "should generate the correct url" in {
        appConfig.emcsGeneralEnquiriesUrl mustBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/excise-movement-and-control-system-emcs-enquiries"
      }
    }

    ".emcsMovementDetailsUrl() must return the correct URL" in {
      config.emcsMovementDetailsUrl(testErn, testArc) mustBe s"http://localhost:8310/emcs/account/trader/$testErn/movement/$testArc/overview"
    }

    ".emcsMovementsUrl() must return the correct URL" in {
      config.emcsMovementsUrl(testErn) mustBe s"http://localhost:8310/emcs/account/trader/$testErn/movements"
    }

    ".traderKnownFactsBaseUrl must return to the correct URL" in {
      config.traderKnownFactsBaseUrl mustBe s"http://localhost:8311/emcs-tfe/trader-known-facts"
    }
  }

}
