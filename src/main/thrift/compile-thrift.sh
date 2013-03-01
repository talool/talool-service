#  This requires that you install Thrift binaries and compiler
#
#!/bin/sh

scriptDir=$(dirname $0)

#/opt/local/bin/thrift -v -o $scriptDir --gen java:java5 src/main/thrift/talool-service.thrift 

/usr/local/bin/thrift -v -o $scriptDir --gen java:java5 src/main/thrift/talool-service.thrift 

/usr/local/bin/thrift -v -o $scriptDir --gen cocoa src/main/thrift/talool-service.thrift

cp -r  $scriptDir/gen-java/* src/main/java/.
rm -rf $scriptDir/gen-java

