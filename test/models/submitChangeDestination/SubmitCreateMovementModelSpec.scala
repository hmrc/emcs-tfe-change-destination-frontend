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

package models.submitChangeDestination

import base.SpecBase
import config.AppConfig
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemSmallIndependentProducerMessages
import models.requests.DataRequest
import models.sections.info._
import pages.sections.info._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class SubmitChangeDestinationModelSpec extends SpecBase with ItemFixtures {
  implicit val ac: AppConfig = appConfig

  val messagesForLanguage = ItemSmallIndependentProducerMessages.English
  implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "dispatchOffice" - {
    "when XIRC" - {
      s"must return OfficeModel(XI$dispatchOfficeSuffix)" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = emptyUserAnswers,
          ern = "XIRC123"
        )

        SubmitChangeDestinationModel.dispatchOffice mustBe OfficeModel(s"XI$dispatchOfficeSuffix")
      }
    }
    "when XIWK" - {
      s"must return OfficeModel(GB$dispatchOfficeSuffix) when DispatchPlacePage is GreatBritain" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = emptyUserAnswers.set(DispatchPlacePage, DispatchPlace.GreatBritain),
          ern = "XIWK123"
        )

        SubmitChangeDestinationModel.dispatchOffice mustBe OfficeModel(s"GB$dispatchOfficeSuffix")
      }
      s"must return OfficeModel(XI$dispatchOfficeSuffix) when DispatchPlacePage is NorthernIreland" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = emptyUserAnswers.set(DispatchPlacePage, DispatchPlace.NorthernIreland),
          ern = "XIWK123"
        )

        SubmitChangeDestinationModel.dispatchOffice mustBe OfficeModel(s"XI$dispatchOfficeSuffix")
      }
    }
    Seq("GBRC123", "GBWK123").foreach(
      ern =>
        s"when $ern" - {
          s"must return OfficeModel(GB$dispatchOfficeSuffix)" in {
            implicit val dr: DataRequest[_] = dataRequest(
              request = fakeRequest,
              answers = emptyUserAnswers,
              ern = ern
            )

            SubmitChangeDestinationModel.dispatchOffice mustBe OfficeModel(s"GB$dispatchOfficeSuffix")
          }
        }
    )
  }

  "apply" - {
    "must return a model" - {
      "when XIRC" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers,
          ern = "XIRC123"
        )

        SubmitChangeDestinationModel.apply mustBe xircSubmitChangeDestinationModel
      }
      "when XIWK" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers.set(DispatchPlacePage, DispatchPlace.NorthernIreland),
          ern = "XIWK123"
        )

        SubmitChangeDestinationModel.apply mustBe xiwkSubmitChangeDestinationModel
      }
      "when GBRC" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers,
          ern = "GBRC123"
        )

        SubmitChangeDestinationModel.apply mustBe gbrcSubmitChangeDestinationModel
      }
      "when GBWK" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers,
          ern = "GBWK123"
        )

        SubmitChangeDestinationModel.apply mustBe gbwkSubmitChangeDestinationModel
      }
    }
  }
}
