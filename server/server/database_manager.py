import os

from sqlobject import *
from json import loads, load, dumps, dump

from logger import Logger
from database_objects import *

TABLES = [Nonogram]

class DatabaseManager:

    def __init__(self, user, password, db_name, db_storage):
        sqlhub.processConnection = connectionForURI('mysql://{}:{}@/{}'.format(user, password, db_name))
        self.db_storage = db_storage

        for table in TABLES:
            try:
                Logger.writeInfo("Creating table `{}`: ".format(table.__name__))
                table.createTable(ifNotExists=True)
                Logger.writeInfo("OK")
            except dberrors.Error as e:
                Logger.writeError("Error when creating table occurred, args: {}".format(e.args))

    def saveNonogram(self, data):
        try:
            image = data["data"]
            id = Nonogram(name=data["name"], author=data["author"], height=image["height"], width=image["width"]).id

            with open(os.path.join(self.db_storage, str(id)), 'w') as image_storage:
                dump(image, image_storage, separators=(',', ':'))
            return {"response": "ok", "data": ""}
        except (OSError, dberrors.Error) as e:
            Logger.writeError("Error when saving nonogram occurred, args: {}".format(e.args))
            return {"response": "fail", "desc": "saving nonogram failed"}

    def getNonogramPreviewInfo(self, data):
        try:
            response = {"response": "ok", "data": []}
            for image in list(Nonogram.select()):
                response["data"].append(dict((c, getattr(image, c)) for c in image.sqlmeta.columns))
                response["data"][-1]["id"] = image.id
            return response
        except dberrors.Error as e:
            Logger.writeError("Error when getting preview info occurred, args: {}".format(e.args))
            return {"response": "fail", "desc": "getting preview info failed"}

    def getNonogramById(self, data):
        id = data["id"] 
        try:
            with open(os.path.join(self.db_storage, str(id)), 'r') as image_storage:
                return {"response": "ok", "data": load(image_storage)}
        except OSError as e:
            Logger.writeError("Image {} not found".format(id))
            return {"response": "fail", "desc": "image not found"}

    def close(self):
        sqlhub.processConnection = None

