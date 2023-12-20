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

package fixtures.messages.sections.items

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}
import models.Index

object ItemCheckAnswersMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    val heading = "Check your answers"

    val title = titleHelper(heading)

    def subheading(idx: Index): String = s"Item ${idx.displayIndex}"

    val cardTitleItemDetails = "Item details"
    val cardTitleQuantity = "Quantity"
    val cardTitleWineDetails = "Wine details"
    val cardTitlePackaging = "Packaging"

    def packagingKey(idx: Index): String = s"Packaging type ${idx.displayIndex}"
    def packagingValue(quantity: String, packagingType: String): String = s"${quantity}x $packagingType"
  }

  object English extends ViewMessages with BaseEnglish

}
