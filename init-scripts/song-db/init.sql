create table if not exists public.songs (
   id integer primary key,
   "name" varchar(255),
   artist varchar(255),
   album varchar(255),
   duration varchar(50),
   "year" varchar(10)
);
