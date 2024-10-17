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

package fixtures.messages.sections.guarantor

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.GoodsOwner


object GuarantorVatMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    def title()(implicit guarantorArranger: GuarantorArranger): String = guarantorArranger match {
      case GoodsOwner => titleHelper(heading(), Some(SectionMessages.English.guarantorSubHeading))
      case _ => titleHelper(heading(), Some(SectionMessages.English.guarantorSubHeading))
    }

    def heading()(implicit guarantorArranger: GuarantorArranger): String = guarantorArranger match {
      case GoodsOwner => "What is the goods owner’s VAT registration number?"
      case _ => "What is the transporter’s VAT registration number?"
    }

    def notVatRegisteredLink()(implicit guarantorArranger: GuarantorArranger): String = guarantorArranger match {
      case GoodsOwner => "The goods owner is not VAT registered"
      case _ => "The transporter is not VAT registered"

    }

    val cyaLabel = "VAT registration number"
    val cyaChangeHidden = "VAT registration number"

    val cyaErnLabel = "Excise registration number (ERN)"
    val cyaErnNumberForTemporaryRegisteredConsignee: String = "Identification number for Temporary Registered Consignee"
    val cyaErnNumberForTemporaryCertifiedConsignee: String = "Identification number for Temporary Certified Consignee"
    val cyaVatNumberForExports: String = "Identification number"
    val cyaVatChoiceLabel = "VAT registered in the UK"
    val cyaVatInputLabel = "VAT registration number"

    val consigneeErnNotProvided = "Consignee section not complete"
  }

  object English extends ViewMessages with BaseEnglish
}
