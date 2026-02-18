
# Metabase Driver: IBM i

IBM i DB2 for Power Systems driver for [Metabase](https://www.metabase.com).

This driver has been tested on a Metabase open-source version, installed on a Linux platform with a connection to an IBM i V7R5.

This driver is *NOT* for DB2 on Linux/UNIX/Windows. See [DB2 for LUW (Linux, UNIX, Windows) Driver](https://github.com/alisonrafael/metabase-db2-driver).

###  Versions
| Metabase Version | IBM i Metabase driver | IBM i OS version | jar |
| --- | --- | --- | --- |
| 55 => 58 | 1.55 | 7.6, 7.5, 7.4 TR 7 | ([download 1.55 jar](https://github.com/damienchambe/metabase-ibmi-driver/releases/tag/1.0.55)) |
| 51 => 54 | 1.54 | 7.6, 7.5, 7.4 TR 7 | ([download 1.54 jar](https://github.com/damienchambe/metabase-ibmi-driver/releases/tag/1.0.54)) |
| <= 0.46 | 1.46 | 7.4<TR 7, 7.3, 7.2, 7.1 | ([download jar](https://github.com/damienchambe/metabase-ibmi-driver/releases/download/1.0.46/db2.metabase-driver.jar)) |



##  Running Metabase application with IBM i driver

Metabase should be installed on a Linux or Windows server.

If you have a recent version of OS/400, Metabase can be installed directly on V7R6 and V7R5 because Java 21 is provided. IBM has released Java 21 for both versions. I haven't tested running a Metabase instance on my IBM i yet.



First download the Metabase .jar file [here](https://metabase.com/start/other.html) and run
```bash
java -jar metabase.jar
```
The `/path/to/metabase/plugins/` directory will be created. Drop the driver in your `plugins/` directory and run Metabase again. You can grab it [here](https://github.com/damienchambe/metabase-ibmi-driver/releases) or build it yourself:

I recommend migrating the Metabase system database from HSQLDB to another database.

My Metabase instance runs on Ubuntu 24.04. My system database runs on the same server with PostgreSQL and pgAdmin. I have connections to several IBM i partitions. This setup avoids loading the IBM i with hosting a Java server for Metabase.


##  Migration from <= 1.46 to newer version >= 1.55 

#### Metabase migration
- Backup your Metabase system database (HSQLDB or other).
- Update your metabase version if needed.
- Restart to make sure there are no problems. At this step there will be errors when running IBM i SQL queries, because you haven't updated the driver.

#### Driver migration
- Stop Metabase.
- Remove `db2.metabase-driver.jar` from the plugins directory. You only need the new JAR: `ibmi.metabase-driver.jar`.
- Restart Metabase. For existing connections, go to database settings, edit your connection details, and change "database type" to IBM i.

The database type has been changed from "db2" to "ibm i" to prevent conflicts if you need DB2 for LUW and IBM i in your Metabase instance.


## Building the DB2 Driver Yourself

### Build

### Prerequisites
- Java JDK 21
- Node.js 0.22
- Clojure

Warning: on Ubuntu, install the official Clojure app and not the one from the repositories:
```bash
sudo apt remove -y clojure
sudo apt install -y curl rlwrap
curl -O https://download.clojure.org/install/linux-install-1.11.1.1403.sh
chmod +x linux-install-1.11.1.1403.sh
sudo ./linux-install-1.11.1.1403.sh
```

- Yarn 1.22

### Clone the Metabase project

You should put Metabase and the driver in the same directory:

/yourpath/metabase
/yourpath/metabase-ibmi-driver

Clone the [Metabase repo](https://github.com/metabase/metabase)
```bash
git clone --depth 1 https://github.com/metabase/metabase.git
```

Inside `/yourpath/metabase`, run:
```bash
clojure -X:deps prep
```

Or read the most recent developer docs in Metabase to build and launch Metabase.

### Clone the IBM i Metabase Driver

Clone this [IBM i driver repo](https://github.com/damienchambe/metabase-ibmi-driver) 

Edit the driver as you want.

### Compile the DB2 driver

Inside `/yourpath/metabase-ibmi-driver`, run:

```bash
sh ./build.sh
```

### Copy it to your plugins dir
```bash
cp /yourpath/metabase-ibmi-driver/target/ibmi.metabase-driver.jar /yourpath/metabase/plugins/
```

### Run Metabase

```bash
java -jar /yourpath/metabase/metabase.jar
```


## Thanks
Thanks to everybody here: [https://github.com/metabase/metabase/issues/1509](https://github.com/metabase/metabase/issues/1509)
