.DEFAULT_GOAL := build-run

check-deps:
	./gradlew dependencyUpdates -Drevision=release

dev:
	./gradlew run

setup:
	./gradlew wrapper --gradle-version 8.5

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew clean install

run-dist:
	./build/install/app/bin/app

run:
	./gradlew run

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain

build-run: build run

.PHONY: build