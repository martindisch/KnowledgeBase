import socket
import json

while 1:
    data = input("What do you want to send? ")
    json_string = json.dumps(data) # unicode data is converted to utf-8 in dumps()
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(('127.0.0.1', 13373))
    s.send(json_string)
    # data is received in utf-8 and converted to unicode
    result = json.loads(s.recv(1024).decode('utf-8'))
    print json.dumps(result, indent=4, ensure_ascii=False)
    s.close()
