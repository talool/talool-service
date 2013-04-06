################################################################
#
# Generates a test DB called "talool-test"
#   Reads original scheam and replaces "talool"
#
# Please note you must have ~/.pgpass working so no prompts occur
#    Example:
#
#    localhost:5432:talool-test:talool:XXXXXX
#    localhost:5432:postgres:postgres:XXXXXX
#
##################################################################

taloolSchema=$(dirname $0)/talool-schema.sql
taloolData=$(dirname $0)/talool-data.sql
testDbName="talool"

echo "Dropping Database '$testDbName' ..."
dropdb -U postgres -w $testDbName

if [ $? -eq 1 ]; then
  exit
fi

echo "Creating Database '$testDbName' ..."
createdb -U postgres -w $testDbName

echo "Importing $testDbName schema..."
psql -U postgres -w $testDbName < $taloolSchema

#echo "Importing $taloolData into Database '$testDbName' ..."
#psql -U postgres -w $testDbName < $taloolData
