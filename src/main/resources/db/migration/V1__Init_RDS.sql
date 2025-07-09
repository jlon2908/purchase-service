-- PostgreSQL database schema for purchasedb, compatible for AWS RDS import

-- Habilitar la extensión uuid-ossp si está disponible (en RDS suele estarlo)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

-- Tablas
CREATE TABLE public.payment (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    purchase_id uuid NOT NULL,
    payment_method character varying(50) NOT NULL,
    amount numeric(10,2) NOT NULL,
    payment_date timestamp without time zone,
    status character varying(50) NOT NULL,
    transaction_code character varying(100)
);

CREATE TABLE public.purchase (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    order_code character varying(100) NOT NULL,
    client_id uuid NOT NULL,
    warehouse_pickup character varying(100) NOT NULL,
    purchase_date timestamp without time zone DEFAULT now() NOT NULL,
    status character varying(50) NOT NULL,
    total_amount numeric(10,2) NOT NULL
);

CREATE TABLE public.purchase_item (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    purchase_id uuid NOT NULL,
    sku character varying(100) NOT NULL,
    quantity integer NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    subtotal numeric(10,2) NOT NULL
);

CREATE TABLE public.purchase_item_distribution (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    purchase_item_id uuid NOT NULL,
    warehouse_code character varying(100) NOT NULL,
    quantity integer NOT NULL
);

CREATE TABLE public.shipping_address (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    purchase_id uuid NOT NULL,
    street character varying(255) NOT NULL,
    city character varying(100) NOT NULL,
    state character varying(100) NOT NULL,
    country character varying(100) NOT NULL,
    notes text
);

-- Índices y llaves primarias
ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT purchase_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.purchase
    ADD CONSTRAINT purchase_order_code_key UNIQUE (order_code);

ALTER TABLE ONLY public.purchase_item
    ADD CONSTRAINT purchase_item_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.purchase_item_distribution
    ADD CONSTRAINT purchase_item_distribution_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.shipping_address
    ADD CONSTRAINT shipping_address_pkey PRIMARY KEY (id);

-- Llaves foráneas
ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_purchase_id_fkey FOREIGN KEY (purchase_id) REFERENCES public.purchase(id) ON DELETE CASCADE;

ALTER TABLE ONLY public.purchase_item
    ADD CONSTRAINT purchase_item_purchase_id_fkey FOREIGN KEY (purchase_id) REFERENCES public.purchase(id) ON DELETE CASCADE;

ALTER TABLE ONLY public.purchase_item_distribution
    ADD CONSTRAINT purchase_item_distribution_purchase_item_id_fkey FOREIGN KEY (purchase_item_id) REFERENCES public.purchase_item(id) ON DELETE CASCADE;

ALTER TABLE ONLY public.shipping_address
    ADD CONSTRAINT shipping_address_purchase_id_fkey FOREIGN KEY (purchase_id) REFERENCES public.purchase(id) ON DELETE CASCADE;

-- Fin del esquema