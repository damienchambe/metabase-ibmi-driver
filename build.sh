#!/bin/bash
cd $(dirname $0) 
DRIVER_PATH=$(pwd)

cd ../metabase

JT400_DRIVER_VERSION=21.0.0

clojure -Sdeps "{:aliases {:ibmi {:extra-deps {net.sf.jt400/jt400 {:mvn/version \"$JT400_DRIVER_VERSION\"} com.metabase/db2foribmi-driver {:local/root \"$DRIVER_PATH\"}}}}}" -X:build:ibmi build-drivers.build-driver/build-driver! "{:driver :ibmi, :project-dir \"$DRIVER_PATH\", :target-dir \"$DRIVER_PATH/target\"}"
