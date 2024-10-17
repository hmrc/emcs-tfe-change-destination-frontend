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


object GuarantorNameMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    def title()(implicit guarantorArranger: GuarantorArranger): String = guarantorArranger match {
      case GoodsOwner => titleHelper(heading(), Some(SectionMessages.English.guarantorSubHeading))
      case _ => titleHelper(heading(), Some(SectionMessages.English.guarantorSubHeading))
    }

    def heading()(implicit guarantorArranger: GuarantorArranger): String = guarantorArranger match {
      case GoodsOwner => "What is the goods owner’s business name?"
      case _ => "What is the transporter’s business name?"
    }

    val cyaLabel = "Business name"
    val cyaChangeHidden = "business name"

    val consigneeNameNotProvided = "Consignee section not complete"
    val consignorNameNotProvided = "Consignor section not complete"
  }

  object English extends ViewMessages with BaseEnglish
}
