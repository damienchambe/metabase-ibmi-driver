
# Metabase Driver: IBM i

IBM i DB2 for Power system Driver for [Metabase](https://www.metabase.com).

This driver has been tested on a metabase linux, platform with a connection to an ibm i V7R5

This driver is *NOT* for DB2 Unix/linux/windows. See [DB2 for LUW (Linux, UNIX, Windows) Driver for](https://github.com/alisonrafael/metabase-db2-driver)

###  Versions
| Metabase Version | IBM iMetabase driver | IBM i OS version | jar |
| --- | --- | --- | --- |
| 55 => 56 | 1.55 | 7.6, 7.5, 7.4 TR 7 | ([download 1.55 jar](https://github.com/damienchambe/metabase-ibmi-driver/releases/tag/1.0.55)) |
| 51 => 54 | 1.54 | 7.6, 7.5, 7.4 TR 7 | ([download 1.54 jar](https://github.com/damienchambe/metabase-ibmi-driver/releases/tag/1.0.54)) |
| <= 0.46 | 1.46 | 7.4<TR 7, 7.3, 7.2, 7.1 | ([download jar](https://github.com/damienchambe/metabase-ibmi-driver/releases/download/1.0.46/db2.metabase-driver.jar)) |

###  Running Metabase application with IBM i driver

This should be launched on a linux or windows server. An Ibm i OS400 partition < 7.5 can not launch recent metabase version because java 21 is required.  
This could work on the recent V7R6 and V7R5 because java 21 is provided, IBM has released java 21 for both version. I haven't tested it.

First download Metabase .jar file [here](https://metabase.com/start/other.html)  and run
```bash
java -jar metabase.jar
```
The `/path/to/metabase/plugins/` directory will be created. Drop the driver in your `plugins/` directory and run metabase again. You can grab it [here](https://github.com/damienchambe/metabase-ibmi-driver/releases) or build it yourself:

## Building the DB2 Driver Yourself

### Build

### Prerequisites
- Java JDK 21
- Node.js 0.22
- Clojure
- Yarn 1.22

### Clone the Metabase project

Clone the [Metabase repo](https://github.com/metabase/metabase)
```bash
git clone --depth 1 https://github.com/metabase/metabase.git
```

Inside `/path/to/metabase-master` run 
```bash
clojure -X:deps prep
```

### Clone the DB2 Metabase Driver

Clone this [DB2 driver repo](https://github.com/damienchambe/metabase-ibmi-driver) 

Edit the driver as you want.

### Compile the DB2 driver

Inside `/path/to/metabase-ibmi-driver` run 

```bash
sh ./build.sh
```

### Copy it to your plugins dir
```bash
cp /path/to/metabase-ibmi-driver/target/ibmi.metabase-driver.jar /path/to/metabase/plugins/
```

### Run Metabase

```bash
jar -jar /path/to/metabase/metabase.jar
```


## Thanks
Thanks to everybody here [https://github.com/metabase/metabase/issues/1509](https://github.com/metabase/metabase/issues/1509)
