create table if not exists public.storage_objects (
   id serial primary key,
   storage_type varchar(255) not null,
   bucket varchar(255) not null,
   "path" varchar(255) not null
);

insert into public.storage_objects (storage_type, bucket, "path") values
    ('STAGING', 'staging', 'files'),
    ('PERMANENT', 'permanent', 'files');