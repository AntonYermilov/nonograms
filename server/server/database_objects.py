from sqlobject import *

class Nonogram(SQLObject):
    name = UnicodeCol(length=32)
    author = UnicodeCol(length=32)
    height = IntCol()
    width = IntCol()
