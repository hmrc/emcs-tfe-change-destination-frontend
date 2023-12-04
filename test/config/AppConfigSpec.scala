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
import featureswitch.core.config.{FeatureSwitching, ReturnToLegacy, StubGetTraderKnownFacts}

class AppConfigSpec extends SpecBase with FeatureSwitching {

  override lazy val config = applicationBuilder().build().injector.instanceOf[AppConfig]

  "AppConfig" - {

    ".deskproName must be emcstfe" in {
      config.deskproName mustBe "emcstfe"
    }

    ".feedbackFrontendSurveyUrl() must handoff to feedback frontend with the correct URL" in {
      config.feedbackFrontendSurveyUrl mustBe s"http://localhost:9514/feedback/${config.deskproName}/beta"
    }

    ".emcsMovementDetailsUrl()" - {

      "when ReturnToLegacy is enabled" - {

        "must return to the legacy URL" in {
          enable(ReturnToLegacy)
          config.emcsMovementDetailsUrl(testErn, testArc) mustBe s"http://localhost:8080/emcs/trader/$testErn/movement/$testArc/history"
        }
      }

      "when ReturnToLegacy is disabled" - {

        "must return to the new URL" in {
          disable(ReturnToLegacy)
          config.emcsMovementDetailsUrl(testErn, testArc) mustBe s"http://localhost:8310/emcs/account/consignment/$testErn/$testArc"
        }
      }
    }

    ".emcsTfeHomeUrl()" - {

      "when ReturnToLegacy is enabled" - {

        "when an ERN is supplied" - {

          "must return to the legacy URL including the ERN" in {
            enable(ReturnToLegacy)
            config.emcsTfeHomeUrl(Some(testErn)) mustBe s"http://localhost:8080/emcs/trader/$testErn"
          }
        }

        "when an ERN is NOT supplied" - {

          "must return to the legacy URL without the ERN" in {
            enable(ReturnToLegacy)
            config.emcsTfeHomeUrl(None) mustBe s"http://localhost:8080/emcs/trader"
          }
        }
      }

      "when ReturnToLegacy is disabled" - {

        "must return to the new URL" in {
          disable(ReturnToLegacy)
          config.emcsTfeHomeUrl(None) mustBe s"http://localhost:8310/emcs/account"
        }
      }
    }

    ".emcsMovementsUrl()" - {

      "when ReturnToLegacy is enabled" - {

        "must return to the legacy URL" in {
          enable(ReturnToLegacy)
          config.emcsMovementsUrl(testErn) mustBe s"http://localhost:8080/emcs/trader/$testErn/movements?movementtype=all"
        }
      }

      "when ReturnToLegacy is disabled" - {

        "must return to the new URL" in {
          disable(ReturnToLegacy)
          config.emcsMovementsUrl(testErn) mustBe s"http://localhost:8310/emcs/account/movements-in/$testErn"
        }
      }
    }

    ".languageMap()" in {
      config.languageMap.size mustBe 2
    }

    ".traderKnownFactsReferenceDataBaseUrl" - {

      "when StubGetTraderKnownFacts is enabled" - {

        "must return the stub URL" in {
          enable(StubGetTraderKnownFacts)
          config.traderKnownFactsReferenceDataBaseUrl mustBe "http://localhost:8309/emcs-tfe-reference-data"
        }
      }

      "when StubGetTraderKnownFacts is disabled" - {

        "must return the real URL" in {
          disable(StubGetTraderKnownFacts)
          config.traderKnownFactsReferenceDataBaseUrl mustBe "http://localhost:8312/emcs-tfe-reference-data"
        }
      }
    }

  }
}
