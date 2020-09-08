import socket
from threading import Thread
from SocketServer import ThreadingMixIn


HOST = socket.gethostbyaddr("52.77.68.125")[0]               
PORT = 9999

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.bind((HOST, PORT))

socket.listen(1)
while True:
    conn, addr = socket.accept()
    print 'New client connected ..'
    reqCommand = conn.recv()
    print 'Client> %s' %(reqCommand)
    if (reqCommand == 'quit'):
        break
    #elif (reqCommand == lls):
        #list file in server directory
    else:
        string = reqCommand.split(' ', 1)   #in case of 'put' and 'get' method
        reqFile = 'enc.'+string[1] 

        if (string[0] == 'put'):
            with open(reqFile, 'wb') as file_to_write:
                while True:
                    data = conn.recv()
                    if not data:
                        break
                    file_to_write.write(data)
                    file_to_write.close()
                    break
            print 'Receive Successful'
        elif (string[0] == 'get'):
            with open(reqFile, 'rb') as file_to_send:
                for data in file_to_send:
                    conn.sendall(data)
            print 'Send Successful'
    conn.close()

socket.close()                