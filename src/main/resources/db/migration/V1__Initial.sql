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
    "profile_picture_key" TEXT DEFAULT NULL,
    "confirmed" BOOLEAN NOT NULL DEFAULT FALSE,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT user_pkey PRIMARY KEY("id")
);

CREATE UNIQUE INDEX user_username_un_idx on "user" ("username");
CREATE UNIQUE INDEX user_email_un_idx on "user"(LOWER("email"));

-- todo add scheduler to delete all unconfirmed users 1 month after creation

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
    CONSTRAINT role_permission_pkey PRIMARY KEY("role_id", "permission_id"),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
);

CREATE TABLE "user_role" (
    "user_id" INTEGER NOT NULL,
    "role_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT user_role_pkey PRIMARY KEY("user_id", "role_id"),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
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

CREATE TABLE "group_request" (
    "group_id" INTEGER NOT NULL,
    "user_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT group_request_pkey PRIMARY KEY("group_id", "user_id"),
    FOREIGN KEY (group_id) REFERENCES group(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE "user_group" (
    "id" SERIAL,
    "user_id" INTEGER NOT NULL,
    "group_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT user_group_role_pkey PRIMARY KEY("id"),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES group(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX user_group_un_idx on "user_group" ("user_id", "group_id");

CREATE TABLE "user_group_role" (
    "user_group_id" INTEGER NOT NULL,
    "role_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT user_group_role_pkey PRIMARY KEY("user_group_id", "role_id"),
    FOREIGN KEY (user_group_id) REFERENCES user_group(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

CREATE TABLE "friend_request" (
    "sender_id" INTEGER NOT NULL,
    "recipient_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT friend_request_pkey PRIMARY KEY("recipient_id", "sender_id"),
    FOREIGN KEY (sender_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE "friend" (
    "id" SERIAL,
    "first_user_id" INTEGER NOT NULL,
    "second_user_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT friend_pkey PRIMARY KEY("id"),
    FOREIGN KEY (first_user_id) REFERENCES user(id),
    FOREIGN KEY (second_user_id) REFERENCES user(id)
);

CREATE UNIQUE INDEX friend_un_idx on "direct_message_like" ("first_user_id", "second_user_id");

CREATE TABLE "direct_message" (
    "id" SERIAL,
    "message" TEXT,
    "friend_id" INTEGER NOT NULL ON DELETE CASCADE,
    "sender_id" INTEGER NOT NULL ON DELETE CASCADE,
    "recipient_id" INTEGER NOT NULL ON DELETE CASCADE,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT direct_message_pkey PRIMARY KEY("id"),
    FOREIGN KEY (sender_id) REFERENCES user(id),
    FOREIGN KEY (recipient_id) REFERENCES user(id)
);

CREATE TABLE "group_message" (
    "id" SERIAL,
    "message" TEXT,
    "group_id" INTEGER NOT NULL ON DELETE CASCADE,
    "sender_id" INTEGER NOT NULL ON DELETE CASCADE,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT group_message_pkey PRIMARY KEY("id"),
    FOREIGN KEY (group_id) REFERENCES group(id),
    FOREIGN KEY (sender_id) REFERENCES user(id)
);

CREATE TABLE "post" (
    "id" SERIAL,
    "media_key" TEXT,
    "media_type" TEXT,
    "text" TEXT,
    "user_id" INTEGER NOT NULL ON DELETE CASCADE,
    "group_id" INTEGER NOT NULL ON DELETE CASCADE,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT post_pkey PRIMARY KEY("id"),
    FOREIGN KEY (sender_id) REFERENCES user(id),
    FOREIGN KEY (recipient_id) REFERENCES user(id)
);

CREATE TABLE "comment" (
    "id" SERIAL,
    "text" TEXT,
    "user_id" INTEGER NOT NULL ON DELETE CASCADE,
    "post_id" INTEGER NOT NULL ON DELETE CASCADE,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT comment_pkey PRIMARY KEY("id"),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (post_id) REFERENCES post(id)
);

CREATE TABLE "direct_message_like" (
    "id" SERIAL,
    "direct_message_id" INTEGER NOT NULL ON DELETE CASCADE,
    "user_id" INTEGER NOT NULL ON DELETE CASCADE,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT direct_message_like_pkey PRIMARY KEY("id"),
    FOREIGN KEY (direct_message_id) REFERENCES direct_message(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE UNIQUE INDEX direct_message_like_un_idx on "direct_message_like" ("direct_message_id", "user_id");

CREATE TABLE "group_message_like" (
    "id" SERIAL,
    "group_message_id" INTEGER NOT NULL ON DELETE CASCADE,
    "user_id" INTEGER NOT NULL ON DELETE CASCADE,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT group_message_like_pkey PRIMARY KEY("id"),
    FOREIGN KEY (group_message_id) REFERENCES group_message(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE UNIQUE INDEX group_message_like_un_idx on "group_message_like" ("group_message_id", "user_id");

CREATE TABLE "post_like" (
    "id" SERIAL,
    "post_id" INTEGER NOT NULL ON DELETE CASCADE,
    "user_id" INTEGER NOT NULL ON DELETE CASCADE,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT post_like_pkey PRIMARY KEY("id"),
    FOREIGN KEY (post_id) REFERENCES post(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE UNIQUE INDEX post_like_un_idx on "post_like" ("post_id", "user_id");