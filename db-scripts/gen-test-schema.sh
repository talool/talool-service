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

testSchema=$(dirname $0)/test-schema.sql
taloolSchema=$(dirname $0)/talool-schema.sql
taloolData=$(dirname $0)/talool-data.sql
testDbName="talooltest"

echo "Dropping Database '$testDbName' ..."
dropdb -U postgres -w $testDbName

echo "Creating $testSchema from $taloolSchema..."
sedStmt="s/CREATE DATABASE talool/CREATE DATABASE $testDbName/g;s/ALTER DATABASE talool/ALTER DATABASE $testDbName/g;s/connect talool/connect $testDbName/"

sed -e "$sedStmt" talool-schema.sql > $testSchema 

echo "Creating Database '$testDbName' ..."
createdb -U postgres -w $testDbName

echo "Importing $testDbName schema..."
psql -U postgres -w $testDbName < $testSchema

echo "Importing $taloolData into Database '$testDbName' ..."
psql -U postgres -w $testDbName < $taloolData
