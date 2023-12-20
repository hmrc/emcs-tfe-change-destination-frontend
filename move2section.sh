#!/bin/bash

# first argument is the section name i.e journeyType / transportArranger
SECTION=${1:?Usage: $0 <<SECTION NAME GOES HERE>>}

# list of all untracked git files (after you've just run ./migrate.sh)
FILES="$(git ls-files --others --exclude-standard)"

# for each untracked git file
for f in $FILES
do
	# get the path and file separately
	DIR="$(dirname "${f}")"
	FILE="$(basename "${f}")"

  # Create all intermediate directories (-p)
  mkdir -p $DIR/sections/$SECTION

  echo "moving file $DIR/$FILE into section $DIR/sections/$SECTION/$FILE"
	mv -f $DIR/$FILE $DIR/sections/$SECTION/$FILE

  echo "updating package of $DIR/sections/$SECTION/$FILE setting to *.sections.$SECTION"
  sed -i '' -e "/^package/ s/$/.sections.$SECTION/" $DIR/sections/$SECTION/$FILE

done

# Reminder for actions not carried out
echo "Manually move your routes from the app.routes file to $SECTION.routes file"
echo "and update the location of the controller in the creation routes"
echo "Add your page to the relevant navigation file"