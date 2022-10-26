create table ship
(
   ship_id serial primary key,

   ship_name varchar unique not null
);

create table courier
(
    courier_id serial primary key,

    courier_name varchar unique not null,
    gender varchar not null,
    phone_number varchar not null,
    age integer not null
);

create table company
(
    company_id serial primary key,

    company_name varchar unique not null
);

create table company_ship_detail
(
    company_name varchar references company(company_name),
    ship_name varchar references ship(ship_name),
    primary key (company_name,ship_name)
);

create table company_courier_detail
(
    company_name varchar references company(company_name),
    courier_name varchar references courier(courier_name),
    primary key (company_name,courier_name)
);

create table item
(
    item_id serial primary key,
    item_name varchar unique not null,
    item_type varchar not null,
    item_price integer not null,
    import_tax numeric not null,
    export_tax numeric not null
);

create table container
(
    container_code varchar primary key,
    container_type varchar not null
);

create table city
(
    city_id serial primary key,

    city_name varchar unique not null
);

create table berth
(
    berth_id serial primary key,

    ship_name varchar references ship(ship_name),
    city_name varchar references city(city_name)
);

create table orders
(
    order_id serial primary key,

    log_time timestamp not null,
    import_time date,
    export_time date,
    retrieval_start_time date not null,
    delivery_finish_time date,
    item_name varchar not null references item(item_name),
    container_code varchar references container(container_code),
    ship_name varchar references ship(ship_name),
    retrieval_courier varchar references courier(courier_name),
    delivery_courier varchar references courier(courier_name),
    retrieval_city varchar references city(city_name),
    delivery_city varchar references city(city_name),
    import_city varchar references city(city_name),
    export_city varchar references city(city_name)
);