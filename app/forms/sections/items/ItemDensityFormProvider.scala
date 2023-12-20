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

package forms.sections.items

import forms.mappings.Mappings
import forms.sections.items.ItemDensityFormProvider.{itemDensityFormField, itemDensityFormatter}
import models.GoodsType
import play.api.data.Forms.of
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}
import play.api.i18n.Messages

import javax.inject.Inject
import scala.math.BigDecimal.javaBigDecimal2bigDecimal
import scala.util.{Failure, Success, Try}

class ItemDensityFormProvider @Inject() extends Mappings {

  def apply(goodsType: GoodsType)(implicit messages: Messages): Form[BigDecimal] = Form(
    itemDensityFormField -> of(itemDensityFormatter(goodsType))
  )
}

object ItemDensityFormProvider {
  val itemDensityFormField = "itemDensity"
  val errorRequiredKey = "itemDensity.error.required"
  val errorLengthKey = "itemDensity.error.length"

  def itemDensityFormatter(goodsType: GoodsType)(implicit messages: Messages): Formatter[BigDecimal] = new Formatter[BigDecimal] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], BigDecimal] = {
      val goodsTypeName: String = goodsType.toSingularOutput()

      data.get(key) match {
        case None =>
          Left(Seq(FormError(key, errorRequiredKey, Seq(goodsTypeName))))
        case Some(itemDensity) if itemDensity.isEmpty =>
          Left(Seq(FormError(key, errorRequiredKey, Seq(goodsTypeName))))
        case Some(itemDensity) =>
          Try(BigDecimal(itemDensity).underlying().stripTrailingZeros()) match {
            case Success(itemDensityNumeric) =>
              val numberOfDecimalPlaces = Math.max(0, itemDensityNumeric.scale)
              val totalDigits = itemDensityNumeric.precision

              if (numberOfDecimalPlaces <= 2 && totalDigits <= 5 && itemDensityNumeric >= 0) {
                Right(itemDensityNumeric)
              } else {
                Left(Seq(FormError(key, errorLengthKey)))
              }
            case Failure(_) =>
              Left(Seq(FormError(key, errorLengthKey)))
          }
      }
    }

    override def unbind(key: String, value: BigDecimal): Map[String, String] =
      Map(key -> value.toString)
  }
}
