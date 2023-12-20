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

package forms.sections.info

import base.SpecBase
import fixtures.messages.sections.info.LocalReferenceNumberMessages
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import play.api.i18n.Messages

class LocalReferenceNumberFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val fieldName = "value"
  val maxLength = 22

  "LocalReferenceNumberFormProvider" - {

    "when movement is deferred" - {

      val form = new LocalReferenceNumberFormProvider().apply(isDeferred = true)

      "when a value is not provided" - {

        "must error with the expected msg key" in {

          val boundForm = form.bind(Map(fieldName -> ""))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "localReferenceNumber.deferred.error.required", Seq()))
        }
      }

      "when the value is too long" - {

        "must error with the expected msg key" in {

          val boundForm = form.bind(Map(fieldName -> "A" * (maxLength + 1)))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "localReferenceNumber.deferred.error.length", Seq(maxLength)))
        }
      }
    }

    "when movement is NOT deferred (new)" - {

      val form = new LocalReferenceNumberFormProvider().apply(isDeferred = false)

      "when a value is not provided" - {

        "must error with the expected msg key" in {

          val boundForm = form.bind(Map(fieldName -> ""))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "localReferenceNumber.new.error.required", Seq()))
        }
      }

      "when the value is too long" - {

        "must error with the expected msg key" in {

          val boundForm = form.bind(Map(fieldName -> "A" * (maxLength + 1)))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "localReferenceNumber.new.error.length", Seq(maxLength)))
        }
      }

      "when the value is equal to the max length" - {

        "must bind successfully" in {

          val boundForm = form.bind(Map(fieldName -> "A" * maxLength))
          boundForm.value mustBe Some("A" * maxLength)
        }
      }
    }
  }

  "Error Messages" - {

    Seq(LocalReferenceNumberMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message for required (deferred)" in {
          msgs("localReferenceNumber.deferred.error.required") mustBe
            messagesForLanguage.deferredErrorRequired
        }

        "have the correct error message for required (new)" in {
          msgs("localReferenceNumber.new.error.required") mustBe
            messagesForLanguage.newErrorRequired
        }

        "have the correct error message for length (deferred)" in {
          msgs("localReferenceNumber.deferred.error.length") mustBe
            messagesForLanguage.deferredErrorLength
        }

        "have the correct error message for length (new)" in {
          msgs("localReferenceNumber.new.error.length") mustBe
            messagesForLanguage.newErrorLength
        }
      }
    }
  }
}
