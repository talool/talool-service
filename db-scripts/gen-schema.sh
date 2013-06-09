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
set -e

if [ $# -ne 3 ]
then
  echo "Usage: `basename $0` schema.sql data.sql /Library/PostgreSQL/9.2/share/postgresql/contrib/postgis/" 
  echo "Usage: `basename $0` talool-schema.sql talool-data.sql /usr/pgsql-9.2/share/contrib/postgis-2.0/" 
exit -1
fi

taloolSchema=$(dirname $0)/$1
taloolData=$(dirname $0)/$2
#postGisDir=/Library/PostgreSQL/9.2/share/postgresql/contrib/postgis/
postGisDir=$3
dbName="talool"

echo "Dropping Database '$dbName' ..."
dropdb --if-exists -U postgres -w $dbName

#echo "Creating Database '$dbName' ..."
createdb -T template0 -E UTF8 -U postgres -w $dbName

echo "Installing PostGIS '$dbName' ..."
psql -U postgres -d $dbName -f $postGisDir/postgis.sql
psql -U postgres -d $dbName -f $postGisDir/spatial_ref_sys.sql
 
echo "Importing $dbName schema..."
psql -U postgres -w $dbName < $taloolSchema

echo "Importing $taloolData into Database '$dbName' ..."
psql -U postgres -w $dbName < $taloolData
