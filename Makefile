
SRCS := $(wildcard $(test/*.clj)) $(wildcard $(src/*.clj)) project.clj
JAR := target/mpd-timer-0.1.0-SNAPSHOT-standalone.jar


target/mpd-timer: $(JAR)
	/usr/lib/jvm/default-runtime/jre/bin/native-image \
		--no-server \
		--report-unsupported-elements-at-runtime \
		--enable-all-security-services \
		-H:Name=./target/mpd-timer \
		-H:+ReportExceptionStackTraces \
		--no-fallback \
		-jar $(JAR)

mpd-timer: $(JAR)
	cat res/kick.sh $(JAR) > mpd-timer
	chmod +x mpd-timer

$(JAR): $(SRC)
	lein uberjar
