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

package controllers

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

import base.SpecBase
import controllers.routes
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status.SEE_OTHER
import play.api.test.Helpers.{defaultAwaitTimeout, status, writeableOf_AnyContentAsEmpty}
import play.api.test.{FakeRequest, Helpers}

class LanguageSwitchControllerSpec extends SpecBase {

  "when translation is enabled switching language" - {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
      .configure("play.i18n.langs" -> List("en", "cy"))
      .build()

    "set the language to Cymraeg" in {
      val request = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage("cy").url)

      val result = Helpers.route(application, request).get

      status(result)             shouldBe SEE_OTHER
      getLanguageCookies(result) shouldBe "cy"
    }

    "set the language to English" in {
      val request = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage("en").url)

      val result = Helpers.route(application, request).get

      status(result)             shouldBe SEE_OTHER
      getLanguageCookies(result) shouldBe "en"
    }
  }

  "when translation is disabled switching language" - {

    "should set the language to English regardless of what is requested" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .configure("play.i18n.langs" -> List("en"))
        .build()

      val cymraegRequest = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage("cy").url)
      val englishRequest = FakeRequest(Helpers.GET, routes.LanguageSwitchController.switchToLanguage("en").url)

      val cymraegResult = Helpers.route(application, cymraegRequest).get
      val englishResult = Helpers.route(application, englishRequest).get

      status(cymraegResult)             shouldBe SEE_OTHER
      getLanguageCookies(cymraegResult) shouldBe "en"

      status(englishResult)             shouldBe SEE_OTHER
      getLanguageCookies(englishResult) shouldBe "en"
    }
  }

}