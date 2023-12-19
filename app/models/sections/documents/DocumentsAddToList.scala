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

package models.sections.documents

import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.ViewUtils.pluralSingular

sealed trait DocumentsAddToList

object DocumentsAddToList extends Enumerable.Implicits {

  case object Yes extends WithName("1") with DocumentsAddToList

  case object No extends WithName("2") with DocumentsAddToList

  case object MoreLater extends WithName("3") with DocumentsAddToList

  val values: Seq[DocumentsAddToList] = Seq(
    Yes, No, MoreLater
  )

  def options(totalDocuments: Int, showNoOption: Boolean)(implicit messages: Messages): Seq[RadioItem] = {

    def radioItem(value: DocumentsAddToList, index: Int, totalDocuments: Option[Int]): RadioItem = RadioItem(
      content = totalDocuments match {
        case None => Text(messages(s"documentsAddToList.${value.toString}"))
        case Some(amount) => Text(pluralSingular(s"documentsAddToList.${value.toString}", amount))
      },
      value = Some(value.toString),
      id = Some(s"value_$index")
    )

    val orDivider = RadioItem(
      divider = Some(messages(s"documentsAddToList.divider"))
    )

    Seq(
      Some(radioItem(Yes, 0, None)),
      if (showNoOption) Some(radioItem(No, 1, Some(totalDocuments))) else None,
      if (showNoOption) Some(orDivider) else None,
      Some(radioItem(MoreLater, 2, None))
    ).flatten
  }

  implicit val enumerable: Enumerable[DocumentsAddToList] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
