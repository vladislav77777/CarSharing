package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static int numMenu, numManagLogIn;
    static int ID;
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:./src/carsharing/db/carsharing";
    static final String MENU = """
            1. Log in as a manager
            2. Log in as a customer
            3. Create a customer
            0. Exit""";
    static final String LOG_IN = "1. Company list\n" +
            "2. Create a company\n" +
            "0. Back";
    static final String CAR_LIST = "1. Car list\n" +
            "2. Create a car\n" +
            "0. Back";
    static final String RENT_LIST = "1. Rent a car\n" +
            "2. Return a rented car\n" +
            "3. My rented car\n" +
            "0. Back";
    static final String COMPANY_EMPTY = "The company list is empty!";
    static final String CAR_LIST_EMPTY = "The car list is empty!";
    static final String CUSTOMER_LIST_EMPTY = "The customer list is empty!";


    static public void dropCompanyTable(Statement stmt) {
        try {
            String sql = "DROP TABLE IF EXISTS COMPANY";
            stmt.executeUpdate(sql);
        } catch (SQLException se) {
            se.printStackTrace();
            throw new RuntimeException(se);
        }
    }

    static public void dropCustomerTable(Statement stmt) {
        try {
            String sql = "ALTER TABLE CAR " +
                    "DROP CONSTRAINT fk_idCar;";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS CUSTOMER";
            stmt.executeUpdate(sql);
        } catch (SQLException se) {
            se.printStackTrace();
            throw new RuntimeException(se);
        }
    }

    static public void dropCarTable(Statement stmt) {
        try {
            String sql = "ALTER TABLE CAR " +
                    "DROP CONSTRAINT fk_idCompany;";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS CAR";
            stmt.executeUpdate(sql);
        } catch (SQLException se) {
            se.printStackTrace();
            throw new RuntimeException(se);
        }
    }


    public static void main(String[] args) {

        Connection conn = null;
        Statement stmt = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 2: Open a connection
            //System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(true);
            //STEP 3: Execute a query
            // System.out.println("Creating table in given database...");
            stmt = conn.createStatement();
//            dropCustomerTable(stmt);
//            dropCarTable(stmt);
//            dropCompanyTable(stmt);
            String sql = "CREATE TABLE IF NOT EXISTS COMPANY( " +
                    "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                    " NAME VARCHAR(255) UNIQUE NOT NULL " +
                    ");";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS CAR( " +             // Creating tables
                    "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                    " NAME VARCHAR(255) UNIQUE NOT NULL, " +
                    "COMPANY_ID INT NOT NULL, " +
                    "CONSTRAINT fk_idCompany FOREIGN KEY (COMPANY_ID) " +
                    "REFERENCES COMPANY(ID)" +
                    ");";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS CUSTOMER( " +
                    "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                    " NAME VARCHAR(255) UNIQUE NOT NULL, " +
                    "RENTED_CAR_ID INT, " +
                    "CONSTRAINT fk_idCar FOREIGN KEY (RENTED_CAR_ID) " +
                    "REFERENCES CAR(ID)" +
                    ");";
            stmt.executeUpdate(sql);
            //System.out.println("Created table in given database...");
            do {
                System.out.println(MENU);
                numMenu = scanner.nextInt(); // first Main menu
                if (numMenu == 1) {
                    do {
                        System.out.println(LOG_IN);
                        numManagLogIn = scanner.nextInt(); // choose one of the options
                        switch (numManagLogIn) {
                            case 1:
                                ResultSet resultSet = stmt.executeQuery("SELECT" +
                                        " ID," +
                                        " NAME" +
                                        " FROM COMPANY" +
                                        " ORDER BY" +
                                        " ID;");
                                boolean bool = resultSet.next();
                                if (!bool) {
                                    System.out.println(COMPANY_EMPTY);
                                } else {
                                    System.out.println("Choose the company:");
                                    while (bool || resultSet.next()) {
                                        System.out.print(resultSet.getInt("ID") + ". ");
                                        System.out.print(resultSet.getString("NAME"));
                                        System.out.println();
                                        bool = false;
                                    }
                                    System.out.println("0. Back");
                                    int comp = scanner.nextInt(); // number of company
                                    if (comp == 0) {
                                        break;
                                    }
                                    System.out.println();

                                    String company = getCompany(stmt, comp);
                                    System.out.println("'" + company + "' company");
                                    int numCarList;
                                    do {
                                        System.out.println(CAR_LIST);
                                        System.out.println();
                                        numCarList = scanner.nextInt(); // choosing number in Car list
                                        switch (numCarList) {
                                            case 1:
                                                ResultSet resultSet2 = stmt.executeQuery("SELECT" +
                                                        " ID, " +
                                                        " NAME" +
                                                        " FROM CAR" +
                                                        " WHERE COMPANY_ID=" + comp +
                                                        ";");
                                                boolean bool2 = resultSet2.next();
                                                if (!bool2) {
                                                    System.out.println(CAR_LIST_EMPTY);
                                                    System.out.println();
                                                } else {
                                                    System.out.println();
                                                    int id = 1;
                                                    System.out.println("Car list:"); // display our list of cars
                                                    while (bool2 || resultSet2.next()) {
                                                        System.out.print(id + ". ");
                                                        System.out.print(resultSet2.getString("NAME"));
                                                        System.out.println();
                                                        bool2 = false;
                                                        id++;
                                                    }
                                                    System.out.println();
                                                }
                                                break;
                                            case 2:         // add car
                                                System.out.println("Enter the car name:");
                                                String car = scanner.nextLine();
                                                car = scanner.nextLine();
                                                car = "'" + car + "'";
                                                // System.out.println(car + " CAR\n");
                                                ID = getCarID(stmt) + 1; // the next empty position
                                                stmt.executeUpdate("INSERT INTO CAR VALUES (" +
                                                        ID + ", " + car + ", " + comp + ");");
                                                System.out.println("The car was added!");
                                                System.out.println();
                                                break;
                                            case 0:
                                                break;
                                            default:
                                                break;

                                        }
                                    } while (numCarList != 0);
                                }
                                break;
                            case 2:   // add company
                                System.out.println("Enter the company name:");
                                String company = scanner.nextLine();
                                company = scanner.nextLine();
                                company = "'" + company + "'";
                                // System.out.println(company + " COMPANY\n");
                                ID = getCompanyID(stmt) + 1;
                                stmt.executeUpdate("INSERT INTO COMPANY VALUES (" +
                                        ID + ", " + company + ");");
                                System.out.println("The company was created!");
                                break;
                            case 0:
                                break;
                            default:
                                break;
                        }
                    } while (numManagLogIn != 0);
                } else if (numMenu == 2) {  // log in as costumer
                    ResultSet resultSet3 = stmt.executeQuery("SELECT" +
                            " ID, " +
                            " NAME" +
                            " FROM CUSTOMER" +
                            " ORDER BY" +
                            " ID;");
                    boolean bool3 = resultSet3.next();
                    if (!bool3) {
                        System.out.println(CUSTOMER_LIST_EMPTY);
                        System.out.println();
                    } else {
                        System.out.println("The customer list:");
                        int id = 1;

                        while (bool3 || resultSet3.next()) {
                            System.out.print(resultSet3.getInt("ID") + ". ");
                            System.out.print(resultSet3.getString("NAME"));
                            System.out.println();
                            bool3 = false;
                        }
                        System.out.println("0. Back");
                        int cust = scanner.nextInt(); // number of customer
                        if (cust == 0) {
                            break;
                        }
                        ResultSet rss = stmt.executeQuery("SELECT NAME FROM CUSTOMER WHERE ID=" + cust + ";");
                        String customer = "ERROR";
                        if (rss.next()) {
                            customer = rss.getString("NAME");
                        }
                        System.out.println();
                        int rentInp;
                        do {
                            System.out.println(RENT_LIST);
                            rentInp = scanner.nextInt();  // Choosing number in Rent list
                            switch (rentInp) {
                                case 1:
                                    try {
                                        ResultSet rss2 = stmt.executeQuery("SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID=" + cust + ";");
                                        String rentCarID = "";
                                        if (rss2.next()) {
                                            System.out.println();

                                            rentCarID = rss2.getString("RENTED_CAR_ID");
                                        }
                                        if (rentCarID != null) {
                                            System.out.println("You've already rented a car!");
                                            break;
                                        }

                                        ResultSet resultSet = stmt.executeQuery("SELECT" +
                                                " ID," +
                                                " NAME" +
                                                " FROM COMPANY" +
                                                " ORDER BY" +
                                                " ID;");
                                        boolean bool = resultSet.next();
                                        if (!bool) {
                                            System.out.println(COMPANY_EMPTY);
                                        } else {
                                            System.out.println("Choose a company:");
                                            while (bool || resultSet.next()) {
                                                System.out.print(resultSet.getInt("ID") + ". ");
                                                System.out.print(resultSet.getString("NAME"));
                                                System.out.println();
                                                bool = false;
                                            }
                                            System.out.println("0. Back");
                                            int comp = scanner.nextInt(); // number of company
                                            if (comp == 0) {
                                                break;
                                            }
                                            int[] idS = new int[30];
                                            for (var elem : idS) {
                                                elem = 0;
                                            }
                                            String[] cars = new String[30];
                                            boolean fl = false; // to do some actions only once
                                            List<Integer> rentedCars = new ArrayList<Integer>();
                                            for (var e : rentedCars) {
                                                e = 0;
                                            }
                                            ResultSet resultSet22 = stmt.executeQuery("SELECT" +
                                                    " RENTED_CAR_ID " +
                                                    " FROM CUSTOMER" +
                                                    ";");
                                            while (resultSet22.next()) {
                                                rentedCars.add(resultSet22.getInt("RENTED_CAR_ID"));
                                            }

                                            ResultSet resultSet2 = stmt.executeQuery("SELECT" +
                                                    " ID, " +
                                                    " NAME" +
                                                    " FROM CAR" +
                                                    " WHERE COMPANY_ID=" + comp +
                                                    ";");
                                            boolean bool2 = resultSet2.next();
                                            if (!bool2) {
                                                System.out.println(CAR_LIST_EMPTY);
                                                System.out.println();
                                            } else {
                                                System.out.println();
                                                id = 1;

                                                while (bool2 || resultSet2.next()) {
                                                    boolean isRented = false;
                                                    int ID = resultSet2.getInt("ID");
                                                    for (var elem : rentedCars) {
                                                        if (elem == ID) {
                                                            isRented = true;
                                                            break;
                                                        }
                                                    }
//                                                    System.out.println("ISRENTED  " + isRented +" booL = " + bo);
                                                    if (!isRented) {
                                                        if (!fl) {
                                                            System.out.println("Choose a car:");
                                                            fl = true;
                                                        }
                                                        idS[id] = ID;
                                                        cars[id] = resultSet2.getString("NAME");
                                                        System.out.print(id + ". ");
                                                        System.out.println(cars[id]);
                                                        id++;
                                                    }
                                                    bool2 = false;
                                                }
                                            }
                                            int car = 0;
                                            if (fl) {
                                                System.out.println("0. Back");
                                                car = scanner.nextInt();
                                                if (car == 0) { // back
                                                    break;
                                                }
                                            }
//                                            System.out.println("FL:  " + fl);
                                            if (!fl) {
                                                System.out.println(CAR_LIST_EMPTY);
                                                System.out.println();
                                            } else {
                                                stmt.executeUpdate("UPDATE CUSTOMER SET RENTED_CAR_ID =" + idS[car] + " WHERE ID=" + cust + ";"); // !!!!!!!!!!!!!!!!!!
                                                System.out.println("You rented " + "'" + cars[car] + "'");
                                            }
                                            System.out.println();
                                        }
                                    } catch (SQLException ex) { // handling exceptions
                                        ex.printStackTrace();
                                    }
                                    break;
                                case 2:
                                    ResultSet rss2 = stmt.executeQuery("SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID=" + cust + ";");
                                    if (!rss2.next()) {
                                        System.out.println();
                                    }
                                    String rentCarID = rss2.getString("RENTED_CAR_ID");
                                    if (rentCarID == null) {
                                        System.out.println("You didn't rent a car!");
                                        break;
                                    }
                                    stmt.executeUpdate("UPDATE CUSTOMER SET RENTED_CAR_ID = NULL" + " WHERE ID=" + cust + ";");
                                    System.out.println("You've returned a rented car!");

                                    break;
                                case 3: // we display information about the rented car (or its absence)
                                    ResultSet rss3 = stmt.executeQuery("SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID=" + cust + ";");
                                    String rentCarID3 = null;
                                    if (rss3.next()) {
                                        System.out.println();
                                        rentCarID3 = rss3.getString("RENTED_CAR_ID");
                                    }
                                    if (rentCarID3 == null) {
                                        System.out.println("You didn't rent a car!");
                                        break;
                                    }
                                    rss3 = stmt.executeQuery("SELECT NAME, COMPANY_ID FROM CAR WHERE ID=" + rentCarID3 + ";");
                                    int compID = 0;
                                    String carName = "";
                                    if (rss3.next()) {
                                        System.out.println();
                                        carName = rss3.getString("NAME");
                                        compID = rss3.getInt("COMPANY_ID");
                                    }
                                    rss3 = stmt.executeQuery("SELECT NAME FROM COMPANY WHERE ID=" + compID + ";");
                                    String compName = "";
                                    if (rss3.next()) {
                                        System.out.println();
                                        compName = rss3.getString("NAME");
                                    }
                                    System.out.println("Your rented car:");
                                    System.out.println(carName);
                                    System.out.println("Company:");
                                    System.out.println(compName);
                                    System.out.println();
                                    break;
                            }
                        } while (rentInp != 0); // until you clicked "Back"

                    }
                } else if (numMenu == 3) { // create a customer
                    System.out.println("Enter the customer name:");
                    String name = scanner.nextLine();
                    name = "'" + scanner.nextLine() + "'";
                    int id = getCustID(stmt) + 1;
                    stmt.executeUpdate("INSERT INTO CUSTOMER VALUES (" +
                            id + ", " + name + ", NULL" + ");");
                    System.out.println("The customer was added!");
                }

            } while (numMenu != 0); // until you click "Exit"
            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } //end finally try
        } //end try

        //System.out.println("Goodbye!");
    }


    static int getCompanyID(Statement stmt) {  // return the last empty company ID
        try {
            ResultSet resultSet = stmt.executeQuery("SELECT" +
                    " ID" +
                    " FROM COMPANY" +
                    " ORDER BY" +
                    " ID DESC;");
            System.out.println();
            if (resultSet.next()) {
                return resultSet.getInt("ID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    static int getCustID(Statement stmt) { // return the last empty Customer ID
        try {
            ResultSet resultSet = stmt.executeQuery("SELECT" +
                    " ID" +
                    " FROM CUSTOMER" +
                    " ORDER BY" +
                    " ID DESC;");
            System.out.println();
            if (resultSet.next()) {
                return resultSet.getInt("ID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }


    static int getCarID(Statement stmt) { // return the last empty Car ID
        try {
            ResultSet resultSet = stmt.executeQuery("SELECT" +
                    " ID" +
                    " FROM CAR" +
                    " ORDER BY" +
                    " ID DESC;");
            System.out.println();
            if (resultSet.next()) {
                return resultSet.getInt("ID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    static String getCompany(Statement stmt, int id) { // return the name of company, if we know its ID

        try {
            String str = "SELECT" +
                    " ID, NAME" +
                    " FROM COMPANY" +
                    " WHERE ID = " + id +
                    ";";
            ResultSet resultSet = stmt.executeQuery(str);

            while (resultSet.next()) {
                return resultSet.getString("NAME");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "ERR";
    }

}