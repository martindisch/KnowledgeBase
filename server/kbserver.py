import SocketServer
import json

class MyTCPServer(SocketServer.ThreadingTCPServer):
    allow_reuse_address = True

class MyTCPServerHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        try:
            data = json.loads(self.request.recv(1024).strip())
            # switch between commands
            if data['command'] == "ping":
                print "Received ping"
                self.request.sendall(json.dumps({'response': 'pong'}))
                print "Sent pong"
            elif data['command'] == "entries":
                print "entries"
            elif data['command'] == "get":
                print "get"
            elif data['command'] == "store":
                print "Received store"
                backup = open(data['date'], 'w')
                backup.write(data['data'])
                backup.close()
                print "File saved"
                self.request.sendall(json.dumps({'response': 'ok'}))
                print "Sent ok"
            else:
                print "No command received"
        except Exception, e:
            print "Exception wile receiving message: ", e

server = MyTCPServer(('127.0.0.1', 13373), MyTCPServerHandler)
server.serve_forever()
