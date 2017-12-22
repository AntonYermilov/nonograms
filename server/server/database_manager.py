import mysql.connector as mariadb
from json import loads, dumps

from logger import Logger

TABLES = {}
TABLES["nonogram"] = (
        "CREATE TABLE IF NOT EXISTS `nonogram` ("
        "`id` INT AUTO_INCREMENT PRIMARY KEY,"
        "`name` TEXT(32),"
        "`author` TEXT(32),"
        "`height` INT NOT NULL,"
        "`width` INT NOT NULL,"
        "`data` MEDIUMTEXT);")

class DatabaseManager:

    def __init__(self, user, password, db_name):
        self.db_name = db_name
        self.mariadb_connection = mariadb.connect(user=user, password=password, database=db_name)
        self.cursor = self.mariadb_connection.cursor()
        
        for name, cmd in TABLES.items():
            try:
                Logger.writeInfo("Creating table `%s`: " % name)
                self.cursor.execute(cmd)
                Logger.writeInfo("OK")
            except mariadb.Error as e:
                Logger.writeError(e.msg)

    def loadNonogram(self, data):
        try:
            self.cursor.execute("INSERT INTO `nonogram` (`name`, `author`, `height`, `width`, `data`)"
                    " VALUES ('%s', '%s', %d, %d, '%s');" % (data["name"], data["author"], data["data"]["height"], data["data"]["width"], 
                        dumps(data["data"], separators=(',', ':'))))
            self.mariadb_connection.commit()
            return {"response": "ok", "data": ""}
        except mariadb.Error as e:
            Logger.writeError(e.msg)
            return {"response": "fail", "desc": e.msg}

    def getNonogramPreviewInfo(self, data):
        try:
            self.cursor.execute("SELECT `id`, `name`, `author`, `height`, `width` FROM nonogram LIMIT %d, %d;" % (data["from"], data["amount"]))
            response = {"response": "ok", "data": []}
            for Id, name, author, height, width in self.cursor:
                response["data"].append({"id": Id, "name": name, "author": author, "height": height, "width": width})
            return response
        except mariadb.Error as e:
            Logger.writeError(e.msg)
            return {"response": "fail", "desc": e.msg}

    def getNonogramById(self, data):
        try:
            self.cursor.execute("SELECT `data` FROM `nonogram` WHERE id=%d;" % data["id"])
            row = self.cursor.fetchone()
            if row is None:
                return {"response": "fail", "desc": "nonogram with such `id` does not exist"}
            return {"response": "ok", "data": row[0]}
        except mariadb.Error as e:
            Logger.writeError(e.msg)
            return {"response": "fail", "desc": e.msg}

    def close(self):
        self.cursor.close()
        self.mariadb_connection.close()

