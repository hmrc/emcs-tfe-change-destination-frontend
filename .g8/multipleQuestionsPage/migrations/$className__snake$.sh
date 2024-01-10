#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trader/:ern/movement/:arc/$className;format="decap"$                        controllers.$className$Controller.onPageLoad(ern: String, arc: String, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trader/:ern/movement/:arc/$className;format="decap"$                        controllers.$className$Controller.onSubmit(ern: String, arc: String, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /trader/:ern/movement/:arc/$className$/change                  controllers.$className$Controller.onPageLoad(ern: String, arc: String, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /trader/:ern/movement/:arc/$className$/change                  controllers.$className$Controller.onSubmit(ern: String, arc: String, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages
echo "$className;format="decap"$.$field1Name$ = $field1Name$" >> ../conf/messages
echo "$className;format="decap"$.$field2Name$ = $field2Name$" >> ../conf/messages
echo "$className;format="decap"$.checkYourAnswersLabel = $className$" >> ../conf/messages
echo "$className;format="decap"$.error.$field1Name$.required = Enter $field1Name$" >> ../conf/messages
echo "$className;format="decap"$.error.$field2Name$.required = Enter $field2Name$" >> ../conf/messages
echo "$className;format="decap"$.error.$field1Name$.length = $field1Name$ must be $field1MaxLength$ characters or less" >> ../conf/messages
echo "$className;format="decap"$.error.$field2Name$.length = $field2Name$ must be $field2MaxLength$ characters or less" >> ../conf/messages
echo "$className;format="decap"$.$field1Name$.change.hidden = $field1Name$" >> ../conf/messages
echo "$className;format="decap"$.$field2Name$.change.hidden = $field2Name$" >> ../conf/messages

echo "Migration $className;format="snake"$ completed"
