package pages

import pages.QuestionPage
import play.api.libs.json.JsPath

case object $className$Page extends QuestionPage[String] {
  override val toString: String = "$className;format="decap"$"
  override val path: JsPath = JsPath \ toString
}
