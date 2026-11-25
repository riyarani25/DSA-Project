
import java.io.*;
import java.util.*;

// 1. Contact Class
class Contact {
    String id, name, phone, email;

    Contact(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String toString() {
        return id + " | " + name + " | " + phone;
    }
}

// 2. Binary Search Tree
class ContactBST {

    class Node {
        String name;
        ArrayList<Contact> contacts = new ArrayList<>();
        Node left, right;

        Node(Contact c) {
            name = c.name.toLowerCase();
            contacts.add(c);
        }
    }

    Node root;

    // Insert contact into BST
    void insert(Contact c) {
        root = insertNode(root, c);
    }

    Node insertNode(Node root, Contact c) {

        if (root == null)
            return new Node(c);

        String key = c.name.toLowerCase();

        if (key.compareTo(root.name) < 0)
            root.left = insertNode(root.left, c);

        else if (key.compareTo(root.name) > 0)
            root.right = insertNode(root.right, c);

        else
            root.contacts.add(c);

        return root;
    }

    // Search contact by name
    ArrayList<Contact> search(String name) {
        Node temp = root;
        name = name.toLowerCase();

        while (temp != null) {

            int result = name.compareTo(temp.name);

            if (result == 0)
                return temp.contacts;

            else if (result < 0)
                temp = temp.left;

            else
                temp = temp.right;
        }

        return new ArrayList<>();
    }
}

// 3. Contact Manager Class
class ContactManager {

    // Linked List for storing contacts
    LinkedList<Contact> contacts = new LinkedList<>();

    // BST for searching names
    ContactBST bst = new ContactBST();

    // HashMap for searching phone numbers
    HashMap<String, Contact> phoneMap = new HashMap<>();

    int nextId = 101;

    String fileName = "contacts.txt";

    ContactManager() {
        loadContacts();
    }

    // Check phone number
    boolean validPhone(String phone) {
        return phone.matches("[0-9]{10}");
    }

    // Rebuild BST and HashMap after update or delete
    void rebuildIndexes() {

        bst = new ContactBST();
        phoneMap.clear();

        for (Contact c : contacts) {
            bst.insert(c);
            phoneMap.put(c.phone, c);
        }
    }

    // Add Contact
    void addContact(String name, String phone, String email) {

        if (name.trim().isEmpty() || email.trim().isEmpty()) {
            System.out.println("Name and email cannot be empty.");
            return;
        }

        if (!validPhone(phone)) {
            System.out.println("Invalid phone number. Enter 10 digits.");
            return;
        }

        if (phoneMap.containsKey(phone)) {
            System.out.println("Duplicate phone number.");
            return;
        }

        String id = "C" + nextId++;

        Contact c = new Contact(id, name.trim(), phone, email.trim());

        contacts.add(c);
        bst.insert(c);
        phoneMap.put(phone, c);

        System.out.println("Contact added: " + c);
    }

    // Display Contacts
    void displayContacts() {

        if (contacts.isEmpty()) {
            System.out.println("No contacts available.");
            return;
        }

        System.out.println("\n--- CONTACT LIST ---");

        for (Contact c : contacts) {
            System.out.println(c);
        }
    }

    // Search by Phone using HashMap
    void searchByPhone(String phone) {

        if (!validPhone(phone)) {
            System.out.println("Invalid phone number.");
            return;
        }

        Contact c = phoneMap.get(phone);

        if (c == null)
            System.out.println("Contact not found.");
        else
            System.out.println("Contact found: " + c);
    }

    // Search by Name using BST
    void searchByName(String name) {

        ArrayList<Contact> result = bst.search(name.trim());

        if (result.isEmpty()) {
            System.out.println("Contact not found.");
        } else {
            for (Contact c : result)
                System.out.println(c);
        }
    }

    // Update Contact
    void updateContact(String id, String name,
                       String phone, String email) {

        Contact found = null;

        for (Contact c : contacts) {
            if (c.id.equalsIgnoreCase(id)) {
                found = c;
                break;
            }
        }

        if (found == null) {
            System.out.println("Contact not found.");
            return;
        }

        if (name.trim().isEmpty() || email.trim().isEmpty()) {
            System.out.println("Name and email cannot be empty.");
            return;
        }

        if (!validPhone(phone)) {
            System.out.println("Invalid phone number.");
            return;
        }

        Contact existing = phoneMap.get(phone);

        if (existing != null && existing != found) {
            System.out.println("Duplicate phone number.");
            return;
        }

        found.name = name.trim();
        found.phone = phone;
        found.email = email.trim();

        rebuildIndexes();

        System.out.println("Contact updated successfully.");
    }

    // Delete Contact
    void deleteContact(String id) {

        Contact found = null;

        for (Contact c : contacts) {
            if (c.id.equalsIgnoreCase(id)) {
                found = c;
                break;
            }
        }

        if (found == null) {
            System.out.println("Contact not found.");
            return;
        }

        contacts.remove(found);

        rebuildIndexes();

        System.out.println("Contact deleted successfully.");
    }

    // Sort Contacts by Name
    void sortContacts() {

        ArrayList<Contact> list = new ArrayList<>(contacts);

        Collections.sort(list, (a, b) ->
            a.name.compareToIgnoreCase(b.name)
        );

        if (list.isEmpty()) {
            System.out.println("No contacts available.");
            return;
        }

        System.out.println("\n--- SORTED CONTACTS ---");

        int i = 1;

        for (Contact c : list) {
            System.out.println(i + ". " + c.id + " | " + c.name);
            i++;
        }
    }

    // Save Contacts to File
    void saveContacts() {

        try {
            BufferedWriter bw =
                new BufferedWriter(new FileWriter(fileName));

            for (Contact c : contacts) {

                bw.write(c.id + "|" + c.name + "|" +
                         c.phone + "|" + c.email);

                bw.newLine();
            }

            bw.close();

        } catch (IOException e) {
            System.out.println("File saving error.");
        }
    }

    // Load Contacts from File
    void loadContacts() {

        try {
            BufferedReader br =
                new BufferedReader(new FileReader(fileName));

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split("\\|", -1);

                if (data.length != 4)
                    continue;

                String id = data[0];
                String name = data[1];
                String phone = data[2];
                String email = data[3];

                if (!id.matches("C[0-9]+") ||
                    name.trim().isEmpty() ||
                    email.trim().isEmpty() ||
                    !validPhone(phone) ||
                    phoneMap.containsKey(phone))
                    continue;

                Contact c = new Contact(id, name, phone, email);

                contacts.add(c);
                bst.insert(c);
                phoneMap.put(phone, c);

                int number = Integer.parseInt(id.substring(1));

                if (number >= nextId)
                    nextId = number + 1;
            }

            br.close();

        } catch (FileNotFoundException e) {
            // First run: file does not exist yet.
        } catch (IOException | NumberFormatException e) {
            System.out.println("File loading error.");
        }
    }
}

// 4. Main Class
public class ContactManagerApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ContactManager cm = new ContactManager();

        int choice = 0;

        do {

            System.out.println("\n===== CONTACT MANAGER =====");
            System.out.println("1. Add Contact");
            System.out.println("2. Display Contacts");
            System.out.println("3. Search by Name");
            System.out.println("4. Search by Phone");
            System.out.println("5. Update Contact");
            System.out.println("6. Delete Contact");
            System.out.println("7. Sort Contacts");
            System.out.println("8. Exit");

            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid choice.");
                continue;
            }

            switch (choice) {

                case 1:
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Phone: ");
                    String phone = sc.nextLine().trim();

                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();

                    cm.addContact(name, phone, email);
                    break;

                case 2:
                    cm.displayContacts();
                    break;

                case 3:
                    System.out.print("Enter Name: ");
                    cm.searchByName(sc.nextLine());
                    break;

                case 4:
                    System.out.print("Enter Phone: ");
                    cm.searchByPhone(sc.nextLine().trim());
                    break;

                case 5:
                    System.out.print("Enter Contact ID: ");
                    String id = sc.nextLine();

                    System.out.print("Enter New Name: ");
                    String newName = sc.nextLine();

                    System.out.print("Enter New Phone: ");
                    String newPhone = sc.nextLine().trim();

                    System.out.print("Enter New Email: ");
                    String newEmail = sc.nextLine();

                    cm.updateContact(id, newName, newPhone, newEmail);
                    break;

                case 6:
                    System.out.print("Enter Contact ID: ");
                    cm.deleteContact(sc.nextLine());
                    break;

                case 7:
                    cm.sortContacts();
                    break;

                case 8:
                    cm.saveContacts();
                    System.out.println("Contacts saved successfully.");
                    System.out.println("Thank you!");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 8);

        sc.close();
    }
}
