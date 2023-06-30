SET TIME ZONE 'UTC';

CREATE TABLE "user" (
    "id" SERIAL,
    "username" TEXT NOT NULL,
    "password" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    "first_name" TEXT NOT NULL,
    "last_name" TEXT NOT NULL,
    "date_of_birth" DATE NOT NULL,
    "description" TEXT DEFAULT NULL,
    "confirmed" BOOLEAN NOT NULL DEFAULT FALSE,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT user_pkey PRIMARY KEY("id")
);

CREATE UNIQUE INDEX user_username_un_idx on "user" ("username");
CREATE UNIQUE INDEX user_email_un_idx on "user"(LOWER("email"));
CREATE INDEX user_confirmed_idx ON "user"("confirmed") WHERE "confirmed" = FALSE;

CREATE TABLE "role" (
    "id" SERIAL,
    "role" TEXT NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT role_pkey PRIMARY KEY("id")
);

CREATE UNIQUE INDEX role_un_idx on "role" ("role");

CREATE TABLE "permission" (
    "id" SERIAL,
    "permission" TEXT NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT permission_pkey PRIMARY KEY("id")
);

CREATE UNIQUE INDEX permission_un_idx on "permission" ("permission");

CREATE TABLE "role_permission" (
    "role_id" INTEGER NOT NULL,
    "permission_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT role_permission_pkey PRIMARY KEY("role_id", "permission_id")
);

CREATE TABLE "user_role" (
    "user_id" INTEGER NOT NULL,
    "role_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT user_role_pkey PRIMARY KEY("user_id", "role_id")
);

CREATE TABLE "group" (
    "id" SERIAL,
    "name" TEXT NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT group_pkey PRIMARY KEY("id")
);

CREATE UNIQUE INDEX group_name_un_idx on "group" ("name");

CREATE TABLE "user_group_role" (
    "user_id" INTEGER NOT NULL,
    "group_id" INTEGER NOT NULL,
    "role_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT user_group_role_pkey PRIMARY KEY("user_id", "group_id", "role_id")
);