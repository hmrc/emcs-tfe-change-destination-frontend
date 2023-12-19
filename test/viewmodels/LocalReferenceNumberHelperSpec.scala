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

package viewmodels

import base.SpecBase
import fixtures.messages.sections.info.LocalReferenceNumberMessages
import forms.sections.info.LocalReferenceNumberFormProvider
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}

class LocalReferenceNumberHelperSpec extends SpecBase with GuiceOneAppPerSuite {

  lazy val helper = app.injector.instanceOf[LocalReferenceNumberHelper]
  lazy val form = app.injector.instanceOf[LocalReferenceNumberFormProvider]
  lazy val p = app.injector.instanceOf[views.html.components.p]

  "LocalReferenceNumberHelper" - {

    Seq(LocalReferenceNumberMessages.English).foreach { langMessages =>

      implicit lazy val msgs: Messages = messages(Seq(langMessages.lang))

      s"when running for language code of '${langMessages.lang.code}'" - {

        "calling .title(isDeferred: Boolean)" - {

          "when the movement is deferred" - {

            "must output the expected title" in {
              helper.title(form(isDeferred = true), isDeferred = true) mustBe langMessages.deferredTitle
            }
          }

          "when the movement is NOT deferred" - {

            "must output the expected title" in {
              helper.title(form(isDeferred = false), isDeferred = false) mustBe langMessages.newTitle
            }
          }
        }

        "calling .heading(isDeferred: Boolean)" - {

          "when the movement is deferred" - {

            "must output the expected title" in {
              helper.heading(isDeferred = true) mustBe langMessages.deferredHeading
            }
          }

          "when the movement is NOT deferred" - {

            "must output the expected title" in {
              helper.heading(isDeferred = false) mustBe langMessages.newHeading
            }
          }
        }

        "calling .content(isDeferred: Boolean)" - {

          "when the movement is deferred" - {

            "must output the expected title" in {
              helper.content(isDeferred = true) mustBe HtmlFormat.fill(Seq(
                p()(Html(langMessages.deferredP1))
              ))
            }
          }

          "when the movement is NOT deferred" - {

            "must output the expected title" in {
              helper.content(isDeferred = false) mustBe HtmlFormat.fill(Seq(
                p()(Html(langMessages.newP1)),
                p()(Html(langMessages.newP2))
              ))
            }
          }
        }
      }
    }
  }
}
