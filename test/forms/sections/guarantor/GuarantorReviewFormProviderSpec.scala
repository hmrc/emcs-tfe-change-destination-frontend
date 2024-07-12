/*
 * Copyright 2024 HM Revenue & Customs
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

package forms.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorReviewMessages
import forms.behaviours.OptionFieldBehaviours
import models.sections.ReviewAnswer
import play.api.data.FormError
import play.api.i18n.Messages

class GuarantorReviewFormProviderSpec extends SpecBase with OptionFieldBehaviours {

  val requiredKey = "guarantorReview.error.required"
  val invalidKey = "error.invalid"

  val form = new GuarantorReviewFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like optionsField[ReviewAnswer](
      form,
      fieldName,
      validValues = ReviewAnswer.values,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "Error messages" - {

    Seq(GuarantorReviewMessages.English).foreach { messagesForLang =>

      implicit val msgs: Messages = messages(Seq(messagesForLang.lang))

      s"when rendering with lang code of '${messagesForLang.lang}'" - {

        "have the correct mandatory wording" in {

          msgs(requiredKey) mustBe messagesForLang.errorRequired
        }
      }
    }
  }
}
