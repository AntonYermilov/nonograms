#!/usr/bin/python3.6

import sys, os

from socket import *
from struct import unpack

from data_handler import DataHandler
from logger import Logger
from nonogram import NonogramLogic

HOST = ""
PORT = 5222

# Provides interaction with client
class Server:
    
    BUFFER_SIZE = 4096
    MAX_INPUT_SIZE = 120 * 1024

    # Creates server and binds it to the specified host and port
    def __init__(self, host, port):
        self.host = host
        self.port = port

    # Starts listening queries from clients
    def runServer(self):
        Logger.writeInfo("Opening socket...")
        sock = socket(AF_INET, SOCK_STREAM)
        sock.bind((self.host, self.port))
        sock.listen(10)
        handler = DataHandler()
        
        try:
            while True:
                conn, addr = sock.accept()
                Logger.writeInfo("Connected by {}".format(addr))
                
                try:
                    data = Server.receiveAll(conn)
                    if data != None:
                        response = handler.process(data)
                        conn.sendall(response)
                    conn.close()
                except Exception as e:
                    Logger.writeError(str(e))
                
                Logger.writeInfo("Disconnected {}".format(addr))
        finally:
            Logger.writeInfo("Socket closed")
            sock.close()
            handler.close()

    # Receives all data from client. If input is too big, closes the connection.
    @staticmethod
    def receiveAll(conn):
        (length,) = unpack('<i', conn.recv(4))
        Logger.writeInfo("Receiving " + str(length) + " bytes...")
        if length > Server.MAX_INPUT_SIZE:
            Logger.writeInfo("Input is too big")
            return None
        data = b''
        while len(data) < length:
            to_read = min(Server.BUFFER_SIZE, length - len(data))
            recv = conn.recv(to_read)
            if len(recv) == 0:
                Logger.writeInfo("Some data is lost")
                return None
            data += recv
        return data
    
    #TODO
    @staticmethod
    def authenticate(key):
        return True

if __name__ == "__main__":
    Logger.init()
    NonogramLogic.init()

    server = Server(HOST, PORT)
    while True:
        server.runServer()
