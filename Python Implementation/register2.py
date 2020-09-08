import bcrypt
import Tkinter as tk
from ttk import *

button_bg = "#283593"
	


	
root = tk.Tk()
root.title("Welcome to GeoShare")
root.resizable(width=tk.FALSE, height=tk.FALSE)
root.configure(bg="#fff1f2")
root.geometry("720x460")

notebook = Notebook(root)
s = Style()
s.configure(notebook,background='cyan')
reg_frame = tk.Frame(notebook)
frame2 = tk.Frame(notebook)
notebook.add(reg_frame, text='Registration')
notebook.add(frame2, text='Login')
notebook.pack(expand=True, fill=tk.BOTH)


fname = tk.Label(reg_frame, text = "First Name") 
lname = tk.Label(reg_frame, text = "Last Name") 
email = tk.Label(reg_frame, text = "Email ID") 
password = tk.Label(reg_frame, text = "Password") 
conf_password = tk.Label(reg_frame, text = "Confirm Password")

reg_button = tk.Button(reg_frame, text="Register",width=8,font=12,highlightcolor=button_bg,borderwidth=2,bg=button_bg,fg="white",relief=tk.GROOVE)

fname_entry = tk.Entry(reg_frame)
lname_entry = tk.Entry(reg_frame)
email_entry = tk.Entry(reg_frame)
password_entry = tk.Entry(reg_frame)
conf_password_entry = tk.Entry(reg_frame)

fname.place(relx=0.2, rely=0.2, anchor=tk.CENTER)
lname.place(relx=0.2, rely=0.3, anchor=tk.CENTER)
email.place(relx=0.2, rely=0.4, anchor=tk.CENTER)
password.place(relx=0.2, rely=0.5, anchor=tk.CENTER)
conf_password.place(relx=0.2, rely=0.6, anchor=tk.CENTER)
reg_button.place(relx=0.5, rely=0.7, anchor=tk.CENTER)

fname_entry.place(relx=0.4, rely=0.2, anchor=tk.CENTER)
lname_entry.place(relx=0.4, rely=0.3, anchor=tk.CENTER)
email_entry.place(relx=0.4, rely=0.4, anchor=tk.CENTER)
password_entry.place(relx=0.4, rely=0.5, anchor=tk.CENTER)
conf_password_entry.place(relx=0.4, rely=0.6, anchor=tk.CENTER)








root.mainloop()