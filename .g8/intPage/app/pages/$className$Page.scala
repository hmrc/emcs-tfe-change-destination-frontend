package pages

import play.api.libs.json.JsPath
import pages.QuestionPage

case object $className$Page extends QuestionPage[Int] {
  override val toString: String = "$className;format="decap"$"
  override val path: JsPath = JsPath \ toString
}
