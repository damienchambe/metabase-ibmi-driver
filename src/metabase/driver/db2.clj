(ns metabase.driver.db2
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [honeysql.core :as hsql]
            [metabase.driver :as driver]
            [metabase.driver.common :as driver.common]
            [metabase.driver.sql
             [query-processor :as sql.qp]
             [util :as sql.u]]
            [metabase.driver.sql-jdbc
             [common :as sql-jdbc.common]
             [connection :as sql-jdbc.conn]
             [execute :as sql-jdbc.execute]
             [sync :as sql-jdbc.sync]]
            [metabase.driver.sql.util.unprepare :as unprepare]
            [metabase.util
             [date-2 :as du]
             [honeysql-extensions :as hx]
             [ssh :as ssh]])
  (:import [java.sql ResultSet Types]
           java.util.Date))

(driver/register! :db2, :parent :sql-jdbc)


;;; +----------------------------------------------------------------------------------------------------------------+
;;; |                                             metabase.driver impls                                              |
;;; +----------------------------------------------------------------------------------------------------------------+

(defmethod driver/display-name :db2 [_] "DB2")

(defmethod driver/humanize-connection-error-message :db2 [_ message]
  (condp re-matches message
    #"^FATAL: database \".*\" does not exist$"
    (driver.common/connection-error-messages :database-name-incorrect)

    #"^No suitable driver found for.*$"
    (driver.common/connection-error-messages :invalid-hostname)

    #"^Connection refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.$"
    (driver.common/connection-error-messages :cannot-connect-check-host-and-port)

    #"^FATAL: role \".*\" does not exist$"
    (driver.common/connection-error-messages :username-incorrect)

    #"^FATAL: password authentication failed for user.*$"
    (driver.common/connection-error-messages :password-incorrect)

    #"^FATAL: .*$" ; all other FATAL messages: strip off the 'FATAL' part, capitalize, and add a period
    (let [[_ message] (re-matches #"^FATAL: (.*$)" message)]
      (str (str/capitalize message) \.))

    #".*" ; default
    message))

(defmethod driver.common/current-db-time-date-formatters :db2 [_]     ;; "2019-09-14T18:03:20.679658000-00:00"
;;  (println "current-db-time-date-formatters")
  (mapcat
   driver.common/create-db-time-formatters
   ["yyyy-MM-dd HH:mm:ss"
    "yyyy-MM-dd HH:mm:ss.SSS"
    "yyyy-MM-dd'T'HH:mm:ss.SSS"
    "yyyy-MM-dd HH:mm:ss.SSSZ"
    "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    "yyyy-MM-dd HH:mm:ss.SSSZZ"
    "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
    "yyyy-MM-dd HH:mm:ss.SSSSSSZZ"
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZ"
    "yyyy-MM-dd HH:mm:ss.SSSSSSSSSZZ"
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSZZ"]))

(defmethod driver.common/current-db-time-native-query :db2 [_]
;;  (println "current-db-time-native-query")
  "SELECT TO_CHAR(CURRENT TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') FROM SYSIBM.SYSDUMMY1")       ;; "SELECT CURRENT TIMESTAMP FROM SYSIBM.SYSDUMMY1")

(defmethod driver/current-db-time :db2 [& args]
;;  (println "current-db-time")
  (apply driver.common/current-db-time args))


;;; +----------------------------------------------------------------------------------------------------------------+
;;; |                                           metabase.driver.sql impls                                            |
;;; +----------------------------------------------------------------------------------------------------------------+

(defn- date-format [format-str expr] (hsql/call :varchar_format expr (hx/literal format-str)))
(defn- str-to-date [format-str expr] (hsql/call :to_date expr (hx/literal format-str)))

(defn- trunc-with-format [format-str expr]
  (str-to-date format-str (date-format format-str expr)))

(defmethod sql.qp/date [:db2 :default]        [_ _ expr] expr)
(defmethod sql.qp/date [:db2 :minute]         [_ _ expr] (trunc-with-format "YYYY-MM-DD HH24:MI" expr))
(defmethod sql.qp/date [:db2 :minute-of-hour] [_ _ expr] (hsql/call :minute expr))
(defmethod sql.qp/date [:db2 :hour]           [_ _ expr] (trunc-with-format "YYYY-MM-DD HH24" expr))
(defmethod sql.qp/date [:db2 :hour-of-day]    [_ _ expr] (hsql/call :hour expr))
(defmethod sql.qp/date [:db2 :day]            [_ _ expr] (hsql/call :date expr))
(defmethod sql.qp/date [:db2 :day-of-month]   [_ _ expr] (hsql/call :day expr))
(defmethod sql.qp/date [:db2 :week]           [_ _ expr] (hx/- expr (hsql/raw (format "%d days" (int (hx/- (hsql/call :dayofweek expr) 1))))))
(defmethod sql.qp/date [:db2 :month]          [_ _ expr] (str-to-date "YYYY-MM-DD" (hx/concat (date-format "YYYY-MM" expr) (hx/literal "-01"))))
(defmethod sql.qp/date [:db2 :month-of-year]  [_ _ expr] (hsql/call :month expr))
(defmethod sql.qp/date [:db2 :quarter]        [_ _ expr] (str-to-date "YYYY-MM-DD" (hsql/raw (format "%d-%d-01" (int (hx/year expr)) (int ((hx/- (hx/* (hx/quarter expr) 3) 2)))))))
(defmethod sql.qp/date [:db2 :year]           [_ _ expr] (hsql/call :year expr))

(defmethod sql.qp/date [:db2 :day-of-year] [driver _ expr] (hsql/call :dayofyear expr))

(defmethod sql.qp/date [:db2 :week-of-year] [_ _ expr] (hsql/call :week expr))

(defmethod sql.qp/date [:db2 :quarter-of-year] [driver _ expr] (hsql/call :quarter expr))

(defmethod sql.qp/date [:db2 :day-of-week] [driver _ expr] (hsql/call :dayofweek expr))

(defmethod sql.qp/add-interval-honeysql-form :db2 [_ hsql-form amount unit]
  (hx/+ (hx/->timestamp hsql-form) (case unit
                              :second  (hsql/raw (format "current timestamp + %d seconds" (int amount)))
                              :minute  (hsql/raw (format "current timestamp + %d minutes" (int amount)))
                              :hour    (hsql/raw (format "current timestamp + %d hours" (int amount)))
                              :day     (hsql/raw (format "current timestamp + %d days" (int amount)))
                              :week    (hsql/raw (format "current timestamp + %d days" (int (hx/* amount (hsql/raw 7)))))
                              :month   (hsql/raw (format "current timestamp + %d months" (int amount)))
                              :quarter (hsql/raw (format "current timestamp + %d months" (int (hx/* amount (hsql/raw 3)))))
                              :year    (hsql/raw (format "current timestamp + %d years" (int amount))))))

(defmethod sql.qp/unix-timestamp->honeysql [:db2 :seconds] [_ _ expr]
  (hx/+ (hsql/raw "timestamp('1970-01-01 00:00:00')") (hsql/raw (format "%d seconds" (int expr))))

(defmethod sql.qp/unix-timestamp->honeysql [:db2 :milliseconds] [driver _ expr]
  (hx/+ (hsql/raw "timestamp('1970-01-01 00:00:00')") (hsql/raw (format "%d seconds" (int (hx// expr 1000)))))))

(def ^:private now (hsql/raw "current timestamp"))

(defmethod sql.qp/current-datetime-honeysql-form :db2 [_] now)

;; DB2 ibm i doesn't support `TRUE`/`FALSE`; use `1`/`0`, respectively; convert these booleans to numbers.
(defmethod sql.qp/->honeysql [:db2 Boolean]
  [_ bool]
  (if bool 1 0))


;;; +----------------------------------------------------------------------------------------------------------------+
;;; |                                           metabase.driver.sql date workarounds                                 |
;;; +----------------------------------------------------------------------------------------------------------------+

;; (.getObject rs i LocalDate) doesn't seem to work, nor does `(.getDate)`; ;;v0.34.x
;; Merged from vertica.clj e sqlite.clj.
;; Fix to Invalid data conversion: Wrong result column type for requested conversion. ERRORCODE=-4461
(defmethod sql-jdbc.execute/read-column [:db2 Types/DATE]
		[_ _ ^ResultSet rs _ ^Integer i]
		  (let [s (.getString rs i)
		        t (du/parse s)]
		    t))

(defmethod sql-jdbc.execute/read-column [:db2 Types/TIME]
		[_ _ ^ResultSet rs _ ^Integer i]
		  (let [s (.getString rs i)
		        t (du/parse s)]
		    t))

(defmethod sql-jdbc.execute/read-column [:db2 Types/TIMESTAMP]
		[_ _ ^ResultSet rs _ ^Integer i]
		  (let [s (.getString rs i)
		        t (du/parse s)]
		    t))

;;; +----------------------------------------------------------------------------------------------------------------+
;;; |                                         metabase.driver.sql-jdbc impls                                         |
;;; +----------------------------------------------------------------------------------------------------------------+

(defmethod sql-jdbc.conn/connection-details->spec :db2
  [_ {:keys [host port dbname]
      :or   {host "localhost", port 3386, dbname ""}
      :as   details}]
  (-> (merge {:classname "com.ibm.as400.access.AS400JDBCDriver"   ;; must be in classpath
          :subprotocol "as400"
          :subname (str "//" host ":" port "/" dbname)}                    ;; :subname (str "//" host "/" dbname)}   (str "//" host ":" port "/" (or dbname db))}
         (dissoc details :host :port :dbname))
  (sql-jdbc.common/handle-additional-options details, :seperator-style :semicolon)))

;  (defmethod sql-jdbc.conn/connection-details->spec :db2 [_ {:keys [host port db dbname]
;                                                             :or   {host "localhost", port 50000, dbname ""}
;                                                             :as   details}]
;  (-> (merge {:classname   "com.ibm.db2.jcc.DB2Driver"
;            :subprotocol "db2"
;              :subname     (str "//" host ":" port "/" dbname ":" )}
;             (dissoc details :host :port :dbname :ssl))
;      (sql-jdbc.common/handle-additional-options details, :seperator-style :semicolon)))

(defmethod driver/can-connect? :db2 [driver details]
  (let [connection (sql-jdbc.conn/connection-details->spec driver (ssh/include-ssh-tunnel! details))]
    (= 1 (first (vals (first (jdbc/query connection ["SELECT 1 FROM SYSIBM.SYSDUMMY1"])))))))

(defmethod sql-jdbc.sync/database-type->base-type :db2 [_ database-type]
  ({:BIGINT       :type/BigInteger    ;; Mappings for DB2 types to Metabase types.
    :BINARY       :type/*             ;; See the list here: https://docs.tibco.com/pub/spc/4.0.0/doc/html/ibmdb2/ibmdb2_data_types.htm
    :BLOB         :type/*
    :BOOLEAN      :type/Boolean
    :CHAR         :type/Text
    :CLOB         :type/Text
    :DATALINK     :type/*
    :DATE         :type/Date
    :DBCLOB       :type/Text
    :DECIMAL      :type/Decimal
    :DECFLOAT     :type/Decimal
    :DOUBLE       :type/Float
    :FLOAT        :type/Float
    :GRAPHIC      :type/Text
    :INTEGER      :type/Integer
    :NUMERIC      :type/Decimal
    :REAL         :type/Float
    :ROWID        :type/*
    :SMALLINT     :type/Integer
    :TIME         :type/Time
    :TIMESTAMP    :type/DateTime
    :VARCHAR      :type/Text
    :VARGRAPHIC   :type/Text
    :XML          :type/Text
    (keyword "CHAR() FOR BIT DATA")       :type/*
    (keyword "LONG VARCHAR")              :type/*
    (keyword "LONG VARCHAR FOR BIT DATA") :type/*
    (keyword "LONG VARGRAPHIC")           :type/*
    (keyword "VARCHAR() FOR BIT DATA")    :type/*} database-type))

(defmethod sql-jdbc.sync/excluded-schemas :db2 [_]
  #{"SQLJ"
    "QSYS"
    "QSYS2"
    "SYSCAT"
    "SYSFUN"
    "SYSIBM"
    "SYSIBMADM"
    "SYSIBMINTERNAL"
    "SYSIBMTS"
    "SPOOLMAIL"
    "SYSPROC"
    "SYSPUBLIC"
    "SYSTOOLS"
    "SYSSTAT"
    "QHTTPSVR"
    "QUSRSYS"})

(defmethod sql-jdbc.execute/set-timezone-sql :db2 [_]
  "SET SESSION TIME ZONE = %s")

;; instead of returning a CLOB object, return the String. (#9026)
;; (defmethod sql-jdbc.execute/read-column [:db2 Types/CLOB] [_ _, ^ResultSet resultset, _, ^Integer i]
;;   (println "XXXXX read-column: " i)
;;   (.getString resultset i))

;; (defmethod unprepare/unprepare-value [:db2 Date] [_ value]
;;   (println "XXXXX unprepare/unprepare-value: " value)
;;   (format "timestamp '%s'" (du/format-date "yyyy-MM-dd hh:mm:ss.SSS ZZ" value)))
