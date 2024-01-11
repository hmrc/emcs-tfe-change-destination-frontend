package forms

import forms.behaviours.DateBehaviours

class $className$FormProviderSpec extends DateBehaviours {

  val form = new $className$FormProvider()()

  ".value" - {

    behave like dateField(form, "value")

    behave like mandatoryDateField(form, "value", "$className;format="decap"$.error.required.all")
  }
}
