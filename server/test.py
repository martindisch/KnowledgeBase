import SocketServer
import json
import codecs
import os

class MyTCPServer(SocketServer.ThreadingTCPServer):
    allow_reuse_address = True

class MyTCPServerHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        try:
            # data is received in utf-8 and decoded to unicode
            data = self.request.recv(1024).strip().decode('utf-8')
            print data
            self.request.sendall(json.dumps({'msg': 'Yeah, that worked'}))
        except Exception, e:
            print "Exception wile receiving message: ", e
            print "Sending exception"
            try:
                self.request.sendall(json.dumps({'error': str(e)}))
                print "Sent exception"
            except Exception, e:
                print "Failed to send exception: ", e

ip = raw_input("Internal IP of server: ")
server = MyTCPServer((ip, 13373), MyTCPServerHandler)
if not os.path.exists("store"):
    os.mkdir("store")
print "Server running"
server.serve_forever()
