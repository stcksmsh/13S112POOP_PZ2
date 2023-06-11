SRCDIR = src
BINDIR = bin
SRC = $(wildcard $(SRCDIR)/**/*.java)
BIN = $(patsubst $(SRCDIR)/%.java, $(BINDIR)/%.class, $(SRC))
PROGRAM = Table.Sheet

all : $(BIN)
	(cd $(BINDIR) && java $(PROGRAM))

$(BIN) : $(SRC)
	javac -d $(BINDIR) $(SRCDIR)/**/*.java

clean:
	rm -rdf $(BINDIR)/*