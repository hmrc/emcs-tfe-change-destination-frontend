package pages

import java.time.LocalDate
import pages.QuestionPage
import play.api.libs.json.JsPath

case object $className$Page extends QuestionPage[LocalDate] {
  override val toString: String = "$className;format="decap"$"
  override val path: JsPath = JsPath \ toString
}