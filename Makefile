# foo
push:
	./gradlew build && cf push

test:
	./gradlew clean test
