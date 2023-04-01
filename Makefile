lexer: clean
	@echo "Building lexer/Lexer.java..."
	javac -d target lexer/Lexer.java

build-test: clean
	@echo "Building project with tests..."
	find . -name "*.java" > sources.txt
	-javac -d target -cp target:lib/junit-platform-console-standalone-1.9.0.jar:. @sources.txt
	rm sources.txt

test: build-test
	@echo "Running tests... (Note that tests that fail to compile will not be included!)"
	java -jar lib/junit-platform-console-standalone-1.9.0.jar --class-path ./target --scan-classpath

setup:
	@echo "Running TokenSetup..."
	javac lexer/setup/TokenSetup.java
	java lexer.setup.TokenSetup
	@echo "Remember to commit the newly generated Tokens.java and TokenType.java files!"

clean:
	@echo "Deleting all class files..."
	find . -name ".class" -type f -delete