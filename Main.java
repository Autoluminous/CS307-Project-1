package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    private static String url = "jdbc:postgresql://localhost:5432/postgres";
    private static String user = "postgres";
    private static String password = "Qwe/147147";
    private static Connection con = null;
    private static PreparedStatement to_orders = null;
    private static PreparedStatement to_berth = null;
    private static PreparedStatement to_company = null;
    private static PreparedStatement to_company_ship_detail = null;
    private static PreparedStatement to_company_courier_detail = null;
    private static PreparedStatement to_city = null;
    private static PreparedStatement to_ship = null;
    private static PreparedStatement to_courier = null;
    private static PreparedStatement to_container = null;
    private static PreparedStatement to_item = null;
    private static boolean verbose = false;
    private static String fileName;

    private static void load_company(String company_name) throws SQLException {
        if (to_company != null) {
            to_company.setString(1, company_name);
            to_company.executeUpdate();
        }
    }

    private static void load_ship(String ship_name) throws SQLException {
        if (to_ship != null) {
            to_ship.setString(1, ship_name);
            to_ship.executeUpdate();
        }
    }

    private static void load_city(String city_name) throws SQLException {
        if (to_city != null) {
            to_city.setString(1, city_name);
            to_city.executeUpdate();
        }
    }

    private static void load_courier(String courier_name, String gender, String phone_number, Integer age) throws SQLException {
        if (to_courier != null) {
            to_courier.setString(1, courier_name);
            to_courier.setString(2, gender);
            to_courier.setString(3, phone_number);
            if(age!=null)
            {
                to_courier.setInt(4, age);
            }
            to_courier.executeUpdate();
        }
    }

    private static void load_container(String container_code, String container_type) throws SQLException {
        if (to_container != null) {
            to_container.setString(1, container_code);
            to_container.setString(2, container_type);
            to_container.executeUpdate();
        }
    }

    private static void load_item(String item_name, String item_type, Integer item_price, double import_tax, double export_tax) throws SQLException {
        if (to_item != null) {
            to_item.setString(1, item_name);
            to_item.setString(2, item_type);
            if(!item_price.equals(null))
            {
                to_item.setInt(3, item_price);
            }
            to_item.setDouble(4, import_tax);
            to_item.setDouble(5, export_tax);
            to_item.executeUpdate();
        }
    }

    private static void load_company_ship_detail(String company_name, String ship_name) throws SQLException {
        if (to_company_ship_detail != null) {
            to_company_ship_detail.setString(1, company_name);
            to_company_ship_detail.setString(2, ship_name);
            to_company_ship_detail.executeUpdate();
        }
    }

    private static void load_company_courier_detail(String company_name, String courier_name) throws SQLException {
        if (to_company_courier_detail != null) {
            to_company_courier_detail.setString(1, company_name);
            to_company_courier_detail.setString(2, courier_name);
            to_company_courier_detail.executeUpdate();
        }
    }

    private static void load_berth(String ship_name, String city_name) throws SQLException {
        if (to_berth != null) {
            to_berth.setString(1, ship_name);
            to_berth.setString(2, city_name);
            to_berth.executeUpdate();
        }
    }

    private static void load_orders(Timestamp log_time, Date import_time, Date export_time, Date retrieval_start_time, Date delivery_finish_time,
                                    String item_name, String container_code, String ship_name, String retrieval_courier, String delivery_courier,
                                    String retrieval_city, String delivery_city, String import_city, String export_city) throws SQLException {
        if (to_orders != null) {
            to_orders.setTimestamp(1, log_time);
            to_orders.setDate(2, import_time);
            to_orders.setDate(3, export_time);
            to_orders.setDate(4, retrieval_start_time);
            to_orders.setDate(5, delivery_finish_time);
            to_orders.setString(6, item_name);
            to_orders.setString(7, container_code);
            to_orders.setString(8, ship_name);
            to_orders.setString(9, retrieval_courier);
            to_orders.setString(10, delivery_courier);
            to_orders.setString(11, retrieval_city);
            to_orders.setString(12, delivery_city);
            to_orders.setString(13, import_city);
            to_orders.setString(14, export_city);
            to_orders.executeUpdate();
        }
    }

    private static void openDB() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            System.err.println("Cannot find the Postgres driver. Check CLASSPATH.");
            System.exit(1);
        }
        try {
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        try {
            to_company = con.prepareStatement("insert into company (company_name) values(?)");
            to_ship = con.prepareStatement("insert into ship (ship_name) values(?)");
            to_city = con.prepareStatement("insert into city (city_name) values(?)");
            to_courier = con.prepareStatement("insert into courier (courier_name, gender, phone_number,age) values(?,?,?,?)");
            to_container = con.prepareStatement("insert into container (container_code, container_type) values(?,?)");
            to_item = con.prepareStatement("insert into item (item_name, item_type, item_price, import_tax, export_tax) values(?,?,?,?,?)");
            to_company_ship_detail = con.prepareStatement("insert into company_ship_detail (company_name,ship_name) values(?,?)");
            to_company_courier_detail = con.prepareStatement("insert into company_courier_detail (company_name,courier_name) values(?,?)");
            to_berth = con.prepareStatement("insert into berth (ship_name,city_name) values(?,?)");
            to_orders = con.prepareStatement(
                    "insert into orders (" +
                            "logtime,import_time,export_time,retrieval_start_time,delivery_finish_time" +
                            "item_name,container_code,ship_name,retrieval_courier,delivery_courier" +
                            "retrieval_city,delivery_city,import_city,export_city) " +
                            "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
            );
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDB();
            System.exit(1);
        }
    }

    private static void closeDB() {
        if (con != null) {
            try {
                if (to_orders != null) {
                    to_orders.close();
                }
                if (to_berth != null) {
                    to_berth.close();
                }
                if (to_company_courier_detail != null) {
                    to_company_courier_detail.close();
                }
                if (to_company_ship_detail != null) {
                    to_company_ship_detail.close();
                }
                if (to_company != null) {
                    to_company.close();
                }
                if (to_city != null) {
                    to_city.close();
                }
                if (to_ship != null) {
                    to_ship.close();
                }
                if (to_courier != null) {
                    to_courier.close();
                }
                if (to_container != null) {
                    to_container.close();
                }
                if (to_item != null) {
                    to_item.close();
                }
                con.close();
                con = null;
            } catch (Exception e) {
            }
        }
    }

    public static void imp() {
        try (BufferedReader infile = new BufferedReader(new FileReader(fileName))) {
            long start, end;//记录开始和结束的时间；
            int cnt = 0;
            String line;//一次读取一行；
            String[] parts;//将一行分割成很多部分；
            //csv文件的列为以下
            String item_name, item_type;
            String retrieval_city, retrieval_courier, retrieval_courier_gender, retrieval_courier_phone_number;
            String delivery_city, delivery_courier, delivery_courier_gender, delivery_courier_phone_number;
            String item_export_city, item_import_city, container_code, container_type, ship_name, company_name;
            Integer item_price, retrieval_courier_age, delivery_courier_age;
            double item_export_tax, item_import_tax;
            Date retrieval_start_time, delivery_finish_time, item_export_time, item_import_time;
            Timestamp log_time;
            openDB();
            Statement clear;
            if (con != null) {
                clear = con.createStatement();
                clear.execute("truncate table company,ship,courier,city,item,container,company_ship_detail,company_courier_detail,berth,orders");
                clear.close();
            }
            start = System.currentTimeMillis();
            while ((line = infile.readLine()) != null) {
                cnt++;
                if (cnt >= 2)
                {
                    System.out.println(cnt);
                    parts = line.split(",");
                    item_name = parts[0];
                    item_type = parts[1];
                    item_price = Integer.parseInt(parts[2]);
                    retrieval_city = parts[3];
                    retrieval_start_time = Date.valueOf(parts[4]);

                    retrieval_courier = parts[5];
                    retrieval_courier_gender = parts[6];
                    retrieval_courier_phone_number = parts[7];
                    retrieval_courier_age = Integer.parseInt(parts[8]);
                    if(!parts[9].equals(""))
                    {
                        delivery_finish_time = Date.valueOf(parts[9]);
                    }
                    else
                    {
                        delivery_finish_time = null;
                    }
                    delivery_city = parts[10];
                    if(!parts[11].equals(""))
                    {
                        delivery_courier = parts[11];
                        delivery_courier_gender = parts[12];
                        delivery_courier_phone_number = parts[13];
                        delivery_courier_age = Integer.parseInt(parts[14]);
                    }
                    else
                    {
                        delivery_courier=null;
                        delivery_courier_gender=null;
                        delivery_courier_phone_number=null;
                        delivery_courier_age=null;
                    }
                    item_export_city = parts[15];
                    item_export_tax = Double.parseDouble(parts[16]);
                    if(!parts[17].equals(""))
                    {
                        item_export_time = Date.valueOf(parts[17]);
                    }
                    else
                    {
                        item_export_time=null;
                    }
                    item_import_city = parts[18];
                    item_import_tax = Double.parseDouble(parts[19]);
                    if(!parts[20].equals(""))
                    {
                        item_import_time = Date.valueOf(parts[20]);
                    }
                    else
                    {
                        item_import_time=null;
                    }
                    if(!parts[17].equals(null))
                    {
                        container_code = parts[21];
                        container_type = parts[22];
                        ship_name = parts[23];
                    }
                    else
                    {
                        container_code=null;
                        container_type=null;
                        ship_name=null;
                    }
                    company_name = parts[24];
                    log_time = Timestamp.valueOf(parts[25]);
                    try {
                        load_company(company_name);
                    } catch (SQLException sqlException) {}
                    try {
                        load_ship(ship_name);
                    } catch (SQLException sqlException) {}
                    try {
                        load_courier(delivery_courier,delivery_courier_gender,delivery_courier_phone_number,delivery_courier_age);
                    } catch (SQLException sqlException) {}
                    try {
                        load_courier(retrieval_courier,retrieval_courier_gender,retrieval_courier_phone_number,retrieval_courier_age);
                    } catch (SQLException sqlException) {}
                    try {
                        load_city(delivery_city);
                    } catch (SQLException sqlException) {}
                    try {
                        load_city(retrieval_city);
                    } catch (SQLException sqlException) {}
                    try {
                        load_city(item_import_city);
                    } catch (SQLException sqlException) {}
                    try {
                        load_city(item_export_city);
                    } catch (SQLException sqlException) {}
                    try {
                        load_container(container_code,container_type);
                    } catch (SQLException sqlException) {}
                    try {
                        load_item(item_name,item_type,item_price,item_import_tax,item_export_tax);
                    } catch (SQLException sqlException) {}
                    try {
                        load_company_ship_detail(company_name,ship_name);
                    } catch (SQLException sqlException) {}
                    try {
                        load_company_courier_detail(company_name,delivery_courier);
                    } catch (SQLException sqlException) {}
                    try {
                        load_company_courier_detail(company_name,retrieval_courier);
                    } catch (SQLException sqlException) {}
                    try {
                        load_berth(ship_name,delivery_city);
                    } catch (SQLException sqlException) {}
                    try {
                        load_berth(ship_name,retrieval_city);
                    } catch (SQLException sqlException) {}
                    try {
                        load_berth(ship_name,item_export_city);
                    } catch (SQLException sqlException) {}
                    try {
                        load_berth(ship_name,item_import_city);
                    } catch (SQLException sqlException) {}
                    try {
                        load_orders(log_time,item_import_time,item_export_time, retrieval_start_time,delivery_finish_time,
                                item_name,container_code,ship_name,retrieval_courier,delivery_courier,
                                retrieval_city,delivery_city,item_import_city,item_export_city);
                    } catch (SQLException sqlException) {}
                }
            }
            con.commit();
            closeDB();
            end = System.currentTimeMillis();
            System.out.println("importing use actual time " + (end - start) + " ms");
            System.out.println(cnt - 1 + " records successfully loaded");
            System.out.println("Loading speed : " + (cnt * 1000) / (end - start) + " records/s");
        } catch (SQLException se) {
            System.err.println("SQL error: " + se.getMessage());
            closeDB();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Fatal error: " + e.getMessage());
            closeDB();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        fileName="data2.csv";
        imp();
    }
}