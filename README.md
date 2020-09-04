# metabase-db2-driver
v7r1 iSeries DB2 Driver for Metabase

### Prereqs: Install Metabase locally, compiled for building drivers

```bash
cd /path/to/metabase/source
wget desired version of metabase source from: https://github.com/metabase/metabase/releases/xxxx...
unzip / untar
lein install-for-building-drivers
```

### Build the DB2 driver using the newly-built metabase as a dependency

```bash
git clone this repository
cd /path/to/db2-driver-code-from-github
lein clean
DEBUG=1 LEIN_SNAPSHOTS_IN_RELEASE=true lein uberjar
```
### Copy the newly built driver to your plugins dir and restart Metabase
### along with the jt400.jar from IBM's sourceforge project page depending on your OS: https://javadoc.midrange.com/readme.html
```bash
cp target/uberjar/db2.metabase-driver.jar /path/to/metabase/plugins/
cd /path/to/metabase/plugins wget sourceforge jt400 applicable driver version (in this case 8.5): 
jar -jar /path/to/metabase/metabase.jar
```

*or:*

```bash
cp target/uberjar/db2.metabase-driver.jar /path/to/metabase/source/plugins
cd /path/to/metabase/plugins wget sourceforge jt400 applicable driver version (in this case 8.5)
cd /path/to/metabase/source
lein run
```


