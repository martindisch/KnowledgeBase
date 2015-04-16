import SocketServer
import json
import codecs
import os

class MyTCPServer(SocketServer.ThreadingTCPServer):
    allow_reuse_address = True

class MyTCPServerHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        try:
            # data is received in utf-8 and decoded to byte strings
            data = json.loads(self.request.recv(1024).strip().decode('utf-8'))
            if data['command'] == "ping":
                print "Received ping"
                # byte string data is converted to utf-8 in dumps()
                self.request.sendall(json.dumps({'response': 'pong'}))
                print "Sent pong"
            elif data['command'] == "entries":
                print "Received entries"
                files = [f for f in os.listdir("store") if os.path.isfile(os.path.join("store", f))]
                # byte string data is converted to utf-8 in dumps()
                self.request.sendall(json.dumps({'entries': list(reversed(files))}))
                print "Sent entries"
            elif data['command'] == "get":
                print "Received get "
                backup = codecs.open("store/" + data['date'], 'r', encoding='utf-8')
                content = backup.read()
                backup.close()
                print "Read file"
                self.request.sendall(json.dumps({'data': content}))
                print "Sent content"
            elif data['command'] == "store":
                print "Received store"
                # stores byte string data in utf-8
                backup = codecs.open("store/" + data['date'], 'w', encoding='utf-8')
                backup.write(data['data'])
                backup.close()
                print "File saved"
                # byte string data is converted to utf-8 in dumps()
                self.request.sendall(json.dumps({'response': 'ok'}))
                print "Sent ok"
            else:
                print "No command received"
        except Exception, e:
            print "Exception wile receiving message: ", e

server = MyTCPServer(('127.0.0.1', 13373), MyTCPServerHandler)
if not os.path.exists("store"):
    os.mkdir("store")
print "Server running"
server.serve_forever()
