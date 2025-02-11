create sequence if not exists public.song_resources_sequence
    increment by 1
    start with 1
    no cycle ;

create table if not exists public.song_resources (
   id integer primary key default nextval('public.song_resources_sequence'),
   data bytea
);
