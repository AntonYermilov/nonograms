import os

from json import loads, dumps, JSONDecodeError
from struct import pack

from database_manager import DatabaseManager
from logger import Logger
from nonogram import Nonogram

# Receives queries and handles them according to their types.
class DataHandler:
    
    # Creates new data handler, connects to database using username and password saved in config file.
    def __init__(self):
        path = os.path.join(os.path.abspath(""), "config", "database.config")
        with open(path) as config:
            user = config.readline().strip()
            pwd = config.readline().strip()
        self.database = DatabaseManager(user, pwd, "nonogramDB")

    # Handles queries according to their type. Returns response to each query.
    def process(self, data):
        try:
            query = loads(data, encoding="UTF-8")
            Logger.writeInfo("Query type: {}".format(query["type"]))
            if query["type"] == "loadNonogram":
                return self.createResponse(self.database.loadNonogram(query["data"]))
            if query["type"] == "getNonogramPreviewInfo":
                return self.createResponse(self.database.getNonogramPreviewInfo(query["data"]))
            if query["type"] == "getNonogramById":
                return self.createResponse(self.database.getNonogramById(query["data"]))
            if query["type"] == "solveNonogram":
                return self.createResponse(Nonogram.solve(query["data"]))
            if query["type"] == "createNonogram":
                return self.createResponse(Nonogram.create(query["data"]))
            Logger.writeError("Method " + query["type"] + " does not exist")
            return self.createResponse({"response": "fail", "desc": "Method '{}' not implemented yet".format(query["type"])})
    
        except (JSONDecodeError, KeyError) as e:
            Logger.writeError("DataHandler.process: " + str(e))
            return self.createResponse({"response": "fail", "desc": str(e)})

    # Packs size of response object and inserts it at the beginning of response string, creating the response.
    def createResponse(self, obj):
        msg = dumps(obj, separators=(',', ':')).encode()
        length = pack("<i", len(msg))
        Logger.writeInfo("Created response")
        return length + msg
    
