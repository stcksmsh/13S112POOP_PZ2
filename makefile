SRCDIR = src
BINDIR = bin
SRC = $(wildcard $(SRCDIR)/**/*.java)
BIN = $(patsubst $(SRCDIR)/%.java, $(BINDIR)/%.class, $(SRC))
PROGRAM = Table.Table
NAME = OSCalc.jar
MANIFEST = manifest.txt
IMGDIR = images

build: $(BIN)

run: build
	(cd $(BINDIR) && java $(PROGRAM))

$(BIN) : $(SRC)
	javac -d $(BINDIR) $(SRCDIR)/**/*.java

jar: build $(MANIFEST) 
	jar cmf $(MANIFEST) $(NAME) $(BINDIR)/** $(IMGDIR)/**

$(MANIFEST):
	@echo "Manifest-Version: 1.0" > $(MANIFEST)
	@echo "Class-Path: ./bin/" >> $(MANIFEST)
	@echo "Main-Class: Table.Table" >> $(MANIFEST)
	@echo "" >> $(MANIFEST)

clean:
	rm -f $(BINDIR)/**/*.class
	rm -f $(BINDIR)/**/*.csv
	rm -f $(BINDIR)/**/*.json
	rm -f $(NAME)
	rm -f manifext.txt
