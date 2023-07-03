_=$() $()
SRCDIR = src
LIBDIR = lib
SRC = $(wildcard $(SRCDIR)/**/*.java)
LIB = $(wildcard $(LIBDIR)/*.jar)
BIN = $(patsubst $(SRCDIR)/%.java, ./%.class, $(SRC))
JFLAGS = -cp "$(subst $(_),:,$(LIB)):."

PROGRAM = Table.Table
NAME = OSCalc.jar
MANIFEST = manifest.txt
ICON = images/icon.png

build: $(BIN)

run: build
	java $(JFLAGS) $(PROGRAM)

$(BIN) : $(SRC)
	javac $(JFLAGS) -d . $(SRCDIR)/**/*.java

jar: build $(MANIFEST) 
	jar cmf $(MANIFEST) $(NAME) $(BIN) $(ICON) $(LIB)

$(MANIFEST):
	@echo "Manifest-Version: 1.0" > $(MANIFEST)
	@echo "Class-Path: ./bin/" >> $(MANIFEST)
	@echo "Main-Class: Table.Table" >> $(MANIFEST)
	@echo "" >> $(MANIFEST)

clean:
	rm -f ./***.class
	rm -f ./*.csv
	rm -f ./*.json
	rm -f $(NAME)
	rm -f manifext.txt
