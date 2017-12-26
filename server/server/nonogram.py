import os

from ctypes import *
from json import dumps, loads, JSONDecodeError

from logger import Logger

# Provides access to nonogram logic and its functions to create and solve crosswords
class Nonogram:
    lib = cdll.LoadLibrary(os.path.join(os.path.abspath(""), "lib", "logic.so"))
    
    # Initializing return parameters of lib functions.
    @staticmethod
    def init():
        Nonogram.lib.checkSolvability.restype = c_bool
        Nonogram.lib.createNonogram.restype = c_char_p

    # Tells if specified nonogram is solvable or not.
    # Receives json in the following format:
    # {"backgroundColor": int, "width": int, "height": int, "colors": int, "pixels": str}
    @staticmethod
    def solve(nonogram):
        Logger.writeInfo("Solving nonogram...\nwidth: {}, height: {}, colors: {}"
                .format(nonogram["width"], nonogram["height"], nonogram["colors"]))

        data = dumps(nonogram, separators=(',', ':')).encode()
        try:
            result = Nonogram.lib.checkSolvability(c_char_p(data))
            Logger.writeInfo("Specified crossword is solvable: {}".format(result))
            return {"response": "ok", "data": result}
        except (JSONDecodeError, ArgumentError) as e:
            Logger.writeError("Nonogram, solve: " + str(e))
            return {"response": "fail", "desc": "error when solving nonogram occurred"}

    # Creates nonogram from received image.
    # Receives json in the following format:
    # {"backgroundColor": int, "width": int, "height": int, "colors": int, "pixels": str}
    # Returns data in the following format:
    # {"solvable": bool, image: {"backgroundColor": int, "width": int, "height": int, "colors": int, "pixels": str}}
    @staticmethod
    def create(image):
        Logger.writeInfo("Creating nonogram...\nwidth: {}, height: {}, colors: {}"
                .format(image["width"], image["height"], image["colors"]))
        
        data = dumps(image, separators=(',', ':')).encode()
        try:
            result = Nonogram.lib.createNonogram(c_char_p(data))
            data = loads(result, encoding="UTF-8")
            Logger.writeInfo("Created nonogram from specified image")
            return {"response": "ok", "data": data}
        except (JSONDecodeError, ArgumentError) as e:
            Logger.writeError("Nonogram, create: " + str(e))
            return {"response": "fail", "desc": "error when creating nonogram occurred"}
