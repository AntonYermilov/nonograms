import os

from ctypes import *
from json import dumps, loads

from logger import Logger

class Nonogram:
    lib = cdll.LoadLibrary(os.path.join(os.path.abspath(""), "lib", "logic.so"))
    
    # Tells if specified nonogram is solvable or not
    # Receives json in the following format:
    # {"backgroundColor": int, "width": int, "height": int, "colors": int, "pixels": str}
    @staticmethod
    def solve(nonogram):
        data = dumps(nonogram, separators=(',', ':')).encode()
        try:
            Logger.writeInfo("Solving nonogram...")
            result = c_bool(Nonogram.lib.checkSolvability(c_char_p(data)))
            Logger.writeInfo("Specified crossword is solvable: {}".format(result.value))
            return {"response": "ok", "data": result.value}
        except ArgumentError as e:
            Logger.writeError(e.msg)
            return {"response": "fail", "desc": e.msg}

    # Creates nonogram from received image.
    # Receives json in the following format:
    # {"backgroundColor": int, "width": int, "height": int, "colors": int, "pixels": str}
    # Returns data in the following format:
    # {"solvable": bool, image: {"backgroundColor": int, "width": int, "height": int, "colors": int, "pixels": str}}
    @staticmethod
    def create(image):
        data = dumps(image, separators=(',', ':')).encode()
        try:
            Logger.writeInfo("Creating nonogram...")
            result = c_char_p(Nonogram.lib.createNonogram(c_char_p(data)))
            Logger.writeInfo("Created nonogram from specified image")
            return {"response": "ok", "data": loads(result.value, encoding="UTF-8")}
        except ArgumentError as e:
            Logger.writeError(e.msg)
            return {"response": "fail", "desc": e.msg}
