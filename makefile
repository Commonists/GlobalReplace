# Make does not offer a recursive wildcard function, so here's one:
rwildcard=$(wildcard $1$2) $(foreach d,$(wildcard $1*),$(call rwildcard,$d/,$2))

# How to recursively find all files that match a pattern
ALL_JAVALIBS := $(call rwildcard,org/,*.java)
ALL_JAVAS := $(call rwildcard,fbot/,*.java)

all: globalreplace
globalreplace: compile globalreplace.jar
	@echo "Done!"

compile:
	javac $(ALL_JAVALIBS)
	javac $(ALL_JAVAS)

globalreplace.jar:
	jar cmf MANIFEST.MF globalreplace.jar org fbot

clean: FRC
	-rm *.jar
	-rm *.class

# This pseudo target causes all targets that depend on FRC
# to be remade even in case a file with the name of the target exists.
# This works with any make implementation under the assumption that
# there is no file FRC in the current directory.
FRC:
