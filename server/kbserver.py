import SocketServer
import json
import codecs

class MyTCPServer(SocketServer.ThreadingTCPServer):
    allow_reuse_address = True

class MyTCPServerHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        try:
            # data is sent in utf-8 and decoded to unicode
            data = json.loads(self.request.recv(1024).strip().decode('utf-8'))
            if data['command'] == "ping":
                print "Received ping"
                # unicode data is converted to utf-8 in dumps()
                self.request.sendall(json.dumps({'response': 'pong'}))
                print "Sent pong"
            elif data['command'] == "entries":
                print "entries"
            elif data['command'] == "get":
                print "get"
            elif data['command'] == "store":
                print "Received store"
                # stores unicode data in utf-8
                backup = codecs.open(data['date'], 'w', encoding='utf-8')
                backup.write(data['data'])
                backup.close()
                print "File saved"
                # unicode data is converted to utf-8 in dumps()
                self.request.sendall(json.dumps({'response': 'ok'}))
                print "Sent ok"
            else:
                print "No command received"
        except Exception, e:
            print "Exception wile receiving message: ", e

server = MyTCPServer(('127.0.0.1', 13373), MyTCPServerHandler)
print "Server running"
server.serve_forever()
