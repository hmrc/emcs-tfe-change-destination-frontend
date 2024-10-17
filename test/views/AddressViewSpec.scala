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

package views

import base.SpecBase
import fixtures.messages.AddressMessages
import forms.AddressFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.destination.DestinationAddressPage
import pages.sections.firstTransporter.FirstTransporterAddressPage
import pages.sections.guarantor.GuarantorAddressPage
import pages.sections.transportArranger.TransportArrangerAddressPage
import play.api.i18n.{Lang, Messages}
import play.api.test.FakeRequest
import views.html.AddressView

class AddressViewSpec extends SpecBase with ViewBehaviours {

  class Fixture(lang: Lang) {
    implicit val msgs: Messages = messages(Seq(lang))
    implicit val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

   lazy val view = app.injector.instanceOf[AddressView]
    val form = app.injector.instanceOf[AddressFormProvider].apply()
  }

  object Selectors extends BaseSelectors

  Seq(AddressMessages.English).foreach { messagesForLanguage =>

    s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - new Fixture(messagesForLanguage.lang) {

      Seq(ConsignorAddressPage, ConsigneeAddressPage) foreach { addressPage =>

        s"$addressPage View" - {

          implicit val doc: Document = Jsoup.parse(view(
            form = form,
            addressPage = addressPage,
            call = testOnwardRoute
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(addressPage, Some(messagesForLanguage.subheading(addressPage))),
            Selectors.h1 -> messagesForLanguage.heading(addressPage),
            Selectors.h2(1) -> messagesForLanguage.subheading(addressPage),
            Selectors.label("property") -> messagesForLanguage.property,
            Selectors.label("street") -> messagesForLanguage.street,
            Selectors.label("town") -> messagesForLanguage.town,
            Selectors.label("postcode") -> messagesForLanguage.postcode,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))
        }
      }

      "when rendered for TransportArranger page" - {

        "when the Arranger is the GoodsOwner" - new Fixture(messagesForLanguage.lang) {

          implicit val doc: Document = Jsoup.parse(view(
            form = form,
            addressPage = TransportArrangerAddressPage,
            call = testOnwardRoute,
            headingKey = Some(s"$TransportArrangerAddressPage.$GoodsOwner")
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.transportArrangerAddressGoodsOwnerTitle,
            Selectors.h1 -> messagesForLanguage.transportArrangerAddressGoodsOwnerHeading,
            Selectors.h2(1) -> messagesForLanguage.subheading(TransportArrangerAddressPage),
            Selectors.label("property") -> messagesForLanguage.property,
            Selectors.label("street") -> messagesForLanguage.street,
            Selectors.label("town") -> messagesForLanguage.town,
            Selectors.label("postcode") -> messagesForLanguage.postcode,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))
        }

        "when the Arranger is Other" - new Fixture(messagesForLanguage.lang) {

          implicit val doc: Document = Jsoup.parse(view(
            form = form,
            addressPage = TransportArrangerAddressPage,
            call = testOnwardRoute,
            headingKey = Some(s"$TransportArrangerAddressPage.$Other")
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.transportArrangerAddressOtherTitle,
            Selectors.h1 -> messagesForLanguage.transportArrangerAddressOtherHeading,
            Selectors.h2(1) -> messagesForLanguage.subheading(TransportArrangerAddressPage)
          ))
        }
      }

      "when rendered for FirstTransporterAddress page" - new Fixture(messagesForLanguage.lang) {

        implicit val doc: Document = Jsoup.parse(view(
          form = form,
          addressPage = FirstTransporterAddressPage,
          call = controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onSubmit(request.ern, request.arc, NormalMode),
          headingKey = Some("firstTransporterAddress")
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.firstTransporterAddressTitle,
          Selectors.h1 -> messagesForLanguage.firstTransporterAddressHeading,
          Selectors.h2(1) -> messagesForLanguage.subheading(FirstTransporterAddressPage),
          Selectors.label("property") -> messagesForLanguage.property,
          Selectors.label("street") -> messagesForLanguage.street,
          Selectors.label("town") -> messagesForLanguage.town,
          Selectors.label("postcode") -> messagesForLanguage.postcode,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }

      "when rendered for DestinationAddress page" - new Fixture(messagesForLanguage.lang) {

        implicit val doc: Document = Jsoup.parse(view(
          form = form,
          addressPage = DestinationAddressPage,
          call = controllers.sections.destination.routes.DestinationAddressController.onSubmit(request.ern, request.arc, NormalMode),
          headingKey = Some("destinationAddress")
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.destinationAddressTitle,
          Selectors.h1 -> messagesForLanguage.destinationAddressHeading,
          Selectors.h2(1) -> messagesForLanguage.subheading(DestinationAddressPage),
          Selectors.label("property") -> messagesForLanguage.property,
          Selectors.label("street") -> messagesForLanguage.street,
          Selectors.label("town") -> messagesForLanguage.town,
          Selectors.label("postcode") -> messagesForLanguage.postcode,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }


      Seq(
        models.sections.guarantor.GuarantorArranger.GoodsOwner,
        models.sections.guarantor.GuarantorArranger.Transporter,
      ).foreach(
        guarantorArranger =>
          s"when rendered for GuarantorAddress page with a guarantor arranger of ${guarantorArranger.getClass.getSimpleName.stripSuffix("$")}" -
            new Fixture(messagesForLanguage.lang) {

              implicit val doc: Document = Jsoup.parse(view(
                form = form,
                addressPage = GuarantorAddressPage,
                call = controllers.sections.guarantor.routes.GuarantorAddressController.onSubmit(request.ern, request.arc, NormalMode),
                headingKey = Some(s"guarantorAddress.$guarantorArranger")
              ).toString())

              behave like pageWithExpectedElementsAndMessages(Seq(
                Selectors.title -> messagesForLanguage.guarantorAddressTitle(guarantorArranger),
                Selectors.h1 -> messagesForLanguage.guarantorAddressHeading(guarantorArranger),
                Selectors.h2(1) -> messagesForLanguage.subheading(GuarantorAddressPage),
                Selectors.label("property") -> messagesForLanguage.property,
                Selectors.label("street") -> messagesForLanguage.street,
                Selectors.label("town") -> messagesForLanguage.town,
                Selectors.label("postcode") -> messagesForLanguage.postcode,
                Selectors.button -> messagesForLanguage.saveAndContinue,
                Selectors.link(1) -> messagesForLanguage.returnToDraft
              ))
            }
      )
    }
  }
}
