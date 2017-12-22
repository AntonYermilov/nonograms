import os

from ctypes import *
from json import dumps

from logger import Logger

class Nonogram:
    @staticmethod
    def solve(nonogram):
        lib = cdll.LoadLibrary(os.path.join(os.path.abspath(""), "lib", "logic.so"))
        nonogram_json = dumps(nonogram, separators=(',', ':')).encode()
        try:
            result = c_bool(lib.checkSolvability(c_char_p(nonogram_json)))
            Logger.writeInfo("Specified crossword is solvable: {}".format(result.value))
            return {"response": "ok", "data": result.value}
        except ArgumentError as e:
            Logger.writeError(e.msg)
            return {"response": "fail", "desc": e.msg}
