import socket
import json

while 1:
    data = input("What do you want to send? ")
    #json_string = json.dumps(unicode(data), ensure_ascii=False).encode('utf-8')
    json_string = json.dumps(data, ensure_ascii=False)
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(('127.0.0.1', 13373))
    s.send(json_string)
    result = json.loads(s.recv(1024))
    print json.dumps(result, indent=4)
    s.close()