import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Task implements Serializable {
    private String description;
    private String category;

    public Task(String description, String category) {
        this.description = description;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return description + " (Category: " + category + ")";
    }
}

class LoginPage {
    private JFrame frame;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton forgotPasswordRegisterButton;

    public LoginPage() {
        frame = new JFrame("Login Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        usernameTextField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        forgotPasswordRegisterButton = new JButton("Forgot Password/Register");

        panel.add(new JLabel("Username:"));
        panel.add(usernameTextField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(forgotPasswordRegisterButton);

        forgotPasswordRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the login page
                new RegisterPage(); // Open the register page
            }
        });

        frame.add(panel);
        frame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String username = usernameTextField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
        } else if (checkCredentials(username, password)) {
            JOptionPane.showMessageDialog(frame, "Login successful!");
            frame.dispose(); // Close the login page

            // Open the Todo page
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new Todo();
                }
            });
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid username or password.");
        }
    }

    private boolean checkCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_credentials.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true; // Credentials match
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false; // Credentials not found or do not match
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginPage();
            }
        });
    }
}

class RegisterPage {
    private JFrame frame;
    private JTextField usernameTextField;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private JButton signUpButton;
    private JButton backButton;

    public RegisterPage() {
        frame = new JFrame("Register Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        usernameTextField = new JTextField();
        passwordField1 = new JPasswordField();
        passwordField2 = new JPasswordField();
        signUpButton = new JButton("Sign Up");
        backButton = new JButton("Back");

        panel.add(new JLabel("Username:"));
        panel.add(usernameTextField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField1);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(passwordField2);
        panel.add(signUpButton);
        panel.add(backButton);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the register page
                new LoginPage(); // Open the login page
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private void registerUser() {
        String username = usernameTextField.getText();
        String password1 = new String(passwordField1.getPassword());
        String password2 = new String(passwordField2.getPassword());

        if (username.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
        } else if (!password1.equals(password2)) {
            JOptionPane.showMessageDialog(frame, "Passwords do not match.");
        } else {
            // Store the new user credentials in a local file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("user_credentials.txt", true))) {
                writer.write(username + ":" + password1);
                writer.newLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            frame.dispose(); // Close the register page
            new LoginPage(); // Open the login page
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegisterPage();
            }
        });
    }
}

class Todo {
    private JTextField textField1;
    private JTextField categoryField; // Added category field
    private JButton addTaskButton;
    private JButton deleteTaskButton;
    private JButton deleteAllTaskButton;
    private JButton backButton;
    private JList<Task> list1;
    private DefaultListModel<Task> listModel;

    private static final String TODO_DATA_FILE = "todo_data.txt";

    public Todo() {
        // Initialize components
        JFrame frame = new JFrame("Todo List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new JPanel());
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        textField1 = new JTextField();
        categoryField = new JTextField(); // Added category field
        addTaskButton = new JButton("Add Task");
        deleteTaskButton = new JButton("Delete Task");
        deleteAllTaskButton = new JButton("Delete All Tasks");
        backButton = new JButton("Back");
        listModel = new DefaultListModel<>();
        list1 = new JList<>(listModel);

        // Load existing tasks from file
        loadTasks();

        // Add components to the frame
        frame.add(new JLabel("Task Description:"));
        frame.add(textField1);
        frame.add(new JLabel("Category:"));
        frame.add(categoryField);
        frame.add(addTaskButton);
        frame.add(deleteTaskButton);
        frame.add(deleteAllTaskButton);
        frame.add(backButton);
        frame.add(new JScrollPane(list1));

        // Add ActionListener for buttons
        addTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        deleteTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTask();
            }
        });

        deleteAllTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAllTasks();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the Todo page
                saveTasks(); // Save tasks to file
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new LoginPage(); // Open the login page
                    }
                });
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private void addTask() {
        String taskDescription = textField1.getText();
        String category = categoryField.getText();
        if (!taskDescription.isEmpty()) {
            Task task = new Task(taskDescription, category);
            listModel.addElement(task);
            textField1.setText("");
            categoryField.setText("");
        }
    }

    private void deleteTask() {
        int selectedIndex = list1.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
        }
    }

    private void deleteAllTasks() {
        listModel.removeAllElements();
    }

    private void loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TODO_DATA_FILE))) {
            List<Task> tasks = (List<Task>) ois.readObject();
            for (Task task : tasks) {
                listModel.addElement(task);
            }
        } catch (FileNotFoundException e) {
            // Ignore if the file is not found, as it might be the first run
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveTasks() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            tasks.add(listModel.get(i));
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TODO_DATA_FILE))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Todo();
            }
        });
    }
}
