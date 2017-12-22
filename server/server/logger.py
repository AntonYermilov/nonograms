import os

import logging
import logging.handlers

class Logger:
    logger = None

    @staticmethod
    def init():
        Logger.logger = logging.getLogger("server")
        Logger.logger.setLevel(logging.DEBUG)
        
        path = os.path.join(os.path.abspath(""), "log", "server.log")

        handler = logging.FileHandler(path, "w")
        handler.setLevel(logging.DEBUG)
    
        formatter = logging.Formatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")
        handler.setFormatter(formatter)
        Logger.logger.addHandler(handler)

    @staticmethod
    def writeInfo(msg):
        Logger.logger.info(msg)

    @staticmethod
    def writeError(msg):
        Logger.logger.error(msg)

    @staticmethod
    def writeWarning(msg):
        Logger.logger.warning(msg)
