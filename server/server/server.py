#!/usr/bin/python3.6

from socket import *
from struct import unpack
from data_handler import DataHandler

class ServerProtocol:
    
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.buffer_size = 4096
        self.socket = socket(AF_INET, SOCK_STREAM)
        self.socket.bind((host, port))
        self.socket.listen(10)
        self.handler = DataHandler()

    def run_server(self):
        try:
            while True:
                conn, addr = self.socket.accept()
                print('Connected by', addr)
                try:
                    data = self.receive_all(conn)
                    print(data)
                    response = self.handler.process(data)
                    conn.sendall(response)
                finally:
                    conn.shutdown(SHUT_RDWR)
                    conn.close()
        finally:
            self.socket.close()
            self.socket = None

    def receive_all(self, conn):
        (length,) = unpack('<i', conn.recv(4))
        print(length)
        data = b''
        while len(data) < length:
            to_read = min(self.buffer_size, length - len(data))
            data += conn.recv(to_read)
        return data

if __name__ == '__main__':
    server = ServerProtocol('', 5222)
    server.run_server()
