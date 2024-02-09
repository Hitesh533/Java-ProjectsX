import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Car {
    private String carId;
    private String brand;
    private String model;
    private double basePricePerDay;
    private boolean isAvailable;

    Car(String carId, String brand, String model, double basePricePerDay, boolean isAvailable) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.basePricePerDay = basePricePerDay;
        this.isAvailable = true;
    }

    public String getCarId() {
        return carId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double calculatePrice(double rentalDays) {
        return basePricePerDay * rentalDays;
    }

    public double getBasePricePerDay() {
        return basePricePerDay;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void rent() {
        isAvailable = false;
    }

    public void returnCar() {
        isAvailable = true;
    }

    @Override
    public String toString() {
        return "Car [carId=" + carId + ", brand=" + brand + ", model=" + model + ", basePricePerDay=" + basePricePerDay
                + ", isAvailable=" + true + "]";
    }

}

class Customer {
    private String customerId;
    private String name;

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public String getcustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

}

class Rental {
    private Car car;
    private Customer customer;
    private double days;

    public Rental(Car car, Customer customer, double rentalDays) {
        this.car = car;
        this.customer = customer;
        this.days = rentalDays;
    }

    public Car getCar() {
        return car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Double getdays() {
        return days;
    }
}

class CarRentalSystem {
    private List<Car> cars;
    private List<Customer> customers;
    private List<Rental> rentals;

    public CarRentalSystem() {
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void rentalCar(Car car, Customer customer, double rentalDays) {
        if (car.isAvailable()) {
            car.rent();
            rentals.add(new Rental(car, customer, rentalDays));
        } else {
            System.out.println("Car is not available for rent");
        }
    }

    public void returnCar(Car car) {
        car.returnCar();
        Rental rentalToRemove = null;
        for (Rental rental : rentals) {
            if (rental.getCar() == car) {
                rentalToRemove = rental;
                break;
            }
        }
        if (rentalToRemove != null) {
            rentals.remove(rentalToRemove);
            System.out.println("Car returned successfully");
        } else {
            System.out.println("Car was not returned");
        }
    }

    public void menu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("=====Car Rental System=====");
            System.out.println("1. Rent a Car");
            System.out.println("2. Return a car");
            System.out.println("3. Exit");
            System.out.println("Enter your choice");

            int choice = scanner.nextInt();
            scanner.nextLine();// continue new line

            if (choice == 1) {
                System.out.println("\n===== Rent a Car =====\n");
                System.out.println("Enter your name");
                String customerName = scanner.nextLine();

                System.out.println("\nAvailable Cars: ");
                for (Car car : cars) {
                    if (car.isAvailable()) {
                        System.out.println(car.getCarId() + " - " + car.getBrand() + " - " + car.getModel());
                    }
                }

                System.out.println("Enter the Car Id you want to rent: ");
                String carId = scanner.nextLine();

                System.out.println("Enter the number of days for rental: ");
                double rentalDays = scanner.nextInt();
                scanner.nextLine();// consue new line

                Customer newCustomer = new Customer("CUS" + (customers.size() + 1), customerName);
                addCustomer(newCustomer);

                Car selectedCar = null;
                for (Car car : cars) {
                    if (car.getCarId().equals(carId) && car.isAvailable()) {
                        selectedCar = car;
                        break;
                    }
                }
                if (selectedCar != null) {
                    double totalPrice = selectedCar.calculatePrice(rentalDays);
                    System.out.println("\n==Rental Information==\n");
                    System.out.println("Customer Id : " + newCustomer.getcustomerId());
                    System.out.println("Customer name : " + newCustomer.getName());
                    System.out.println("Car : " + selectedCar.getBrand() + " - " + selectedCar.getModel());
                    System.out.println("Rental Days : " + rentalDays);
                    System.out.printf("Total Price : ", totalPrice);

                    System.out.println("\nConfirm rental(Y/N) : ");
                    String confirm = scanner.nextLine();

                    if (confirm.equalsIgnoreCase("Y")) {
                        rentalCar(selectedCar, newCustomer, rentalDays);
                        System.out.println("\n Car rented successfully");
                    } else {
                        System.out.println("\nRental canceled");
                    }
                } else {
                    System.out.println("\n Invalid car selection or car not available for rent");
                }
            } else if (choice == 2) {
                System.out.println("\n=====Rent a Car=====\n");
                System.out.println("Enter the car Id you want to return");
                String carId = scanner.nextLine();

                Car carToReturn = null;
                for (Car car : cars) {
                    if (car.getCarId().equals(carId) && !car.isAvailable()) {
                        carToReturn = car;
                        break;
                    }
                }
                if (carToReturn != null) {
                    Customer customer = null;
                    for (Rental rental : rentals) {
                        if (rental.getCar() == carToReturn) {
                            customer = rental.getCustomer();
                            break;
                        }
                    }
                    if (customer != null) {
                        returnCar(carToReturn);
                        System.out.println("Car returned successfully by " + customer.getName());
                    } else {
                        System.out.println("Car was not rented or rental information is missing.");
                    }
                } else {
                    System.out.println("Invalid car ID or car was not rented.");
                }

            } else if (choice == 3) {
                break;
            } else {
                System.out.println("\n Invalid choice please enter a valid option.");
            }
        }

        System.out.println("\n Thank you for using the Car Rental System.");
    }

}

public class HotelReservationSystem {
    public static void main(String[] args) {
        CarRentalSystem CRS = new CarRentalSystem();

        Car car1 = new Car("C001", "Audi", "Diamond", 60.0, false);
        Car car2 = new Car("C002", "BMW", "Gold", 80.0, true);
        Car car3 = new Car("C003", "Mercedes", "Silver", 90.0, true);
        Car car4 = new Car("C004", "TATA", "Platinum", 100.0, true);

        CRS.addCar(car1);
        CRS.addCar(car2);
        CRS.addCar(car3);
        CRS.addCar(car4);

        CRS.menu();
    }
}
