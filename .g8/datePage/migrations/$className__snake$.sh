#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trader/:ern/movement/:arc/$className;format="decap"$                  controllers.$className$Controller.onPageLoad(ern: String, arc: String, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trader/:ern/movement/:arc/$className;format="decap"$                  controllers.$className$Controller.onSubmit(ern: String, arc: String, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /trader/:ern/movement/:arc/$className$/change                        controllers.$className$Controller.onPageLoad(ern: String, arc: String, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /trader/:ern/movement/:arc/$className$/change                        controllers.$className$Controller.onSubmit(ern: String, arc: String, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages
echo "$className;format="decap"$.title = $className$" >> ../conf/messages
echo "$className;format="decap"$.heading = $className$" >> ../conf/messages
echo "$className;format="decap"$.hint = For example, 12 11 2007" >> ../conf/messages
echo "$className;format="decap"$.checkYourAnswersLabel = $className$" >> ../conf/messages
echo "$className;format="decap"$.error.required.all = Enter the $className;format="decap"$" >> ../conf/messages
echo "$className;format="decap"$.error.required.two = The $className;format="decap"$" must include {0} and {1} >> ../conf/messages
echo "$className;format="decap"$.error.required = The $className;format="decap"$ must include {0}" >> ../conf/messages
echo "$className;format="decap"$.error.invalid = Enter a real $className$" >> ../conf/messages
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages

echo "Migration $className;format="snake"$ completed"
