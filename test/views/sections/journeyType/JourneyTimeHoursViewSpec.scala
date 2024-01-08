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

package views.sections.journeyType

import base.SpecBase
import fixtures.messages.sections.journeyType.JourneyTimeHoursMessages
import forms.sections.journeyType.JourneyTimeHoursFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.journeyType.JourneyTimeHoursView
import views.{BaseSelectors, ViewBehaviours}

class JourneyTimeHoursViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "JourneyTimeHoursView" - {

    Seq(JourneyTimeHoursMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

       lazy val view = app.injector.instanceOf[JourneyTimeHoursView]
        val form = app.injector.instanceOf[JourneyTimeHoursFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, NormalMode).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.journeyTypeSection,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.inputSuffix -> messagesForLanguage.suffix,
          Selectors.link(1) -> messagesForLanguage.toJourneyDaysLink,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))

        "must have the correct link to the CAM-JT03 page" in {
          doc.select(Selectors.link(1)).attr("href") mustBe
            controllers.sections.journeyType.routes.JourneyTimeDaysController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.arc, NormalMode).url
        }
      }
    }
  }
}
