import os
from Crypto.Cipher import AES
from Crypto.Hash import SHA256
from Crypto import Random
from Tkinter import *
import socket
import sys

button_bg = "#283593"
TCP_IP = '52.77.68.125'
TCP_PORT = 9999
    
def encrypt():
	chunksize = 64*1024
	outputFile = "enc."+ filename.get()
	filesize = str(os.path.getsize(filename.get())).zfill(16)
	IV = Random.new().read(16)

	encryptor = AES.new(getKey(key), AES.MODE_CBC, IV)

	with open(filename.get(), 'rb') as infile:
		with open(outputFile, 'wb') as outfile:
			outfile.write(filesize.encode('utf-8'))
			outfile.write(IV)

			while True:
				chunk = infile.read(chunksize)

				if len(chunk) == 0:
					break
				elif len(chunk) % 16 != 0:
					chunk += b' ' * (16 - (len(chunk) % 16))

				outfile.write(encryptor.encrypt(chunk))


def decrypt():
	chunksize = 64*1024
	outputFile = filename.get()

	with open("enc."+filename.get(), 'rb') as infile:
		filesize = int(infile.read(16))
		IV = infile.read(16)

		decryptor = AES.new(getKey(key), AES.MODE_CBC, IV)

		with open(outputFile, 'wb') as outfile:
			while True:
				chunk = infile.read(chunksize)

				if len(chunk) == 0:
					break

				outfile.write(decryptor.decrypt(chunk))
			outfile.truncate(filesize)


def getKey(key):
	hasher = SHA256.new(key.get().encode('utf-8'))
	return hasher.digest()
    
def sendfile():

    print 'TCP_IP=',TCP_IP
    print 'TCP_PORT=',TCP_PORT
    socket1 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    socket1.connect((TCP_IP, TCP_PORT))
    
    command = 'put ' + filename.get()
    socket1.send(command)
    send_file = 'enc.'+filename.get()
    with open(send_file, 'rb') as file_to_send:
        for data in file_to_send:
            socket1.sendall(data)
    print 'PUT Successful'
    socket1.close()
    
def recfile():
    socket1 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    socket1.connect((TCP_IP, TCP_PORT))
    command = 'get ' + filename.get()
    socket1.send(command)
    inputFile = 'enc.'+filename.get()
    with open(inputFile, 'wb') as file_to_write:
        while True:
            data = socket1.recv(1024)
            # print data
            if not data:
                break
            # print data
            file_to_write.write(data)
    file_to_write.close()
    print 'GET Successful'
    socket1.close()
    
    
    
    
    

global filename,key
root = Tk()
root.geometry("560x240")
root.configure(bg="#ffffff")

label1 = Label(root,text="Select file:",font=8,bg="#ffffff")
label2 = Label(root,text="Key:",font=8,bg="#ffffff")

filename = Entry(root,borderwidth=2)
key = Entry(root,borderwidth=2)


btn1 = Button(root,text="Encrypt",width=6,font=6,highlightcolor=button_bg,borderwidth=2,bg=button_bg,fg="white",relief=GROOVE,command=encrypt)
btn2 = Button(root,text="Decrypt",width=6,font=6,highlightcolor=button_bg,borderwidth=2,bg=button_bg,fg="white",relief=GROOVE,command=decrypt)
btn3 = Button(root,text="Send",width=6,font=6,highlightcolor=button_bg,borderwidth=2,bg=button_bg,fg="white",relief=GROOVE,command=sendfile)
btn4 = Button(root, text="Receive",width=6,font=6,highlightcolor=button_bg,borderwidth=2,bg=button_bg,fg="white",relief=GROOVE,command=recfile)


label1.place(relx=0.1, rely=0.1, anchor=CENTER)
label2.place(relx=0.1, rely=0.2, anchor=CENTER)

filename.place(relx=0.3, rely=0.1, anchor=W,width=350)
key.place(relx=0.3, rely=0.2, anchor=W,width=350)

btn1.place(relx=0.2, rely=0.4, anchor=CENTER)
btn2.place(relx=0.4, rely=0.4, anchor=CENTER)
btn3.place(relx=0.6, rely=0.4, anchor=CENTER)
btn4.place(relx=0.8, rely=0.4, anchor=CENTER)

root.mainloop()
