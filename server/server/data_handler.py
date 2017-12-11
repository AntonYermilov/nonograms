from json import loads, dumps, JSONDecodeError
from struct import pack
from database_manager import DatabaseManager

class DataHandler:
    
    def __init__(self):
        with open("config/database.config") as config:
            user = config.readline().strip()
            pwd = config.readline().strip()
        self.database = DatabaseManager(user, pwd, "nonogramDB")

    def process(self, data):
        try:
            query = loads(data, encoding="UTF-8")
            if query["type"] == "loadNonogram":
                return self.createResponse(self.database.loadNonogram(query["data"]))
            if query["type"] == "getNonogramPreviewInfo":
                return self.createResponse(self.database.getNonogramPreviewInfo(query["data"]))
            if query["type"] == "getNonogramById":
                return self.createResponse(self.database.getNonogramById(query["data"]))
            return self.createResponse({"response": "fail", "desc": "Method '%s' not implemented yet" % query["type"]})
    
        except (JSONDecodeError, KeyError) as e:
            return self.createResponse({"response": "fail", "desc": e.msg})

    def createResponse(self, obj):
        msg = dumps(obj, separators=(',', ':')).encode()
        length = pack("<i", len(msg))
        return length + msg
    
