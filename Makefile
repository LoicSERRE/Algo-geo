JAVAC=javac
JAVA=java
SRC=tp1.java
CLASS=tp1

all: compile run

compile: $(SRC)
	$(JAVAC) -encoding UTF8 $(SRC)

run: $(CLASS).class
	$(JAVA) $(CLASS)

clean:
	rm -f *.class