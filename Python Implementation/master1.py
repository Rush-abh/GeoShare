from Tkinter import *


button_bg = "#283593"

class Page(Frame):
    def __init__(self, *args, **kwargs):
        Frame.__init__(self, *args, **kwargs)
    def show(self):
        self.lift()

class Page1(Page):
   def __init__(self, *args, **kwargs):

	   Page.__init__(self, *args, **kwargs)


	   fname = Label(self, text = "First Name")
	   lname = Label(self, text = "Last Name")
	   email = Label(self, text = "Email ID")
	   password = Label(self, text = "Password")
	   conf_password = Label(self, text = "Confirm Password")

	   fname_entry = Entry(self)
	   lname_entry = Entry(self)
	   email_entry = Entry(self)
	   password_entry = Entry(self)
	   conf_password_entry = Entry(self)

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



class MainView(Frame):
    def __init__(self, *args, **kwargs):
        Frame.__init__(self, *args, **kwargs)
        p1 = Page1(self)
        #p2 = Page2(self)

        buttonframe = Frame(self)
        container = Frame(self)
        buttonframe.pack(side="top", fill="x", expand=False)
        container.pack(side="top", fill="both", expand=True)


        reg_button = Button(root, text="Register",width=8,font=12,highlightcolor=button_bg,borderwidth=2,bg=button_bg,fg="white",relief=GROOVE, command = p1.lift)
        #login_button = Button(root, text = "Login",width=8,font=12,highlightcolor=button_bg,borderwidth=2,bg=button_bg,fg="white",relief=GROOVE,command = p2.lift)

        reg_button.place(relx=0.4, rely=0.5, anchor=CENTER)
        #login_button.place(relx=0.6, rely=0.5, anchor=CENTER)

        """b1 = tk.Button(buttonframe, text="Page 1", command=p1.lift)
        b2 = tk.Button(buttonframe, text="Page 2", command=p2.lift)
        b3 = tk.Button(buttonframe, text="Page 3", command=p3.lift)

        b1.pack(side="left")
        b2.pack(side="left")
        b3.pack(side="left")"""

        p1.show()

if __name__ == "__main__":
    root = Tk()
    main = MainView(root)
    main.pack(side="top", fill="both", expand=True)
    root.wm_geometry("400x400")
    root.mainloop()
