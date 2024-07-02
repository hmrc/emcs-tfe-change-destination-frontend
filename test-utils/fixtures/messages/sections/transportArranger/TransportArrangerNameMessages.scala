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

package fixtures.messages.sections.transportArranger

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}
import models.sections.transportArranger.TransportArranger
import models.sections.transportArranger.TransportArranger.GoodsOwner

object TransportArrangerNameMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def title()(implicit transportArranger: TransportArranger): String = transportArranger match {
      case GoodsOwner => titleHelper(heading())
      case _ => titleHelper(heading())
    }

    def heading()(implicit transportArranger: TransportArranger): String = transportArranger match {
      case GoodsOwner => "What is the goods owner’s business name?"
      case _ => "What is the transport arranger’s business name?"
    }

    val cyaLabel = "Business name"
    val cyaChangeHidden = "transport arranger name"
  }

  object English extends ViewMessages with BaseEnglish


}
