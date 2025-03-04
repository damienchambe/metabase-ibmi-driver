# metabase-db2-driver for ibm i ("AS/400") on Power
v7r1 - v7r5 ibm i DB2 Driver for Metabase

The metabase driver for DB2 LUW (Linux/Unix/Windows) is here https://github.com/alisonrafael/metabase-db2-driver

Use the provided .jar on the release page, with the latest jtopen from IBM.
https://github.com/IBM/JTOpen

*Use the Java 8 version, to enable JDBC 4.2 specification*

This drivers has only been tested with V7R4

### 05/03/2025  a new version is coming with metabase 0.53 support, in beta for the moment

### Build

Prereqs: Install Metabase locally, and build using metabase instructions

```bash
cd /path/to/metabase/source
wget desired version of metabase source from: https://github.com/metabase/metabase/releases/xxxx...
```

Build the DB2 driver using the newly-built metabase as a dependency ( new method for version > 0.46)

```bash
export DRIVER_PATH="/path/to/metabase/source/modules/drivers/metabase-db2-driver"

cd /path/to/metabase/source

clojure \
  -Sdeps "{:aliases {:db2 {:extra-deps {com.metabase/db2fori-driver {:local/root \"$DRIVER_PATH\"}}}}}"  \
  -X:build:db2 \
  build-drivers.build-driver/build-driver! \
  "{:driver :db2, :project-dir \"$DRIVER_PATH\", :target-dir \"$DRIVER_PATH/target\"}"

```

Copy the newly built driver to your plugins dir and restart Metabase
along with the jt400.jar from IBM's sourceforge project page depending on your OS: https://javadoc.midrange.com/readme.html

```bash
cp target/uberjar/db2.metabase-driver.jar /path/to/metabase/plugins/
cd /path/to/metabase/plugins wget sourceforge jt400 applicable driver version (in this case 8.5): 
jar -jar /path/to/metabase/metabase.jar
```

### Use these additional JDBC properties to be able to use date and time and metadata column description

metadata source=0;prompt=false;naming=system;date format=iso;time format=hms;time separator=colon;extended metadata=true;access=read call;remarks=system

