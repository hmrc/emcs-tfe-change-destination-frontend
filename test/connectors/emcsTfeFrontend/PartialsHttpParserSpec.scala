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

package connectors.emcsTfeFrontend

import base.SpecBase
import play.api.http.Status
import play.twirl.api.Html
import uk.gov.hmrc.http.HttpResponse

class PartialsHttpParserSpec extends SpecBase with Status with PartialsHttpParser {

  val htmlString = "<div><p>hello</p></div>"

  "PartialReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return a successful response" - {

      "when valid HTML is returned that can be parsed to the model" in {

        val httpResponse = HttpResponse(Status.OK, "<div><p>hello</p></div>")

        PartialReads.read("GET", s"/emcs/partial/navigation/trader/$testErn", httpResponse) mustBe Some(Html(htmlString))
      }
    }

    "should return None" - {

      s"when status is not OK (${Status.OK})" in {
        val httpResponse = HttpResponse(Status.NO_CONTENT, "")
        PartialReads.read("GET", s"/emcs/partial/navigation/trader/$testErn", httpResponse) mustBe None
      }
    }
  }
}
