SRCDIR = src
BINDIR = bin
SRC = $(wildcard $(SRCDIR)/**/*.java)
BIN = $(patsubst $(SRCDIR)/%.java, $(BINDIR)/%.class, $(SRC))
PROGRAM = Table.Table
NAME = OSCalc.jar
MANIFEST = manifest.txt
all : $(BIN)
	(cd $(BINDIR) && java $(PROGRAM))

$(BIN) : $(SRC)
	javac -d $(BINDIR) $(SRCDIR)/**/*.java

jar: $(BIN) $(MANIFEST)
	jar cmf $(MANIFEST) $(NAME) $(BINDIR)/**

$(MANIFEST):
	@echo "Manifest-Version: 1.0" > $(MANIFEST)
	@echo "Class-Path: ./bin/" >> $(MANIFEST)
	@echo "Main-Class: Table.Table" >> $(MANIFEST)
	@echo "" >> $(MANIFEST)

clean:
	rm -rdf $(BINDIR)/*
	rm -f $(NAME)
	rm -f manifext.txt
