package controllers

import base.SpecBase
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers
import play.api.test.Helpers._
import views.html.$className$View

class $className$ControllerSpec extends SpecBase {

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val view = app.injector.instanceOf[$className$View]

  lazy val controller = new $className$Controller(
    messagesApi,
    Helpers.stubMessagesControllerComponents(),
    view
  )

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {

      val result = controller.onPageLoad()(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view()(request, messages).toString
    }
  }
}
