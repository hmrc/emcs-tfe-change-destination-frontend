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
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages
echo "$className;format="decap"$.checkYourAnswersLabel = $className;format="decap"$" >> ../conf/messages
echo "$className;format="decap"$.error.required = Enter $className;format="decap"$" >> ../conf/messages
echo "$className;format="decap"$.error.length = $className$ must be $maxLength$ characters or less" >> ../conf/messages
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages

echo "Migration $className;format="snake"$ completed"
