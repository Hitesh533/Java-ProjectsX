import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.*;

class User {
    private Connection connection;
    private Scanner scanner;

    public User(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void Register() {
        scanner.nextInt();
        System.out.println("Full Name : ");
        String full_name = scanner.nextLine();
        System.out.println("Email : ");
        String email = scanner.nextLine();
        System.out.println("Password : ");
        String password = scanner.nextLine();
        if (user_exist(email)) {
            System.out.println("User already exist for this Email Address");
            return;
        }
        System.out.println("INSERT INTO User(full_name, email, password) VALUES(?, ?, ?)");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(register_query);
            preparedStatement.setString(1, full_name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Registration successfully");
            } else {
                System.out.println("Registration failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String login() {
        scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println("Password: ");
        String password = scanner.nextLine();
        String login_query = "SELECT * FROM User WHERE email = ? AND password = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(login_query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return email;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean user_exist(String email) {
        String query = "SELECT * FROM user WHERE email = ? ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

class Accounts {
    private Connection connection;
    private Scanner scanner;

    public Accounts(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public long open_account(String email) {
        if (!account_exist(email)) {
            String open_account_query = "INSERT INTO Accounts(account_number, full_name, email, balance, security_pin) VALUES(?,?,?,?,?)";
            scanner.nextLine();
            System.out.println("Enter Full Name: ");
            String full_name = scanner.nextLine();
            System.out.println("Enter Initial Amount: ");
            double balance = scanner.nextDouble();
            scanner.nextLine();
            System.out.println("Enter Security Pin : ");
            String security_pin = scanner.nextLine();
            try {
                long account_number = generateAccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(open_account_query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5, security_pin);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return account_number;
                } else {
                    throw new RuntimeException("Account Creation failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account Already Exist");
    }

    public long getAccount_number(String email) {
        String query = "SELECT account_number from Accounts WHERE email = ? ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("account_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Account Number Doesn't Exist.");
    }

    public long generateAccountNumber() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement
                    .executeQuery("SELECT account_number from Accounts ORDER BY account_number DISC LIMIT 1");
            if (resultSet.next()) {
                long last_account_number = resultSet.getLong("account_number");
                return last_account_number;
            } else {
                return 10000100;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 10000100;
    }

    public boolean account_exist(String email) {
        String query = "SELECT account_number from Accounts WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

class AccountManager {
    private Connection connection;
    private Scanner scanner;

    AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void credit_money(long account_number)throws SQLException{
        scanner.nextLine();
        System.out.println("Enter Amount: ");
        double amount = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            if (account_number != 0) {
                PreparedStatement preparedStatement = connection.prepareStatement(security_pin);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String credit_query = "UPDATE Accounts SET balance = balance +? WHERE account_number = ? and security_pin = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, account_number);
                    int rowsAffected = preparedStatement1.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Rs." + amount +" credited successfully");
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    }else{
                        System.out.println("Transaction Failed");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                }else{
                    System.out.println("Invalid Security Pin.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void debit_money(long account_number) throws SQLException {
        scanner.nextLine();
        System.out.println("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            if (account_number != 0) {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ?");
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (rowsAffected > 0) {
                    double create_balance = resultSet.getDouble("balance");
                    if (amount <= current_balance) {
                        String debit_query = "UPDATE Accounts SET balance = ? WHERE account_number = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, account_number);
                        int rowsAffected = preparedStatement1.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Rs." + amount + "debited Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    } else {
                        System.out.println("Insufficient Balance");
                    }
                } else {
                    System.out.println("Invalid Pin");
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void transfer_money(long sender_account_number) throws SQLException {
        scanner.nextLine();
        System.out.println("Enter Receiver Account Number: ");
        long receiver_account_number = scanner.nextLong();
        System.out.println("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter Security Pin: ");
        String security_pin = scanner.nextLine();

        try {
            connection.setAutoCommit(false);
            if (sender_account_number != 0 && receiver_account_number != 0) {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?");
                preparedStatement.setLong(1, sender_account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount <= current_balance) {
                        // write debit and credit queries
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                        String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";

                        // Debit and Credit prepared Statements
                        PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);

                        // set values for debit and credit prepared statements
                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, receiver_account_number);
                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(1, sender_account_number);
                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();
                        if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                            System.out.println("Transaction Successfull");
                            System.out.println("RS. " + amount + " Transfered Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed. ");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    } else {
                        System.out.println("Insufficient Balance.");
                    }
                } else {
                    System.out.println("Invalid Security Pin.");
                }
            } else {
                System.out.println("Invalid Account Number.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void getBalance(long account_number) {
        scanner.nextLine();
        System.out.println("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, security_pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                System.out.println("Balance: " + balance);
            } else {
                System.out.println("Invalid Pin.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

public class BankingSystem {
    private static final String url = "jdbc:mysql://Localhost:3306/banking_system";

    private static final String username = "root";

    private static final String password = "toor";

    public static void main(String[] args) throws Exception {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner scanner = new Scanner(System.in);
            User user = new User(connection, scanner);
            Accounts accounts = new Accounts(connection, scanner);
            AccountManager accountManager = new AccountManager(connection, scanner);

            String email;
            long account_number;

            while (true) {
                System.out.println("***WELCOME T0 BANKING SYSTEM***");
                System.out.println();
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.println("Enter your choice: ");
                int choice1 = scanner.nextInt();
                switch (choice1) {
                    case 1:
                        user.Register();
                        break;
                    case 2:
                        email = user.login();
                        if (email != null) {
                            System.out.println();
                            System.out.println("User Logged In.");
                            if (!accounts.account_exist(email)) {
                                System.out.println();
                                System.out.println("1. Open a new Bank Account");
                                System.out.println("2. Exit");
                                if (scanner.next() == 1) {
                                    account_number = accounts.open_account(email);
                                    System.out.println("Account Created Successfully");
                                    System.out.println("Your Account Number is: " + account_number);
                                } else {
                                    break;
                                }
                            }
                            account_number = accounts.getAccount_number(email);
                            int choice2 = 0;
                            while (choice2 != 5) {
                                System.out.println();
                                System.out.println("1. Debit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Balance");
                                System.out.println("5. Log Out");
                                System.out.println("Enter your choice: ");
                                choice2 = scanner.nextInt();
                                switch (choice2) {
                                    case 1:
                                        accountManager.debit_money(account_number);
                                        break;
                                    case 2:
                                        accountManager.credit_money(account_number);
                                        break;
                                    case 3:
                                        accountManager.transfer_money(account_number);
                                        break;
                                    case 4:
                                        accountManager.getBalance(account_number);
                                        break;
                                    case 5:
                                        break;
                                    default:
                                        System.out.println(" Enter Invalid Choice.");
                                        break;
                                }
                            }
                        } else {
                            System.out.println("Incorrect Email or Password");
                        }
                    case 3:
                        System.out.println("THANKYOU FOR USING BANKING SYSTEM");
                        System.out.println("Exiting System");
                        return;
                    default:
                        System.out.println("Enter valid choice");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
