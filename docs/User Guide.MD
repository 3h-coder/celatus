# User Guide

## Table of contents

[1. Setting up your master password](#1-setting-up-your-master-password)

[2. Creating a category](#2-creating-a-category)

[3. Creating a password entry](#3-creating-a-password-entry)

[4. Interacting with the password entry](#4-interacting-with-the-password-entry)

[5. Password search](#5-password-search)

---

### 1. Setting up your master password

Upon the first startup, set your master password. Note that **it will not be saved anywhere** so do not forget it or you may lose all your password data.

![setupWindow image](https://i.imgur.com/wudVDXa.png)

---

### 2. Creating a category

To create a new category, you can either go to File -> new category, or press shift+C.

![](https://i.imgur.com/C5sZecW.png)

After writing down its name and description (optional), click the Save button.

![](https://i.imgur.com/5ACXzKY.png)

---

### 3. Creating a password entry

To create a password entry, select a category first. You can then either go to File -> new password entry or press shift+P.

![](https://i.imgur.com/QjaBVXA.png)

Note that the name, identifier and password are mandatory. Once done, click on the Save button.

![](https://i.imgur.com/7btBOkc.png)

Your password has now been created, and appears whenever you click or select the corresponding category. It is recommended that you save the file through ctrl+S.

![](https://i.imgur.com/vnmiO6U.png)

---

### 4. Interacting with the password entry

To interact with the password entry you created, right click on it.

![](https://i.imgur.com/T8hCc3g.png)

Note that you can : 
- Edit the password entry
- Delete it
- Move it to another category
- Open the associated url (if provided)
- Copy the identifier and password to the clipboard

Also note :
- Deletion and category change can be reverted through ctrl+Z, but only before a file save.
- Creations, deletions and category change are recorded in the log file by default.

---

### 5. Password search

To find a password, simply type its name (or the beginning of its name) in the search bar.

![](https://i.imgur.com/EXYIwkV.png)

You may also search by using other attributes : 
- To search passwords by their identifier, type -id: followed by your query.
- To search by email, type -email: or -mail: followed by your query.
- To search by password, type -pwd: or -password: followed by your query.

![](https://i.imgur.com/ch5Oqm3.png)

To display all the password entries, type -all in the search bar.

![](https://i.imgur.com/X445jCF.png)

**Note :** Columns are sortable.