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

if [ $# -ne 3 ]
then
  echo "Usage: `basename $0` schema.sql data.sql /Library/PostgreSQL/9.2/share/postgresql/contrib/postgis/"
  exit -1
fi

testSchema=$(dirname $0)/test-schema.sql
taloolSchema=$(dirname $0)/$1
taloolData=$(dirname $0)/$2
testDbName="talooltest"
postGisDir=$3

echo "Dropping Database '$testDbName' ..."
dropdb -U postgres -w $testDbName

echo "Creating $testSchema from $taloolSchema..."
sedStmt="s/CREATE DATABASE talool/CREATE DATABASE $testDbName/g;s/ALTER DATABASE talool/ALTER DATABASE $testDbName/g;s/connect talool/connect $testDbName/"

sed -e "$sedStmt" $taloolSchema > $testSchema 

echo "Creating Database '$testDbName' ..."
createdb -U postgres -w $testDbName

echo "Installing PostGIS '$dbName' ..."
psql -U postgres -d $testDbName -f $postGisDir/postgis.sql

psql -U postgres -d $testDbName -f $postGisDir/spatial_ref_sys.sql

echo "Importing $testDbName schema..."
psql -U postgres -w $testDbName < $testSchema

echo "Importing $taloolData into Database '$testDbName' ..."
psql -U postgres -w $testDbName < $taloolData
