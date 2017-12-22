CXX = g++
CXXFLAGS = -O3 -Wall -Werror -Wextra -std=c++17 -Iinclude
DEBUGFLAGS = -DLOCAL -D_GLIBCXX_DEBUG
LIBFLAGS = -shared -Wl,-soname,

LIB = logic.so
SRCDIR = src
BINDIR = bin


OBJECTS = $(patsubst $(SRCDIR)/%.cpp,$(BINDIR)/%.o,$(wildcard $(SRCDIR)/*.cpp))

pylib: $(LIB)

$(LIB): $(BINDIR) $(OBJECTS)
	$(CXX) $(OBJECTS) -shared -Wl,-soname,-$(LIB) -o $(LIB)

$(BINDIR)/%.o: $(SRCDIR)/%.cpp
	$(CXX) $(CXXFLAGS) -c -o $@ -fPIC $<

$(BINDIR):
	mkdir -p $(BINDIR)

clean:
	rm -rf $(BINDIR)
	rm -f $(LIB)

.PHONY: pylib clean
