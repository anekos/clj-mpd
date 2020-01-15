

SRCROOTS = src dev test
SOURCES = $(foreach root, $(SRCROOTS), $(shell find $(root) -name '*.clj'))

JAR := target/mpd-timer-0.1.0-SNAPSHOT-standalone.jar


mpd-timer: $(JAR)
	cat res/kick.sh $(JAR) > mpd-timer
	chmod +x mpd-timer

$(JAR): $(SRCS)
	lein uberjar
