
SRCS := $(wildcard $(test/*.clj)) $(wildcard $(src/*.clj)) project.clj
JAR := target/mpd-timer-0.1.0-SNAPSHOT-standalone.jar


mpd-timer: $(JAR)
	cat res/kick.sh $(JAR) > mpd-timer
	chmod +x mpd-timer

$(JAR): $(SRC)
	lein uberjar
