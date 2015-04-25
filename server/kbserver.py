import SocketServer
import json
import codecs
import os
import sys

class MyTCPServer(SocketServer.ThreadingTCPServer):
    allow_reuse_address = True

class MyTCPServerHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        try:
            done = False
            received = ""
            while done != True:
                # data is received in UTF-8 and decoded to Unicode
                received += self.request.recv(1024).strip().decode('utf-8')
                try:
                    data = json.loads(received)
                except ValueError:
                    # if json could not be decoded, data is
                    # probably not complete yet
                    print "Packet received, getting more"
                else:
                    done = True
            if data['command'] == "ping":
                print "Received ping"
                self.request.sendall(json.dumps({'response': 'pong'}))
                print "Sent pong"
            elif data['command'] == "entries":
                print "Received entries"
                files = [f for f in os.listdir("store") if os.path.isfile(os.path.join("store", f))]
                self.request.sendall(json.dumps({'response': sorted(files, reverse=True)}))
                print "Sent entries"
            elif data['command'] == "get":
                print "Received get"
                # read and decode utf-8 into unicode
                backup = codecs.open("store/" + data['date'], 'r', encoding='utf-8')
                content = backup.read()
                backup.close()
                print "Read file"
                self.request.sendall(json.dumps({'response': content}))
                print "Sent content"
            elif data['command'] == "store":
                print "Received store"
                # stores unicode data in utf-8
                backup = codecs.open("store/" + data['date'], 'w', encoding='utf-8')
                backup.write(data['data'])
                backup.close()
                print "File saved"
                self.request.sendall(json.dumps({'response': 'ok'}))
                print "Sent ok"
            elif data['command'] == "delete":
                print "Received delete"
                os.remove("store/" + data['date'])
                print "File removed"
                self.request.sendall(json.dumps({'response': 'ok'}))
                print "Sent ok"
            else:
                raise Exception("No/unknown command received")
        except Exception, e:
            print "Exception wile receiving message: ", e
            print "Sending exception"
            try:
                self.request.sendall(json.dumps({'error': str(e)}))
                print "Sent exception"
            except Exception, e:
                print "Failed to send exception: ", e

server = MyTCPServer((sys.argv[1], 13373), MyTCPServerHandler)
if not os.path.exists("store"):
    os.mkdir("store")
print "Server running"
server.serve_forever()
