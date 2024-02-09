import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
abstract class Employee {
    private String name;
    private int id;

    public Employee(String name, int id) {
        this.name = name;
        this.id = id;
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract double calculateSalary();

    @Override
    public String toString() {
        return "Employee{" +
                "name=" + name + "\'" +
                ", id=" + id + ", salary=" + calculateSalary() +
                "}";
    }
}

class FullTimeEmployee extends Employee {
    private double monthlySalary;

    public FullTimeEmployee(String name, int id, double monthlySalary) {
        super(name, id);
        this.monthlySalary = monthlySalary;
    }

    @Override
    public double calculateSalary() {
        return monthlySalary;
    }
}

class partTimeEmployee extends Employee {
    private int hrwork;
    private double earlyRate;

    public partTimeEmployee(String name, int id, int hrwork, double earlyRate) {
        super(name, id);
        this.earlyRate = earlyRate;
        this.hrwork = hrwork;
    }

    @Override
    public double calculateSalary() {
        return hrwork * earlyRate;
    }
}

class payRollSystem {
    private ArrayList<Employee> employeeList;

    public payRollSystem() {
        employeeList = new ArrayList<>();
    }

    public void addEmployee(Employee employee) {
        employeeList.add(employee);
    }

    public void removeEmployee(int id) {
        Employee employeeToRemove = null;
        for (Employee employee : employeeList) {
            if (employee.getId() == id) {
                employeeToRemove = employee;
                break;
            }
            if (employeeToRemove != null) {
                employeeList.remove(employeeToRemove);
            }
        }
    }

    public void displayEmployee() {
        for (Employee employee : employeeList) {
            System.out.println(employee);
        }
    }
}

public class Main {
    public static void main(String[] args) {

        payRollSystem pay = new payRollSystem();
        FullTimeEmployee emp1 = new FullTimeEmployee("vikas", 1, 30000);
        pay.addEmployee(emp1);
        partTimeEmployee emp2 = new partTimeEmployee("john", 2, 10, 100);
        System.out.println("All Employee");
        pay.addEmployee(emp2);
        pay.displayEmployee();
        System.out.println(emp1.calculateSalary());
        System.out.println(emp2.calculateSalary());
        System.out.println("Remmaning Employee");
        pay.removeEmployee(2);
        pay.displayEmployee();
    }
}