# CS307 Fall 2022 Project 1

Contributors

钟乐        12110619 Lab1  Tasks: Task3, Task4, advanced Task4        Contribution Ratio: 50%

吴潇阳    12110917 Lab1  Tasks: Task1, Task2 , advanced Task3       Contribution Ratio: 50%

## Task 1: E-R Diagram

Website www.iodraw.com is used for E-R diagram drawing.

![ER图](C:\Users\86386\Downloads\ER图.png)

## Task 2: Database Design

### **Database Diagram**

![public](C:\Users\86386\Pictures\public.svg)

### **Description** for Each Table

**orders**

Table "orders" is the main table of our database, from where we can access to all the data. The "order_id" column is self increment and acts as the prime key of the table. Apart from "order_id", "orders" table is composed of timestamps and the unique identifier of courier, city, item and other basic elements. It is easy for us to debug and avoid frequent query across different table. 

The information of goods is related to this table by the foreign key "item_name", which is a unique key in the table "item". 

Similarly, table "orders" possesses several other foreign keys like "ship_name", "retrieval_courier", etc, all of them work generally the same as the item key.

Also, this foreign key design can grant the tables related to "orders" a new feature. They can serve as a registry then, which means every order with new element need and should register the element to the related tables first or the order will not be permitted to be added in.

**item**

The "item" table contains the name, type, price, tax of items and uses name as the unique identifier for query.

**container**

Table "container" contains every container's unique code and its container type.

**courier**

Table "courier" contains basic information of different courier, including their name, phone number, gender and age.           

**company**

Table "company" contains every company's unique name.

**ship**

Table "ship" contains every ship's unique name.

**berth**

Table "berth" shows the many to many relationship between ships and cities. 

**detail**

There are two detail tables in our database. The "company_ship_detail" table allows us to find the company's ownership for ships. The "company_courier_detail" table allows us to find out for which company does a certain courier works for.

## Task 3: Data Import

### Basic Requirements

#### **Test Environment**

Windows Laptop:

| Operating System | Hardware                             | Software                                                     | Internet  | Data                |
| ---------------- | ------------------------------------ | ------------------------------------------------------------ | --------- | ------------------- |
| Windows 11 22H2  | -i7 12800HX<br>-16GB RAM<br>-1TB SSD | -DataGrip 2022.2.2<br>-IntelliJ IDEA 2022.2.3<br>-PyCharm 2022.2.1<br>-jdk 17.0.4.1+7-b469.62 amd64<br>-Python 3.9 | Localhost | data.csv from sakai |

#### Import Scripts

```java
try (BufferedReader infile = new BufferedReader(new FileReader(fileName))) {
            long start, end;
            int cnt = 0;
            String line;
            String[] parts;
            String item_name, item_type;
            String retrieval_city, retrieval_courier, retrieval_courier_gender, retrieval_courier_phone_number;
            String delivery_city, delivery_courier, delivery_courier_gender, delivery_courier_phone_number;
            String item_export_city, item_import_city, container_code, container_type, ship_name, company_name;
            Integer item_price, retrieval_courier_age, delivery_courier_age;
            //Integer是对象，可以设为null，而int不可设为null
            double item_export_tax, item_import_tax;
            Date retrieval_start_time, delivery_finish_time, item_export_time, item_import_time;
            Timestamp log_time;
            openDB();//preStatement语句在openDB中已经写好了
            Statement clear;
            if (con != null) {
                clear = con.createStatement();
                //清空表
                clear.execute("truncate table company,ship,courier,city,item,container,company_ship_detail,company_courier_detail,berth,orders");
                clear.close();
            }
            start = System.currentTimeMillis();
            while ((line = infile.readLine()) != null) {
                cnt++;
                if (cnt >= 2){
                    System.out.println(cnt);
                    parts = line.split(",");
                    //csv文件以逗号分隔，用字符串操作取出数据
                    item_name = parts[0];
                    item_type = parts[1];
                    item_price = Integer.parseInt(parts[2]);
                    retrieval_city = parts[3];
                    retrieval_start_time = Date.valueOf(parts[4]);

                    retrieval_courier = parts[5];
                    retrieval_courier_gender = parts[6];
                    retrieval_courier_phone_number = parts[7];
                    retrieval_courier_age = Integer.parseInt(parts[8]);
                    //可能为空的列在为空时设为null，非空时才可以使用valueof转化为Date对象，余下同理
                    if(!parts[9].equals(""))
                    {
                        delivery_finish_time = Date.valueOf(parts[9]);
                    }
                    else
                    {
                        delivery_finish_time = null;
                    }
                    delivery_city = parts[10];
                    //插入数据
                    try {
                        load_company(company_name);//向preStatement中插入信息
                    } catch (SQLException sqlException) {//调试时这里可以getmessage以获取报错信息}
                    //其余各表操作同理
                }
            }
            closeDB();
            end = System.currentTimeMillis();
            //计算加载速度
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
```

###### **Description**

Step 0(Prerequisites): Add the driver and connect to the database using IDEA. Put the csv file to the  java project directory.

Step 1: Setup the username, password, etc in the script. The script will be ready to run then.

Step 2: The working principle of the import is to divide every row of the csv file by commas and load the processed informations into the database by using preStatements.

Cautions:

We used the file data2.csv instead of data.csv, which is slighly modified. Using the Excel formatting tools, we changed the format of date and time in the original file, making it easier for the Java's time functions to work.


###### **Performance**

![v1](C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\v1.png)

### Advanced Requirements

#### Different Approach

We uses both java and python to import 10k rows of test data to get a comparison on their efficiencies. Further comparison on more aspects can be seen in advanced requirement 5 in task4. The test data is in the file "data10000.csv". Using more test data will only enlarge the time cost difference. So we consider that 10k rows of data is enough to reveal their performance.

 

<center class="half">
   <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\Comparison on Different Approach.PNG" style="zoom:13%;"/>
</center>


As we can see from the results, Python takes longer time to insert the same amount of data into the database. We uses the package "Pandas" in Python. However, we didn't make any optimization to Python import while batch processing is used in Java import. So we couldn't jump to the conclusion that Java has higher computational efficiency than Python.

#### Optimization

**Revised Version 1**

```java
int Batch_size=500;
        try (BufferedReader infile = new BufferedReader(new FileReader(fileName))) {
            HashSet<String> set_company = new HashSet<>();
            HashSet<String> set_ship = new HashSet<>();
            HashSet<String> set_courier = new HashSet<>();
            HashSet<String> set_city = new HashSet<>();
            HashSet<String> set_item = new HashSet<>();
            HashSet<String> set_container = new HashSet<>();
            HashSet<String> set_company_courier= new HashSet<>();
            HashSet<String> set_company_ship= new HashSet<>();
            HashSet<String> set_berth = new HashSet<>();
            //使用hashset处理unique数据，保证加入批的数据不重复
            //数据预处理与最初版本相同
            //循环读取数据
            start = System.currentTimeMillis();
            while ((line = infile.readLine()) != null) {
                cnt++;
                if (cnt >= 2)
                {
                    //处理数据同最初版本相同
                    //判断数据是否存在于hashset中，若不存在，扔进set，并加⼊batch
                    //table "company"
                    if(!set_company.contains(company_name))
                    {
                        if (to_company != null) {
                            to_company.setString(1, company_name);
                            to_company.addBatch();
                            set_company.add(company_name);
                        }
                    }
                    //余下各表同理
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
```

###### **Description**

Hashsets and batch processing is applied. Hashsets are used for the duplicate removal work, also avoiding the SQLException for repeatedly inserting identical informations into unique columns. Batch processing will send the precompiled sql statements to the database, making the process more effective.

###### **Performance**

<center class="half">
    <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\v2.png" alt="v2" style="zoom:100%;"/>
</center>


**Revised Version 2**

```java
//加大工作内存，调用多线程等硬件方面优化
private static void multithread() {
        try {
            con.createStatement().execute("alter system set maintenance_work_mem = 214748364");
            con.createStatement().execute("alter system set max_parallel_maintenance_workers = 8");
            con.createStatement().execute("alter system set fsync = off");
            con.createStatement().execute("alter system set max_wal_size = 214748364");
            con.createStatement().execute("alter system set checkpoint_timeout = 7200");
            con.createStatement().execute("set constraints all deferred");
        } catch (SQLException e) {
            System.err.println("Remove constraints failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
//寻找最优的batchsize
private static void testbatchsize(){
    int L=1,R=10000;
        int cnt=0;
        while(L<R)
        {
            cnt++;
            if(cnt>20)
            {
                break;
            }
            int Lmid=Math.min(R,L+(R-L)/3);
            int Rmid=Math.max(L,R-(R-L)/3);
            if(imp2(Rmid)>imp2(Lmid))
            {
                System.out.println("betterSiz:"+Lmid);
                R=Rmid;
            }
            else
            {
                System.out.println("betterSiz:"+Rmid);
                L=Lmid;
            }
        }
        System.out.println("Siz:"+L);
}     
```

###### **Description**

In revised version 2, we added the "multithread" function to revised version 1 so as to maximize the working memory, working thread and so on. By doing so, we optimized the import function from hardware level.

Also, after several tests, we found that different batch size may be possible to accelerate the import, and we assume the loading speed can be approximated as a unimodalfunction about batch size. So we tried the method of tripartition to find a better batch size with a lowerbound of 1 and an upperbound 10000 as batch size. The test results are averagely between 800 and 900 to be the best size for our table structure. However, since the loading speed is super highly influenced by the frequently fluctuating computer status, this result actually possesses no generality, providing very slight or even no acceleration for the loading speed depending on the running environment, so this optimization end up providing no improvement.

###### Performance

![v3](C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\v3.png)

**Comparison**

**Original Version**

- Prepared SQL statements

**Revised Version 1** [+767.16%]

- Batch Processing

**Revised Version 2** [+1860.36%]

- Batch Processing with multithread and best batch size

<center class="half">
    <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\time cost.png" alt="time cost" style="zoom:4%;"/>
    <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\speed.png" alt="time cost" style="zoom:4%;"/>
</center>



**Notice**: It is confirmed that all data have been imported into the database completely and correctly in all versions of import.

![correct](C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\correct.png)

## Task 4: Compare DBMS with File I/O

#### General Requirements

1. **Description of test environment**

- Hardware Specification

  Model Name: OMEN by HP Laptop 17-ck1xxx

  CPU:   12th Gen Intel(R) Core(TM) i7-12800HX   2.00 GHz

  RAM:  16GB GDDR5 4800MHz

  SSD:   1TB SAMSUNG PM9A1 

  SSD Performance: 

  [Read]
    SEQ    1MiB (Q=  8, T= 1):  6616.280 MB/s [   6309.8 IOPS] <  1266.53 us>
    SEQ    1MiB (Q=  1, T= 1):  3576.065 MB/s [   3410.4 IOPS] <   293.01 us>
    RND    4KiB (Q= 32, T= 1):   586.181 MB/s [ 143110.6 IOPS] <   216.94 us>
    RND    4KiB (Q=  1, T= 1):    74.399 MB/s [  18163.8 IOPS] <    54.95 us>

  [Write]
    SEQ    1MiB (Q=  8, T= 1):  4724.168 MB/s [   4505.3 IOPS] <  1771.55 us>
    SEQ    1MiB (Q=  1, T= 1):  2642.186 MB/s [   2519.8 IOPS] <   396.00 us>
    RND    4KiB (Q= 32, T= 1):   449.387 MB/s [ 109713.6 IOPS] <   290.89 us>
    RND    4KiB (Q=  1, T= 1):   151.118 MB/s [  36894.0 IOPS] <    27.00 us>

- Software Specification

  DBMS: PostgreSQL 14

  Operating System: Windows 11 22H2

  Programming Language: Java, PostgreSQL, python

  Version of Language: Jdk 17.0.4, Python 3.9

  Development Environment: DataGrip 2022.2.2, IntelliJ IDEA 2021.2.1, Pycharm 2022.2.1

- Requirement to Operate the Project

  Under most circumstances, a laptop published within 3 years with other settings and software version remaining identical can operate the project easily. However, please be aware that the performance might differ due to hardware difference. Also, the data are tested with the laptop plug into its original 330W power supply. You might see obvious difference on performance if the laptop isn't getting enough power.

2. **Specification**
   INSERT: For the insertion, we choose to insert the first 30000 rows of the original file to the database/file.
   DELETE: We delete all the orders with log time in the year 2016.
   UPDATE: We updated all the empty delivery courier into a random person.
   QUERY: Query all the orders with log time in the year 2017.

3. **SQL script**

```postgresql
INSERT: 
the same as task3

DELETE:
delete from orders where log_time between '2016-01-01 00:00:00' and '2016-12-31 23:59:59'

UPDATE:
insert into courier values (0,'尊尼获加','男','12306',3);
update orders set delivery_courier='尊尼获加' where delivery_courier is null;

QUERY:
select log_time,item_name,ship_name from orders where log_time between '2016-01-01 00:00:00' and '2016-12-31 23:59:59'
```


4. **Comparison**

   <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\JavaDBMS.png" align=center>

   

   <center> Java DBMS Manipulation </center>

   <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\JavaFile.png" style="zoom:22%;">

   <center>Java File Manipulation</center>

 <center class="half">
     <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\File&DBMS.png"  align="center" style="zoom:6%;"/>
 </center>




#### Detailed Advanced Requirements 

1. In this case the script will query and output all the containers' code, type and how many years they have serviced. To make the output shorter, we choose to only output the ones that have served for over 5 years. The file manipulation program can done this as well, but in a slower speed. In DBMSmanipulation and Filemanipulation folders, you may find the java program and press "5" to see the results for this question. 

2. In this case the script will query and output the most diligent retrieval/delivery courier in each city. The performances of file I/O and DBMS are almost the same. For the file I/O part we used a nesting Hashmap to achieve the double keyword query, which can be optimized. In DBMSmanipulation and Filemanipulation folders, you may find the java program and press "6" to see the results for this question. 

3. In this case the script will query and output the city that possess the minimum tax rate for each type of items. Tax rate here denotes the quotient of the item export tax and the item price, which we found is a constant for the same type of item in the same city. In this case, the file I/O program has a better performance than the DBMS. In DBMSmanipulation and Filemanipulation folders, you may find the java program and  press "7" to see the results for this question. 

4. To deal with high concurrency, we can use the statement below.

   ```postgresql
   SET default_transaction_isolation = 'repeatable read';
   ```

   The transaction will fail if a change of data happens during the transaction, this helps with the program to make up-to-date judgements.

5. In this part, we use a laptop running on Windows 11 and a DIY windows desktop running on Windows 10, since none of us got a mac or a linux based computer. We write insert, query, delete, update actions in both python and java so as to get a comparison on the time they use to process the data.

   **Environment**

   Windows Laptop:

   | Operating System | Hardware                               | Software                                                     | Internet  | Data                          |
   | ---------------- | -------------------------------------- | ------------------------------------------------------------ | --------- | ----------------------------- |
   | Windows 11 22H2  | -i7 12800HX <br>-16GB RAM <br>-1TB SSD | -DataGrip 2022.2.2 <br>-IntelliJ IDEA 2022.2.3 <br>-PyCharm 2022.2.1<br>-jdk 17.0.4.1+7-b469.62 amd64 <br>-Python 3.9 | Localhost | data10000.csv from the folder |

   Windows Desktop:

   | Operating System | Hardware                                  | Software                                                     | Internet  | Data                          |
   | ---------------- | ----------------------------------------- | ------------------------------------------------------------ | --------- | ----------------------------- |
   | Windows 10 21H1  | -Ryzen7 5800x  <br>-32GB RAM <br>-1TB SSD | -DataGrip 2022.2.2  <br>-IntelliJ IDEA 2022.2.3  <br>-PyCharm 2022.2.1 <br>-jdk 17.0.4.1+7-b469.62 amd64  <br>-Python 3.9 | Localhost | data10000.csv from the folder |

   **Performance**

   Windows Laptop

   <center class="half">
       <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\javaDBMS(compare).png" style="">
       <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\java_File.png" style="zoom:101%;">
       <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\pythonDBMS.png" style="zoom:93%;">
       <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\python_File.png" style="zoom:93%;">
   </center>


   Windows Desktop

   <center class="half">
       <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\jd.png" style="">
       <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\jf.png" style="zoom:100%;">
       <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\pd.png" style="zoom:100%;">
       <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\pf.png" style="zoom:100%;">
   </center>


   **Comparison**

   <center class="half">
       <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\Laptop.png"  style="zoom:15%;"/>
       <img src="C:\Users\86386\Desktop\SUSTech\CS307\project\Profect1\Pictures\Desktop.png" style="zoom:15%;"/>
   </center>


   **Reflection**

   As we can easily see from the graph, for insert operation, file manipulation is much faster than DBMS manipulation. When it comes to query, update and delete, DBMS manipulation through java is faster than file manipulation through java. DBMS is built to conduct operations among metadata while file is built for metadata storage. For insert operation only, DBMS has to first build connection, then analyze SQL  statements in RAM, and finally make it a file and input. Meanwhile, file is already a file, no need for indexing or other special operations, but file isn't good at dealing with metadata. So it make sense that file I/O out perform DBMS in inserting data while DBMS has higher efficiency in query, update and delete. 

   Another thing we can easily observe is that in all operations through the same way python is slower than java. We didn't make any optimization for our python programs. As one of hottest third-party package in data analysis, Pandas can be optimized through many ways. For instance, using multithread and closing auto commit can accelerate Pandas' import speed. Then, I will consider that python is a swollen programming language for compiling. To be clear, I have no discrimination on python. On the contrast, I enjoy python's high usability in deploying AI project. So it is hard to make the conclusion that python is slower than java in data operations by the data we have so far.
