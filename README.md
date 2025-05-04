
# Metabase Driver: IBM i

IBM i DB2 for Power system Driver for [Metabase](https://www.metabase.com).

This driver has been tested with V7R5 / JT400 21.0.3

###  Versions
| Metabase Version | IBM iMetabase driver | IBM i OS version | jar |
| --- | --- | --- | --- |
| 0.51 => 0.54 | 1.54 | 7.6, 7.5, 7.4 TR 7 | ([download latest jar](https://github.com/damienchambe/metabase-ibmi-driver/releases/)) |
| <= 0.46 | 1.46 | 7.4<TR 7, 7.3, 7.2, 7.1 | ([download jar](https://github.com/damienchambe/metabase-ibmi-driver/releases/download/1.0.46/db2.metabase-driver.jar)) |

###  Running Metabase application with IBM i driver
First download Metabase .jar file [here](https://metabase.com/start/other.html)  and run
```bash
java -jar metabase.jar
```
The `/path/to/metabase/plugins/` directory will be created. Drop the driver in your `plugins/` directory and run metabase again. You can grab it [here](https://github.com/alisonrafael/metabase-db2-driver/releases) or build it yourself:

## Building the DB2 Driver Yourself

### Prerequisites
- Java JDK 21
- Node.js 0.22
- Clojure
- Yarn 1.22

### Clone the Metabase project

Clone the [Metabase repo](https://github.com/metabase/metabase)

Inside `/path/to/metabase-master` run 
```bash
clojure -X:deps prep
```

### Clone the DB2 Metabase Driver

Clone this [DB2 driver repo](https://github.com/alisonrafael/metabase-db2-driver) 

Edit the driver as you want.

### Compile the DB2 driver

Inside `/path/to/metabase-db2-driver` run 

```bash
sh ./build.sh
```

### Copy it to your plugins dir
```bash
cp /path/to/metabase-db2-driver/target/db2.metabase-driver.jar /path/to/metabase/plugins/
```

### Run Metabase

```bash
jar -jar /path/to/metabase/metabase.jar
```


## Thanks
Thanks to everybody here [https://github.com/metabase/metabase/issues/1509](https://github.com/metabase/metabase/issues/1509)
