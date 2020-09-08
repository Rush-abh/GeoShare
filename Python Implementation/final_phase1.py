import os
from Crypto.Cipher import AES
from Crypto.Hash import SHA256
from Crypto import Random
from Tkinter import *

def encrypt():
	chunksize = 64*1024
	outputFile = "(encrypted)"+ filename.get()
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
	outputFile = filename.get()[11:]

	with open(filename.get(), 'rb') as infile:
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

global filename,key
root = Tk()

label1 = Label(root,text="Select file")
label2 = Label(root,text="key")

filename = Entry(root)
key = Entry(root)


btn1 = Button(root,text="Encrypt",command=encrypt)
btn2 = Button(root,text="Decrypt",command=decrypt)

label1.grid(row=0)
label2.grid(row=1)

filename.grid(row=0,column=1)
key.grid(row=1,column=1)

btn1.grid(row=2)
btn2.grid(row=2,column=1)

root.mainloop()
