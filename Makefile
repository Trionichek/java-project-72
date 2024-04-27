build:
	make -C app build

run-dist:
	make -C app run-dist

test:
	make -C app test

test_report:
	make -C app test_report

lint:
	make -C app lint

.PHONY: build
