import os

import logging
import logging.handlers

# Provides ability to write some information to log.
class Logger:
    logger = None

    # Initializes logger by setting format and output file for log messages.
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

    # Prints specified message to log with INFO level.
    @staticmethod
    def writeInfo(msg):
        Logger.logger.info(msg)

    # Prints specified message to log with ERROR level.
    @staticmethod
    def writeError(msg):
        Logger.logger.error(msg)

    # Prints specified message to log with WARNING level.
    @staticmethod
    def writeWarning(msg):
        Logger.logger.warning(msg)
