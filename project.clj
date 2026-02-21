(defproject metabase/db2fori-driver "1.58"
  :min-lein-version "2.5.0"

  :profiles
  {:provided
   {:dependencies [
     [metabase-core "1.0.0-SNAPSHOT"]
     [net.sf.jt400/jt400 "21.0.3"]
   ]}

   :clean-targets [:target-path "build/js/output"]

   :uberjar
   {:auto-clean    true
    :aot :all
    :javac-options ["-target" "1.8", "-source" "1.8"]
    :target-path   "target/%s"
    :uberjar-name  "db2fori.jar"}})
