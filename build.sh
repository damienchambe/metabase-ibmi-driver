export DRIVER_PATH="/home/dchambe/git/metabase/modules/drivers/metabase-db2-driver"

clojure \
  -Sdeps "{:aliases {:db2 {:extra-deps {com.metabase/db2fori-driver {:local/root \"$DRIVER_PATH\"}}}}}"  \
  -X:build:db2 \
  build-drivers.build-driver/build-driver! \
  "{:driver :db2, :project-dir \"$DRIVER_PATH\", :target-dir \"$DRIVER_PATH/target\"}"
