import java.sql.DriverManager;
import java.sql.SQLException;
import java.beans.Statement;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.*;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://Localhost:3306/hotel_db";
    // jdbc:mysql://localhost:3306/college          //database link

    private static final String username = "root";

    private static final String password = "toor";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (

        ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("Hotel Management System");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println("Choose an Option : ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservations(connection, scanner);
                        break;
                    case 5:
                        deleteReservations(connection, scanner);
                        break;
                    case 0:
                        Exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice.Try again.");
                }
            }
        } catch (SQLException e2) {
            System.out.println(e2.getMessage());
        } catch (InterruptedException e1) {
            throw new RuntimeException(e1);
        }

    }

    private static void reserveRoom(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter guest name : ");
            String guestName = scanner.next();
            System.out.println("Enter room number : ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter contact number : ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number)" +
                    "VALUES ('" + questName + "'," + roomNumber + ",'" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation successfull!");
                } else {
                    System.out.println("Reservation failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewReservations(Connection connection) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM hotel_db";

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Current Reservations : ");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                // format and display the reservation Date in a time-like format
                System.out.printf("| %-14d | %-15d | %-13d | %-20d | %-19s |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }
            System.out.println(
                    "+------------------------------------------------------------------------------------------+");
        }
    }

    public static void getRoomNumber(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter reservation ID : ");
            int reservationId = scanner.nextInt();
            System.out.println("Enter guest name : ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations" +
                    "WHERE reservation_id = " + reservationId +
                    "AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID" + reservationId +
                            "and Guest " + guestName + " is : " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateReservations(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter reservation ID to update : ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();// consume the newline character

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            System.out.println("Enter new guest name : ");
            String newGuestName = scanner.nextLine();
            System.out.println("Enter nre room number : ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new contact number : ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "'," +
                    "room_number = " + newRoomNumber + "'," +
                    "contact_number = " + newContactNumber + "'," +
                    "WHERE reservation_id = " + reservationId;

            try (Staement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteReservations(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter reservation ID to delete : ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();// if there is a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;// Handle database errors as needed
        }
    }

    public static void Exit() throws InterruptedException {
        System.out.println("Exiting System");
        int i = 5;
        while (i != 0) {
            System.out.println("_");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thankyou for using Hotel Reservation System.");
    }
}