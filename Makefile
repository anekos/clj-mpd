

SRCROOTS = src dev test
SRCS = $(foreach root, $(SRCROOTS), $(shell find $(root) -name '*.clj')) project.clj

JAR := target/mpd-timer-0.1.0-SNAPSHOT-standalone.jar

mpd-timer: $(JAR)
	/usr/lib/jvm/default-runtime/jre/bin/native-image \
		--no-server \
		--no-fallback \
		--report-unsupported-elements-at-runtime \
		-H:Name=mpd-timer \
		-H:+ReportExceptionStackTraces \
		-jar $(JAR)

# mpd-timer: $(JAR)
# 	cat res/kick.sh $(JAR) > mpd-timer
# 	chmod +x mpd-timer

$(JAR): $(SRCS)
	lein clean
	lein uberjar
