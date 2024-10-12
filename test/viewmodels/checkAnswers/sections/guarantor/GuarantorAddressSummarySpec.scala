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

package viewmodels.checkAnswers.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorAddressMessages
import fixtures.messages.sections.guarantor.GuarantorAddressMessages.ViewMessages
import models.requests.DataRequest
import models.response.emcsTfe.{GuarantorType, MovementGuaranteeModel}
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.sections.info.movementScenario.MovementScenario.{ExportWithCustomsDeclarationLodgedInTheUk, UkTaxWarehouse}
import models.{CheckMode, UserAddress}
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.guarantor.{GuarantorAddressPage, GuarantorArrangerPage}
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class GuarantorAddressSummarySpec extends SpecBase {

  def expectedRow(value: Content, showChangeLink: Boolean = true)(implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {
    Some(
      SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaLabel)),
        value = Value(value),
        actions = if (!showChangeLink) Seq() else Seq(ActionItemViewModel(
          content = Text(messagesForLanguage.change),
          href = controllers.sections.guarantor.routes.GuarantorAddressController.onPageLoad(testErn, testArc, CheckMode).url,
          id = "changeGuarantorAddress"
        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
      )
    )
  }

  "GuarantorAddressSummary" - {

    Seq(GuarantorAddressMessages.English).foreach { implicit messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        Seq(GoodsOwner, Transporter).foreach { arranger =>
          s"when the Guarantor is ${arranger.getClass.getSimpleName.stripSuffix("$")}" - {

            "when there's no answer in the user answers" - {

              "must output the expected data" in {

                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                    .set(GuarantorArrangerPage, arranger),
                  movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(GuarantorType.NoGuarantor, None))
                )

                GuarantorAddressSummary.row() mustBe expectedRow(messagesForLanguage.notProvided, showChangeLink = true)
              }
            }

            "when there's no answer in the user answers (defaulting to 801)" - {

              "must output the expected data" in {

                val userAddressFrom801 = UserAddress(None, Some("GuarantorTraderStreetNumber1"), "GuarantorTraderStreetName1", "GuarantorTraderCity1", "GuarantorTraderPostcode1")

                implicit lazy val request: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                    .set(GuarantorArrangerPage, arranger),
                  ern = testGreatBritainWarehouseErn,
                  movementDetails = maxGetMovementResponse
                )

                val expectedValue = HtmlContent(
                  HtmlFormat.fill(
                    Seq(
                      Html(userAddressFrom801.property.fold("")(_ + " ") + userAddressFrom801.street + "<br>"),
                      Html(userAddressFrom801.town + "<br>"),
                      Html(userAddressFrom801.postcode),
                    )
                  )
                )

                GuarantorAddressSummary.row() mustBe expectedRow(expectedValue, true)
              }
            }

            "when there's an answer" - {

              "must output the expected row" in {

                implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
                  .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                  .set(GuarantorArrangerPage, arranger)
                  .set(GuarantorAddressPage, testUserAddress)
                )

                val expectedValue = HtmlContent(
                  HtmlFormat.fill(
                    Seq(
                      Html(testUserAddress.property.fold("")(_ + " ") + testUserAddress.street + "<br>"),
                      Html(testUserAddress.town + "<br>"),
                      Html(testUserAddress.postcode),
                    )
                  )
                )

                GuarantorAddressSummary.row() mustBe expectedRow(expectedValue, true)
              }
            }
          }
        }

        "when the Guarantor is Consignor" - {

          "when there's no answer for the ConsignorAddressPage" - {

            "must use the data from the IE801" in {

              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                  .set(GuarantorArrangerPage, Consignor)
              )

              val expectedValue = HtmlContent(
                HtmlFormat.fill(
                  Seq(
                    Html("ConsignorTraderStreetNumber ConsignorTraderStreetName<br>"),
                    Html("ConsignorTraderCity<br>"),
                    Html("ConsignorTraderPostcode")
                  ))
              )

              GuarantorAddressSummary.row() mustBe expectedRow(expectedValue, false)
            }
          }

          "when there's an answer for the ConsignorAddressPage" - {

            "must output the expected row" in {

              implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
                .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                .set(GuarantorArrangerPage, Consignor)
                .set(ConsignorAddressPage, testUserAddress)
              )

              val expectedValue = HtmlContent(
                HtmlFormat.fill(
                  Seq(
                    Html(testUserAddress.property.fold("")(_ + " ") + testUserAddress.street + "<br>"),
                    Html(testUserAddress.town + "<br>"),
                    Html(testUserAddress.postcode),
                  )))

              GuarantorAddressSummary.row() mustBe expectedRow(expectedValue, false)
            }
          }
        }

        "when the Guarantor is Consignee" - {

          "when there's no answer for the ConsigneeAddressPage in the 801 response or user answers" - {

            "must output the expected data" in {

              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                  .set(GuarantorArrangerPage, Consignee),
                movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
              )

              GuarantorAddressSummary.row() mustBe expectedRow(Text(messagesForLanguage.sectionNotComplete("Consignee")), false)
            }
          }

          "when there's an answer for the ConsigneeAddressPage" - {

            "must output the expected row" in {

              implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
                .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                .set(GuarantorArrangerPage, Consignee)
                .set(ConsigneeAddressPage, testUserAddress)
              )

              val expectedValue = HtmlContent(
                HtmlFormat.fill(
                  Seq(
                    Html(testUserAddress.property.fold("")(_ + " ") + testUserAddress.street + "<br>"),
                    Html(testUserAddress.town + "<br>"),
                    Html(testUserAddress.postcode),
                  )))

              GuarantorAddressSummary.row() mustBe expectedRow(expectedValue, false)
            }
          }
        }
      }
    }
  }
}
