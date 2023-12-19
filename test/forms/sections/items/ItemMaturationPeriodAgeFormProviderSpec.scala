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

import fixtures.messages.sections.items.ItemMaturationPeriodAgeMessages.English
import forms.XSS_REGEX
import forms.behaviours.BooleanFieldBehaviours
import models.GoodsType.Wine
import models.sections.items.ItemMaturationPeriodAgeModel
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.MessagesApi

class ItemMaturationPeriodAgeFormProviderSpec extends BooleanFieldBehaviours with GuiceOneAppPerSuite {

  implicit lazy val messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))
  lazy val form = new ItemMaturationPeriodAgeFormProvider()(Wine)

  "when binding 'Yes'" - {

    "when Maturation Period Age contains invalid characters" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          ItemMaturationPeriodAgeFormProvider.hasMaturationPeriodAgeField -> "true",
          ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeField -> "<"
        ))

        boundForm.errors mustBe Seq(FormError(
          ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeField,
          ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeInvalid,
          Seq(XSS_REGEX)
        ))
      }
    }

    "when Maturation Period Age is too long" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          ItemMaturationPeriodAgeFormProvider.hasMaturationPeriodAgeField -> "true",
          ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeField -> "a" * (ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeMaxLength + 1)
        ))

        boundForm.errors mustBe Seq(FormError(
          ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeField,
          ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeLength,
          Seq(ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeMaxLength)
        ))
      }
    }

    "when Maturation Period Age is valid" - {

      "must bind the form successfully when true with value" in {

        val boundForm = form.bind(Map(
          ItemMaturationPeriodAgeFormProvider.hasMaturationPeriodAgeField -> "true",
          ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeField -> "14 Years"
        ))

        boundForm.errors mustBe Seq()

        boundForm.value mustBe Some(ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("14 Years")))
      }
    }
  }

  "when binding 'No'" - {

    "must bind the form successfully when false with value (should be transformed to None on bind)" in {

      val boundForm = form.bind(Map(
        ItemMaturationPeriodAgeFormProvider.hasMaturationPeriodAgeField -> "false",
        ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeField -> "14 Years"
      ))

      boundForm.errors mustBe Seq()

      boundForm.value mustBe Some(ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = false, None))
    }

    "must bind the form successfully when false with NO value" in {

      val boundForm = form.bind(Map(
        ItemMaturationPeriodAgeFormProvider.hasMaturationPeriodAgeField -> "false"
      ))

      boundForm.errors mustBe Seq()

      boundForm.value mustBe Some(ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = false, None))
    }
  }

  "when binding no value for the radio option" - {

    "must bind with errors" in {

      val boundForm = form.bind(Map(
        ItemMaturationPeriodAgeFormProvider.hasMaturationPeriodAgeField -> ""
      ))

      boundForm.errors mustBe Seq(FormError(
        ItemMaturationPeriodAgeFormProvider.hasMaturationPeriodAgeField,
        ItemMaturationPeriodAgeFormProvider.radioRequired,
        Seq(messages(Wine.toSingularOutput()))
      ))
    }
  }
}
