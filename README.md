
# Metabase Driver: IBM i

IBM i DB2 for Power system Driver for [Metabase](https://www.metabase.com).

This driver has been tested on a metabase opensource version, installed on a linux platform with a connection to an ibm i V7R5

This driver is *NOT* for DB2 Unix/linux/windows. See [DB2 for LUW (Linux, UNIX, Windows) Driver](https://github.com/alisonrafael/metabase-db2-driver)

###  Versions
| Metabase Version | IBM iMetabase driver | IBM i OS version | jar |
| --- | --- | --- | --- |
| 55 => 58 | 1.55 | 7.6, 7.5, 7.4 TR 7 | ([download 1.55 jar](https://github.com/damienchambe/metabase-ibmi-driver/releases/tag/1.0.55)) |
| 51 => 54 | 1.54 | 7.6, 7.5, 7.4 TR 7 | ([download 1.54 jar](https://github.com/damienchambe/metabase-ibmi-driver/releases/tag/1.0.54)) |
| <= 0.46 | 1.46 | 7.4<TR 7, 7.3, 7.2, 7.1 | ([download jar](https://github.com/damienchambe/metabase-ibmi-driver/releases/download/1.0.46/db2.metabase-driver.jar)) |



##  Running Metabase application with IBM i driver

Metabase should be installed on a linux or windows server.

If you have a recent version of os400, metabase can be installed directly on V7R6 and V7R5 because java 21 is provided, IBM has released java 21 for both version. I haven't tested yet to run metabase instance on my ibm i.



First download Metabase .jar file [here](https://metabase.com/start/other.html)  and run
```bash
java -jar metabase.jar
```
The `/path/to/metabase/plugins/` directory will be created. Drop the driver in your `plugins/` directory and run metabase again. You can grab it [here](https://github.com/damienchambe/metabase-ibmi-driver/releases) or build it yourself:

I advise migrating the metabase system database from hsqldb to another database.

My Metabase instance runs on Ubuntu 24.04, My system database is running on the same server with PostgreSQL + pgadmin. I have connections to several IBM i partitions. This setting avoids charging the IBM i with hosting a java server with metabase.


##  Migration from <= 1.46 to newer version >= 1.55 

#### Metabase migration
- Backup your metabase system database ( Hdb or other )
- Update your metabase version if needed.
- Restart to check if there is no problem. At this step there will be errors when runnning ibm i sql queries, because you haven't updated the driver.

#### Driver migration
- Stop metabase
- Remove db2.metabase-driver.jar in the plugin directory. You only need the new jar : ibmi.metabase-driver.jar.
- Restart metabase your actual connection, go in database settings, you must edit your connections details and change "database type" to ibm i

The database type as been changed from "db2" to "ibm i", to prevent conflicts if you need DB2 for LUW and Ibm i in you metabase instance.


## Building the DB2 Driver Yourself

### Build

### Prerequisites
- Java JDK 21
- Node.js 0.22
- Clojure

Warning, for ubuntu , install the official clojure app and not the one from the repositories :
```bash
sudo apt remove -y clojure
sudo apt install -y curl rlwrap
curl -O https://download.clojure.org/install/linux-install-1.11.1.1403.sh
chmod +x linux-install-1.11.1.1403.sh
sudo ./linux-install-1.11.1.1403.sh
```

- Yarn 1.22

### Clone the Metabase project

You should putt metabase and the driver in the same directory:

/yourpath/metabase
/yourpath/metabase-ibmi-driver

Clone the [Metabase repo](https://github.com/metabase/metabase)
```bash
git clone --depth 1 https://github.com/metabase/metabase.git
```

Inside `/yourpath/metabase` run 
```bash
clojure -X:deps prep
```

Or read the most recent dev doc in metabase to build and launch metabase.

### Clone the DB2 Metabase Driver

Clone this [IBM i driver repo](https://github.com/damienchambe/metabase-ibmi-driver) 

in 

Edit the driver as you want.

### Compile the DB2 driver

Inside `/yourpath/metabase-ibmi-driver` run 

```bash
sh ./build.sh
```

### Copy it to your plugins dir
```bash
cp /yourpath/metabase-ibmi-driver/target/ibmi.metabase-driver.jar /yourpath/metabase/plugins/
```

### Run Metabase

```bash
jar -jar /yourpath/metabase/metabase.jar
```


## Thanks
Thanks to everybody here [https://github.com/metabase/metabase/issues/1509](https://github.com/metabase/metabase/issues/1509)
