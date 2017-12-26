#!/usr/bin/python3.6

import sys, os

from socket import *
from struct import unpack

from data_handler import DataHandler
from logger import Logger
from nonogram import Nonogram

HOST = ""
PORT = 5222

# Provides interaction with client
class ServerProtocol:
    
    # Creates server and binds it to the specified host and port
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.buffer_size = 4096
        self.socket = socket(AF_INET, SOCK_STREAM)
        self.socket.bind((host, port))
        self.socket.listen(10)
        self.handler = DataHandler()

    # Starts listening queries from clients
    def run_server(self):
        try:
            while True:
                conn, addr = self.socket.accept()
                Logger.writeInfo("Connected by {}".format(addr))
                try:
                    data = self.receive_all(conn)
                    response = self.handler.process(data)
                    conn.sendall(response)
                except Exception as e:
                    Logger.writeError(str(e))
                finally:
                    conn.shutdown(SHUT_RDWR)
                    conn.close()
                Logger.writeInfo("Disconnected {}".format(addr))
        finally:
            self.socket.close()
            self.socket = None

    # Receives all data from client
    def receive_all(self, conn):
        (length,) = unpack('<i', conn.recv(4))
        Logger.writeInfo(length)
        data = b''
        while len(data) < length:
            to_read = min(self.buffer_size, length - len(data))
            data += conn.recv(to_read)
        return data

if __name__ == "__main__":
    Logger.init()
    Nonogram.init()

    server = ServerProtocol(HOST, PORT)
    server.run_server()
