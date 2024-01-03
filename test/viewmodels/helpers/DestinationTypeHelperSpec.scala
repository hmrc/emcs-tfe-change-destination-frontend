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

package viewmodels.helpers

import base.SpecBase
import fixtures.messages.sections.info.DestinationTypeMessages
import models.UserType.{NorthernIrelandWarehouse, Unknown}
import models.requests.DataRequest
import models.response.InvalidUserTypeException
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import models.sections.info.movementScenario.MovementScenario
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class DestinationTypeHelperSpec extends SpecBase {
  lazy val helper = new DestinationTypeHelper()


  Seq(DestinationTypeMessages.English).foreach { messagesForLanguage =>
    s"when being rendered in lang code of ${messagesForLanguage.lang.code}" - {
      implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      Seq("GBWK", "XIWK").foreach {
        ern =>
          s"when ERN is a warehouse keeper and starts with $ern" - {
            "title" - {
              "must return the correct value" in {
                helper.title(dataRequest(FakeRequest(), ern = s"${ern}123"), msgs) mustBe messagesForLanguage.headingMovement
              }
            }
            "heading" - {
              "must return the correct value" in {
                helper.heading(dataRequest(FakeRequest(), ern = s"${ern}123"), msgs) mustBe messagesForLanguage.headingMovement
              }
            }
          }
      }
      Seq("GBRC", "XIRC").foreach {
        ern =>
          s"when ERN is a registered consignor and starts with $ern" - {
            "title" - {
              "must return the correct value" in {
                helper.title(dataRequest(FakeRequest(), ern = s"${ern}123"), msgs) mustBe messagesForLanguage.headingImport
              }
            }
            "heading" - {
              "must return the correct value" in {
                helper.heading(dataRequest(FakeRequest(), ern = s"${ern}123"), msgs) mustBe messagesForLanguage.headingImport
              }
            }
          }
      }

      "when ERN is unexpected" - {
        "title" - {
          "must throw an error" in {
            val result = intercept[InvalidUserTypeException] {
              helper.title(dataRequest(FakeRequest(), ern = "beans123"), msgs)
            }
            result.message mustBe s"[DestinationTypeHelper][title] invalid UserType for CAM journey: $Unknown"
          }
        }
        "heading" - {
          "must throw an error" in {
            val result = intercept[InvalidUserTypeException] {
              helper.heading(dataRequest(FakeRequest(), ern = "beans123"), msgs)
            }
            result.message mustBe s"[DestinationTypeHelper][heading] invalid UserType for CAM journey: $Unknown"
          }
        }
      }

      "options" - {
        "must return two options" - {
          "when ERN is a GBRC" in {
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), ern = "GBRC123")
            helper.options(GreatBritain) mustBe MovementScenario.valuesUk.map(helper.radioOption)
          }
          "when ERN is a XIWK and dispatchPlace=GB" in {
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), ern = "XIWK123")
            helper.options(GreatBritain) mustBe MovementScenario.valuesUk.map(helper.radioOption)
          }
          "when ERN is a GBWK" in {
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), ern = "GBWK123")
            helper.options(GreatBritain) mustBe MovementScenario.valuesUk.map(helper.radioOption)
          }
        }
        "must return more than two options" - {
          "when ERN is XIWK and dispatchPlace=XI" in {
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), ern = "XIWK123")
            helper.options(NorthernIreland) mustBe MovementScenario.valuesEu.map(helper.radioOption)
          }
          "when ERN is XIRC and dispatchPlace=XI" in {
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), ern = "XIRC123")
            helper.options(NorthernIreland) mustBe MovementScenario.valuesEu.map(helper.radioOption)
          }
        }
        "must throw an exception" - {
          "when userType is unexpected" in {
            val result = intercept[InvalidUserTypeException] {
              implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), ern = "XI00123")
              helper.options(NorthernIreland)
            }
            result.message mustBe s"[DestinationTypeHelper][options] invalid UserType for CAM journey: $NorthernIrelandWarehouse"
          }
        }
      }
    }
  }
}
