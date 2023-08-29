
# emcs-tfe-change-destination-frontend

Contains the frontend journey for EMCS TFE to change a destination for a movement. Using hmrc-scaffold-g8.

## How to build pages with the Scaffolding

1) Enter an `sbt` shell by issuing the command `sbt` in your terminal window. _(alternative, if using IntelliJ click on the `sbt shell` tab in the bottom panel)_


2) Once the sbt shell has initialised enter the command `g8Scaffold template` where `template` is replaced with one of the following available scaffolding templates:
    1) `checkboxPage`
    2) `contentPage`
    3) `datePage`
    4) `intPage`
    5) `multipleQuestionsPage`
    6) `radioButtonPage`
    7) `stringPage`
    8) `yesNoPage`
    9) `characterCountPage`


3) The g8Scaffolding will prompt for values to properties. Such as the `className`


4) Enter appropriate values for each prompt


5) Once the g8Scaffold says `success :)`, exit out of the sbt shell and back to a normal terminal window


6) Run the script `./migrate.sh` to run the migrations _(this creates the messages entries and the routes entries - along with a few other things)_


7) Your page is ready to be refactored and enhanced with business rules, content changes and anything else.


8) **IMPORTANT:**
    1) Using the scaffolds to generate tests is not a replacement for writing good tests. Please refactor and enhance the default tests with tests which cover extra logic and business rules
    2) Scaffold are here to help us get a head start with simple pages, but do refactor the generated code to improve quality where appropriate
    3) If you find an improvement that could be applied to the Scaffolds update the `g8` template so that this can be used by future Engineers

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").