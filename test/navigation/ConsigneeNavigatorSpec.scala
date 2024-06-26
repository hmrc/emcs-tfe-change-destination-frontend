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

package navigation

import base.SpecBase
import controllers.routes
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, NoInformation, VatNumber}
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.consignee._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class ConsigneeNavigatorSpec extends SpecBase {

  val navigator = new ConsigneeNavigator

  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  "ConsigneeNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Consignee CYA" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testArc)
      }

      "for the ConsigneeAddress page" - {

        "must go to the Consignee Check Your Answers page" in {

          navigator.nextPage(ConsigneeAddressPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testArc)
        }
      }

      "for the ConsigneeExcise page" - {

        "must go to the ConsigneeBusinessName page" in {

          navigator.nextPage(ConsigneeExcisePage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for the ConsigneeBusinessNamePage" - {

        "must go to CAM-NEE07" in {
          val userAnswers = emptyUserAnswers
            .set(ConsigneeBusinessNamePage, "a business name")

          navigator.nextPage(ConsigneeBusinessNamePage, NormalMode, userAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testArc, NormalMode)
        }

      }

      "from ConsigneeExemptOrganisationPage to ConsigneeBusinessName" in {

        navigator.nextPage(ConsigneeExemptOrganisationPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testArc, NormalMode)
      }

      "for the ConsigneeExportInformationPage" - {

        "must go to CAM-NEE12 export-vat page" - {
          "when VAT has been selected" in {
            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportInformationPage, Set(VatNumber))

            navigator.nextPage(ConsigneeExportInformationPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "must go to CAM-NEE13 export-eori page" - {
          "when EORI has been selected" in {
            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportInformationPage, Set(EoriNumber))

            navigator.nextPage(ConsigneeExportInformationPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeExportEoriController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "must go to CAM-NEE03 business name page" - {
          "when neither VAT or EORI are selected" in {
            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportInformationPage, Set(NoInformation))

            navigator.nextPage(ConsigneeExportInformationPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

      }

      "for the ConsigneeExportVat page" - {

        "must go to the ConsigneeExportEori page" - {

          "when the user selected both VAT and EORI on ConsigneeExportInformation" in {

            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))

            navigator.nextPage(ConsigneeExportVatPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeExportEoriController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "must go to the ConsigneeBusinessName page" - {

          "when the user selected only VAT on ConsigneeExportInformation" in {

            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportInformationPage, Set(VatNumber))

            navigator.nextPage(ConsigneeExportVatPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testArc, NormalMode)
          }
        }
      }

      "for the ConsigneeExportEoriPage" - {
        "must go to CAM-NEE03 consignee-business-name page" in {
          val userAnswers = emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(EoriNumber))
            .set(ConsigneeExportEoriPage, testEoriNumber)

          navigator.nextPage(ConsigneeExportEoriPage, NormalMode, userAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for the CheckAnswersConsignee page" - {
        "must go to the tasklist" in {
          navigator.nextPage(CheckAnswersConsigneePage, NormalMode, emptyUserAnswers) mustBe
            routes.TaskListController.onPageLoad(testErn, testArc)
        }
      }
    }

    "in Check mode" - {
      "must go to CheckYourAnswersConsigneeController" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testArc)
      }
    }

    "in Review mode" - {
      "must go to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
      }
    }
  }
}
