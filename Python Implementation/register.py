import bcrypt
from Tkinter import *

button_bg = "#283593"

def register_frame():
	reg_frame = LabelFrame(root, text="Registeration")
	reg_frame.place()
	
	fname = Label(reg_frame, text = "First Name") 
	lname = Label(reg_frame, text = "Last Name") 
	email = Label(reg_frame, text = "Email ID") 
	password = Label(reg_frame, text = "Password") 
	conf_password = Label(reg_frame, text = "Confirm Password") 
	
	fname_entry = Entry(reg_frame)
	lname_entry = Entry(reg_frame)
	email_entry = Entry(reg_frame)
	password_entry = Entry(reg_frame)
	conf_password_entry = Entry(reg_frame)
	
	fname.place(relx=0.2, rely=0.2, anchor=CENTER)
	lname.place(relx=0.2, rely=0.3, anchor=CENTER)
	email.place(relx=0.2, rely=0.4, anchor=CENTER)
	password.place(relx=0.2, rely=0.5, anchor=CENTER)
	conf_password.place(relx=0.2, rely=0.6, anchor=CENTER)
	fname_entry.place(relx=0.4, rely=0.5, anchor=CENTER)
	lname_entry.place(relx=0.4, rely=0.5, anchor=CENTER)
	email_entry.place(relx=0.4, rely=0.5, anchor=CENTER)
	password_entry.place(relx=0.4, rely=0.5, anchor=CENTER)
	conf_password_entry.place(relx=0.4, rely=0.5, anchor=CENTER)


	
root = Tk()
root.resizable(width=FALSE, height=FALSE)
root.configure(bg="#ffffff")
root.title("Log-In")
root.geometry("720x460")

reg_button = Button(root, text="Register",width=8,font=12,highlightcolor=button_bg,borderwidth=2,bg=button_bg,fg="white",relief=GROOVE, command = register_frame)
login_button = Button(root, text = "Login",width=8,font=12,highlightcolor=button_bg,borderwidth=2,bg=button_bg,fg="white",relief=GROOVE)

reg_button.place(relx=0.4, rely=0.5, anchor=CENTER)
login_button.place(relx=0.6, rely=0.5, anchor=CENTER)


root.mainloop()