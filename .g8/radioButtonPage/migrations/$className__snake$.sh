#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trader/:ern/draft/:draftId/$className;format="decap"$                        controllers.$className$Controller.onPageLoad(ern: String, draftId: String, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trader/:ern/draft/:draftId/$className;format="decap"$                        controllers.$className$Controller.onSubmit(ern: String, draftId: String, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /trader/:ern/draft/:draftId/$className$/change                  controllers.$className$Controller.onPageLoad(ern: String, draftId: String, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /trader/:ern/draft/:draftId/$className$/change                  controllers.$className$Controller.onSubmit(ern: String, draftId: String, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages
echo "$className;format="decap"$.title = $title$" >> ../conf/messages
echo "$className;format="decap"$.heading = $title$" >> ../conf/messages
echo "$className;format="decap"$.$option1key;format="decap"$ = $option1msg$" >> ../conf/messages
echo "$className;format="decap"$.$option2key;format="decap"$ = $option2msg$" >> ../conf/messages
echo "$className;format="decap"$.checkYourAnswersLabel = $title$" >> ../conf/messages
echo "$className;format="decap"$.error.required = Select $className;format="decap"$" >> ../conf/messages
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages

echo "Migration $className;format="snake"$ completed"
