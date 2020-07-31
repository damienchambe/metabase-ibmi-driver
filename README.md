# metabase-db2-driver
v7r1 iSeries DB2 Driver for Metabase

### Prereqs: Install Metabase locally, compiled for building drivers

```bash
cd /path/to/metabase/source
lein install-for-building-drivers
```

### Build the driver using the newly-built metabase

```bash
cd /path/to/teradata-driver
lein clean
DEBUG=1 LEIN_SNAPSHOTS_IN_RELEASE=true lein uberjar
```
### Copy the newly built driver to your plugins dir and restart Metabase
### along with the jt400.jar from IBM's sourceforge project page
```bash
cp target/uberjar/db2.metabase-driver.jar /path/to/metabase/plugins/
cd /path/to/metabase/plugins wget sourceforge jt400 applicable driver version (in this case 8.5)
jar -jar /path/to/metabase/metabase.jar
```

*or:*

```bash
cp target/uberjar/sudoku.metabase-driver.jar /path/to/metabase/source/plugins
cd /path/to/metabase/plugins wget sourceforge jt400 applicable driver version (in this case 8.5)
cd /path/to/metabase/source
lein run
```
###Don't forget to download the jt400.jar applicable to your environment from

