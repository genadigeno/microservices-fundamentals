DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'songs') THEN
      CREATE DATABASE songs;
END IF;
END $$;
