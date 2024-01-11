package pages

import models.$className$
import pages.QuestionPage
import play.api.libs.json.JsPath

case object $className$Page extends QuestionPage[Set[$className$]] {
  override val toString: String = "$className;format="decap"$"
  override val path: JsPath = JsPath \ toString
}
