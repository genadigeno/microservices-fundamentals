create sequence if not exists public.song_resources_sequence
    increment by 1
    start with 1
    no cycle ;

create table if not exists public.song_resources (
   id integer primary key default nextval('public.song_resources_sequence'),
   location varchar(64)
);

alter table public.song_resources add file_state varchar(255) not null default 'STAGING';
alter table public.song_resources add bucket_name varchar(255);