# experimental-service-cache

## Run micro-benchmarks with jmh:
in service-cache-lib run gradle jmh task
is profiling with flight recorder, see in build.gradle `args '-prof', 'jfr'`

## Service benchmarks with JMeter:
launch jmeter/bin/jmeter.sh
load test.jmx from root
