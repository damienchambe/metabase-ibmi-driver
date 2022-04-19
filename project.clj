(defproject metabase/db2fori-driver "1.0.42"
  :min-lein-version "2.5.0"

  :profiles
  {:provided
   {:dependencies [
     [metabase-core "1.0.0-SNAPSHOT"]
     [net.sf.jt400/jt400 "10.7"]
   ]}

   :clean-targets [:target-path "build/js/output"]

   :uberjar
   {:auto-clean    true
    :aot :all
    :javac-options ["-target" "1.8", "-source" "1.8"]
    :target-path   "target/%s"
    :uberjar-name  "db2fori.jar"}})
