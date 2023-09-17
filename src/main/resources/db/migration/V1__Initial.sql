SET TIME ZONE 'UTC';

CREATE TABLE "user" (
    "id" SERIAL,
    "username" TEXT NOT NULL,
    "password_hash" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    "first_name" TEXT NOT NULL,
    "last_name" TEXT NOT NULL,
    "date_of_birth" DATE NOT NULL,
    "description" TEXT DEFAULT NULL,
    "profile_picture_key" TEXT DEFAULT NULL,
    "profile_picture_thumbnail_key" TEXT DEFAULT NULL,
    "verified" BOOLEAN NOT NULL DEFAULT FALSE,
    "token_issued_at" INTEGER,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT user_pkey PRIMARY KEY("id")
);

CREATE UNIQUE INDEX user_username_un_idx on "user" ("username");

-- todo add scheduler to delete all unconfirmed users 1 month after creation

CREATE TABLE "pending_verification" (
    "id" SERIAL,
    "verification_type" TEXT NOT NULL,
    "user_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT pending_verification_pkey PRIMARY KEY("id"),
    FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX user_username_verification_type_un_idx on "pending_verification" ("user_id", "verification_type");

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
    FOREIGN KEY (role_id) REFERENCES "role"(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES "permission"(id) ON DELETE CASCADE
);

CREATE TABLE "group" (
    "id" SERIAL,
    "name" TEXT NOT NULL,
    "profile_picture_key" TEXT DEFAULT NULL,
    "profile_picture_thumbnail_key" TEXT DEFAULT NULL,
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
    FOREIGN KEY (group_id) REFERENCES "group"(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

CREATE TABLE "user_group" (
    "id" SERIAL,
    "user_id" INTEGER NOT NULL,
    "group_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT user_group_pkey PRIMARY KEY("id"),
    FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES "group"(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX "user_group_un_idx" ON "user_group" ("user_id", "group_id");

CREATE TABLE "user_group_role" (
    "user_group_id" INTEGER NOT NULL,
    "role_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT user_group_role_pkey PRIMARY KEY("user_group_id", "role_id"),
    FOREIGN KEY (user_group_id) REFERENCES "user_group"(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES "role"(id) ON DELETE CASCADE
);

CREATE TABLE "friend_request" (
    "sender_id" INTEGER NOT NULL,
    "recipient_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT friend_request_pkey PRIMARY KEY("recipient_id", "sender_id"),
    FOREIGN KEY (sender_id) REFERENCES "user"(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES "user"(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX friend_request_sender_un_idx on "friend_request" ("sender_id", "recipient_id");

CREATE TABLE "friend" (
    "id" SERIAL,
    "first_user_id" INTEGER NOT NULL,
    "second_user_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT friend_pkey PRIMARY KEY("id"),
    FOREIGN KEY (first_user_id) REFERENCES "user"(id),
    FOREIGN KEY (second_user_id) REFERENCES "user"(id)
);

CREATE UNIQUE INDEX friend_first_un_idx on "friend" ("first_user_id", "second_user_id");
CREATE UNIQUE INDEX friend_second_un_idx on "friend" ("second_user_id", "first_user_id");

CREATE TABLE "direct_message" (
    "id" SERIAL,
    "message" TEXT,
    "friend_id" INTEGER NOT NULL,
    "sender_id" INTEGER NOT NULL,
    "recipient_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT direct_message_pkey PRIMARY KEY("id"),
    FOREIGN KEY (friend_id) REFERENCES "friend"(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES "user"(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES "user"(id) ON DELETE CASCADE
);

CREATE TABLE "group_message" (
    "id" SERIAL,
    "message" TEXT,
    "group_id" INTEGER NOT NULL,
    "sender_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT group_message_pkey PRIMARY KEY("id"),
    FOREIGN KEY (group_id) REFERENCES "group"(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES "user"(id) ON DELETE CASCADE
);

CREATE TABLE "post" (
    "id" SERIAL,
    "media_key" TEXT,
    "media_thumbnail_key" TEXT DEFAULT NULL,
    "text" TEXT,
    "user_id" INTEGER NOT NULL,
    "group_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT post_pkey PRIMARY KEY("id"),
    FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES "group"(id) ON DELETE CASCADE
);

CREATE TABLE "comment" (
    "id" SERIAL,
    "text" TEXT,
    "user_id" INTEGER NOT NULL,
    "post_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT comment_pkey PRIMARY KEY("id"),
    FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES "post"(id) ON DELETE CASCADE
);

CREATE TABLE "post_like" (
    "post_id" INTEGER NOT NULL,
    "user_id" INTEGER NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT post_like_pkey PRIMARY KEY("post_id", "user_id"),
    FOREIGN KEY (post_id) REFERENCES "post"(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX post_like_un_idx on "post_like" ("post_id", "user_id");

CREATE TABLE "mail_message" (
    "id" SERIAL,
    "subject" TEXT,
    "html_message" TEXT NOT NULL,
    "txt_message" TEXT NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT mail_message_pkey PRIMARY KEY(id)
);

CREATE TABLE "mail" (
    "id" SERIAL,
    "recipient_id" INTEGER NOT NULL,
    "mail_message_id" INTEGER NOT NULL,
    "sender_id" INTEGER NOT NULL,
    "mail_status" CHARACTER VARYING(255) NOT NULL,
    "expires" timestamp(0) with time zone NOT NULL,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT mail_pkey PRIMARY KEY(id),
    FOREIGN KEY (recipient_id) REFERENCES "user"(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES "user"(id) ON DELETE CASCADE,
    FOREIGN KEY (mail_message_id) REFERENCES "mail_message"(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX mail_mail_message_recipient_sender_un ON "mail"("mail_message_id", "recipient_id", "sender_id");
CREATE INDEX mail_mail_status_idx ON "mail"("mail_status");

CREATE TABLE "mail_exception_log" (
    "mail_id" INTEGER,
    "message" TEXT,
    "stack_trace" TEXT,
    "created_by" CHARACTER VARYING(255) NOT NULL,
    "modified_by" CHARACTER VARYING(255),
    "created_ts" timestamp(0) with time zone NOT NULL,
    "modified_ts" timestamp(0) WITH TIME ZONE,
    CONSTRAINT mail_exception_log_pkey PRIMARY KEY(mail_id),
    FOREIGN KEY (mail_id) REFERENCES "mail"(id) ON DELETE CASCADE
);